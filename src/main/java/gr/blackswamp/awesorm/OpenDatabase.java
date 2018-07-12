package gr.blackswamp.awesorm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class OpenDatabase extends SQLiteOpenHelper implements IDBConnection {
    OpenDatabase(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public Cursor rawQuery(boolean writable, String sql, String[] selectionArgs) {
        return get_database(writable).rawQuery(sql, selectionArgs);
    }

    @Override
    public void execSQL(boolean writeable, String query, Object[] args) {
        if (args == null)
            get_database(writeable).execSQL(query);
        else
            get_database(writeable).execSQL(query, args);
    }

    @Override
    public void beginTransaction() {
        get_database(true).beginTransaction();
    }

    @Override
    public boolean inTransaction() {
        return get_database(true).inTransaction();
    }

    @Override
    public void commitTransaction() {
        get_database(true).setTransactionSuccessful();
        get_database(true).endTransaction();
    }

    @Override
    public void rollbackTransaction() {
        get_database(true).endTransaction();
    }

    private SQLiteDatabase get_database(boolean writable) {
        return writable ? getWritableDatabase() : getReadableDatabase();
    }

    public interface transaction {
        void execute(IDBConnection db);
    }

}
