package gr.blackswamp.awesorm;


import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

class EncryptedDatabase extends SQLiteOpenHelper implements IDBConnection {
    private final String _password;

    public EncryptedDatabase(Context context, String name, String password) {
        super(context, name, null, 1);
        _password = password;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    @Override
    public Cursor rawQuery(boolean writeable, String sql, String[] selectionArgs) {
        return get_database(writeable).rawQuery(sql, selectionArgs);
    }

    @Override
    public void execSQL(boolean writeable, String query, Object[] args) {
        if (args == null)
            get_database(writeable).execSQL(query);
        else
            get_database(writeable).execSQL(query, args);
    }

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

    private SQLiteDatabase get_database(boolean writeable) {
        return writeable ? getWritableDatabase(_password) : getReadableDatabase(_password);
    }
}
