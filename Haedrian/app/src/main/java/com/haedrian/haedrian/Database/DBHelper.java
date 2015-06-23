package com.haedrian.haedrian.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Logan on 3/24/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "haedrian.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableUsers.onCreate(db);
        TableWallets.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TableUsers.onUpgrade(db, oldVersion, newVersion);
        TableWallets.onUpgrade(db, oldVersion, newVersion);
    }

    public TableWallets getWalletsTable() {
        return new TableWallets(this);
    }

    public TableUsers getUsersTable() {
        return new TableUsers(this);
    }
}
