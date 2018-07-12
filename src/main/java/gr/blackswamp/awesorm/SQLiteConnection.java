package gr.blackswamp.awesorm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SQLiteConnection implements Closeable {
    private static final String TAG = SQLiteConnection.class.getSimpleName();
    private static final HashMap<String, TableMap> Mappings = new HashMap<>();
    IDBConnection connection;
    private Exception _last_error;

    SQLiteConnection(Context context, String database, String password) {
        if (password == null || password.isEmpty()) {
            connection = new OpenDatabase(context, database);
        } else {
            connection = new EncryptedDatabase(context, database, password);
        }
    }

    public Exception get_last_error() {
        return _last_error;
    }

    public void reset_last_error() {
        _last_error = null;
    }

    public boolean drop_table(Class clazz) {
        reset_last_error();
        TableMap mapping = get_mapping(clazz);
        if (mapping == null)
            throw new IllegalArgumentException(String.format("Error while retrieving mappings for %1$s", clazz.getName()));
        String sql = String.format("drop table if exists \"%1$s\"", mapping._name);
        try {
            connection.execSQL(true, sql, null);
            return true;
        } catch (SQLiteException se) {
            log_error(se);
            return false;
        }
    }

    public CreateTableResult create_table(Class clazz) throws IllegalArgumentException {
        reset_last_error();
        try {
            CreateTableResult result;
            TableMap mapping = get_mapping(clazz);
            //region create/migrate table
            if (mapping._columns.size() == 0)
                throw new IllegalArgumentException(String.format("Table %1$s does not have any columns\n You must declare at least one _field in the table with the Column Annotation", clazz.getName()));
            try (Cursor c = connection.rawQuery(false, String.format("pragma table_info (\"%1$s\")", mapping._name), null)) {
                if (c.getCount() == 0) {
                    result = CreateTableResult.Created;
                    create_table(mapping);
                } else {
                    result = CreateTableResult.Migrated;
                    migrate_table(mapping, c);
                }
            }
            //endregion
            //region create unique indexes
            create_index(mapping, true);
            //endregion
            //region create indexes
            create_index(mapping, false);
            //endregion
            return result;
        } catch (Exception e) {
            log_error(e);
            return CreateTableResult.Error;
        }
    }

    public long exec(String sql, Object... args) {
        reset_last_error();
        try {
            connection.execSQL(true, sql, args);
            try (Cursor cursor = connection.rawQuery(true, "SELECT changes()", null)) {
                return cursor.getLong(0);
            }
        } catch (Exception e) {
            log_error(e);
            return -1;
        }
    }

    private void create_table(TableMap mapping) {
        StringBuilder builder =
                new StringBuilder("create table if not exists `")
                        .append(mapping._name).append("` ( ");
        List<TableColumn> keys = new ArrayList<>();
        for (TableColumn col : mapping._columns) {
            builder.append(col.get_declaration()).append(",");
            if (col.pk)
                keys.add(col);
        }
        if (keys.size() == 1 && !keys.get(0).auto_increment) {
            builder.append(" PRIMARY KEY (").append(keys.get(0).name).append(") )");
        } else if (keys.size() > 1) {
            builder.append(" PRIMARY KEY (");
            for (TableColumn key : keys)
                builder.append(key.name).append(" ,");
            builder.deleteCharAt(builder.length() - 1).append(") )");
        } else {
            builder.deleteCharAt(builder.length() - 1).append(")");
        }
        Log.d(TAG, "create table: " + builder.toString());
        connection.execSQL(true, builder.toString(), null);
    }

    private void migrate_table(TableMap mapping, Cursor table_info) {
        List<TableColumn> to_be_added = new ArrayList<>();
        int name_idx = table_info.getColumnIndex("name");
        for (TableColumn column : mapping._columns) {
            boolean found = false;
            table_info.moveToFirst();

            while (!table_info.isAfterLast()) {
                if (table_info.getString(name_idx).toLowerCase().equals(column.name)) {
                    found = true;
                    break;
                }
                table_info.moveToNext();
            }
            if (!found)
                to_be_added.add(column);
        }
        for (TableColumn column : to_be_added) {
            String alter = "alter table \"" + mapping._name + "\" add column " + column.get_declaration();
            Log.d(TAG, "migrate table: " + alter);
            connection.execSQL(true, alter, null);
        }

    }

    private void create_index(TableMap mapping, boolean unique) {
        if (mapping == null)
            return;
        List<TableColumn> cols = mapping.get_indexed_columns(unique);
        if (cols.size() == 0)
            return;
        final StringBuilder sql = new StringBuilder("create ");
        if (unique)
            sql.append(" unique ");
        sql.append(" index if not exists ");
        if (unique)
            sql.append(" \"uq_");
        else
            sql.append(" \"idx_");
        sql.append(mapping._name)
                .append("\" on \"")
                .append(mapping._name)
                .append("\" (");
        for (TableColumn col : cols)
            sql.append("\"").append(col.name).append("\",");
        sql.deleteCharAt(sql.length() - 1).append(")");
        Log.d(TAG, "create_index:" + sql.toString());
        connection.execSQL(true, sql.toString(), null);
    }

    synchronized TableMap get_mapping(Class c) {
        String key = c.getName();
        if (Mappings.containsKey(key)) {
            return Mappings.get(key);
        } else {
            TableMap map = new TableMap(c);
            Mappings.put(key, map);
            return map;
        }
    }

    public <T> TableQuery<T> from(Class<T> cl) {
        return new TableQuery<T>(cl, get_mapping(cl), connection);
    }

    public void begin_transaction() {
        connection.beginTransaction();
    }

    public void commit_transaction() {
        connection.commitTransaction();
    }

    public boolean in_transaction() {
        return connection.inTransaction();
    }

    public void rollback_transaction() {
        connection.rollbackTransaction();
    }

    public <T extends DataObject> T save(Class<T> cl, T object) {
        reset_last_error();
        try {
            TableColumn auto_increment_col = null;
            TableMap map = get_mapping(cl);
            //<editor-fold desc="build insert into">
            StringBuilder builder = new StringBuilder(" insert or replace into `")
                    .append(map._name).append("` (");
            for (TableColumn col : map._columns) {
                if (!col.auto_increment)
                    builder.append('`').append(col.name).append("`,");
                else if (col.pk)
                    auto_increment_col = col;
            }
            builder.deleteCharAt(builder.length() - 1)
                    .append(") values (");
            //</editor-fold>
            //<editor-fold desc="build values part">
            for (TableColumn col : map._columns)
                if (!col.auto_increment)
                    builder.append(col.get_sql_val(object)).append(',');

            builder.deleteCharAt(builder.length() - 1)
                    .append(")");
            //</editor-fold>
            //<editor-fold desc="save the object">
            Log.d(TAG, "Save : " + builder.toString());
            connection.execSQL(true, builder.toString(), null);
            //</editor-fold>
            //<editor-fold desc="if there is an autoincrement key in the object, then load its value">
            if (auto_increment_col != null) {
                try (Cursor c = connection.rawQuery(false, "select last_insert_rowid();", null)) {
                    if (c != null && c.moveToFirst()) {
                        int id = c.getInt(0);
                        auto_increment_col.field.set(object, id);
                    }
                }
            }
            //</editor-fold>
            //<editor-fold desc="reload the object and return it">
            return load(cl, object);
            //</editor-fold>
        } catch (Exception e) {
            log_error(e);
            return null;
        }
    }

    public <T extends DataObject> boolean delete(Class<T> cl, T object) {
        reset_last_error();
        try {
            TableMap map = get_mapping(cl);
            List<TableColumn> keys = get_keys(map);
            if (keys.size() == 0)
                throw new IllegalStateException("an object from a table with no primary key cannot be deleted");
            StringBuilder builder = new StringBuilder(" delete from ")
                    .append(map._name)
                    .append(" where ");
            for (TableColumn col : keys)
                builder.append('`').append(col.name).append("` = ").append(col.get_sql_val(object)).append(" and ");
            builder.delete(builder.length() - 5, builder.length() - 1);
            Log.d(TAG, "Delete : " + builder.toString());
            connection.execSQL(true, builder.toString(), null);
            return true;
        } catch (Exception e) {
            log_error(e);
            return false;
        }
    }

    public <T extends DataObject> T load(Class<T> cl, T object) {
        reset_last_error();
        try {
            TableMap mapping = get_mapping(cl);
            List<TableColumn> keys = get_keys(mapping);
            StringBuilder builder = new StringBuilder("Select * from `").append(mapping._name).append("` WHERE ");
            for (TableColumn key : keys)
                builder.append("`").append(key.name).append("`= ").append(key.get_sql_val(object)).append(" AND ");
            builder = builder.delete(builder.length() - 5, builder.length() - 1);
            try (Cursor c = connection.rawQuery(false, builder.toString(), null)) {
                if (c == null || !c.moveToFirst())
                    throw new Exception("Object not found");
                object = cl.newInstance();
                for (TableColumn col : mapping._columns)
                    col.field.set(object, col.get_value_from(c));
            }
            return object;
        } catch (Exception e) {
            log_error(e);
            return null;
        }
    }

    private List<TableColumn> get_keys(TableMap map) {
        List<TableColumn> keys = new ArrayList<>();
        new ArrayList<>();
        for (TableColumn col : map._columns) {
            if (col.pk)
                keys.add(col);
        }
        return keys;
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            if (connection.inTransaction())
                connection.rollbackTransaction();
            connection.close();
            connection = null;
        }
    }

    private void log_error(Exception e) {
        e.printStackTrace();
        Log.e(TAG, e.getMessage(), e);
        _last_error = e;
    }
}
