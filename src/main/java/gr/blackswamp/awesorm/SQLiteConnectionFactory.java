package gr.blackswamp.awesorm;

import android.content.Context;

import java.io.File;


public class SQLiteConnectionFactory {

    private final Context _context;
    private final String _database;
    private final String _password;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public SQLiteConnectionFactory(Context context, String database) {
        this(context, database, null);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public SQLiteConnectionFactory(Context context, String database, String password) {
        File databaseFile = context.getDatabasePath(database);
//        databaseFile.mkdirs();
        _context = context;
        _database = database;
        _password = password;
    }

    public SQLiteConnection build() {
        return new SQLiteConnection(_context, _database, _password);
    }
}



