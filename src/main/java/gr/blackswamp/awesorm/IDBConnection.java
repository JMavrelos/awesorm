package gr.blackswamp.awesorm;

import android.database.Cursor;

interface IDBConnection {
    Cursor rawQuery(boolean writable, String sql, String[] selectionArgs);

    void execSQL(boolean writable, String query, Object[] args);

    void beginTransaction();

    boolean inTransaction();

    void commitTransaction();

    void rollbackTransaction();

    void close();
}
