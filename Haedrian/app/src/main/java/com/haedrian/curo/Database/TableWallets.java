package com.haedrian.curo.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.haedrian.curo.Models.WalletModel;

/**
 * Created by Logan on 3/24/2015.
 */
public class TableWallets {
    public static final String TABLE_WALLETS = "wallets";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_WALLET_ADDRESS = "wallet_address";
    public static final String COLUMN_BALANCE = "balance";

    public static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_WALLETS
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_ID + " INTEGER, "
            + COLUMN_WALLET_ADDRESS + " TEXT, "
            + COLUMN_BALANCE + " TEXT"
            + ");";

    private SQLiteOpenHelper openHelper;

    public TableWallets(SQLiteOpenHelper openHelper) {
        this.openHelper = openHelper;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }

    public void dropTables(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS" + TABLE_WALLETS);
        onCreate(database);
    }

    public WalletModel insert(WalletModel wallet) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_ID, wallet.getUserId());
        values.put(COLUMN_WALLET_ADDRESS, wallet.getAddress());
        values.put(COLUMN_BALANCE, wallet.getBalance());

        db.insert(TABLE_WALLETS, null, values);
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);

        if (cursor.moveToFirst()) {
            wallet.setId(cursor.getInt(0));
        }


        // Insert row
        db.close();
        cursor.close();

        return wallet;
    }

    public WalletModel selectByUserId(int userId) {

        WalletModel wallet = new WalletModel();

        String selectQuery = "SELECT * FROM " + TABLE_WALLETS
                + " WHERE " + COLUMN_USER_ID
                + " = " + userId;

        SQLiteDatabase db = openHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            wallet.setId(cursor.getInt(0));
            wallet.setUserId(cursor.getInt(1));
            wallet.setAddress(cursor.getString(2));
            wallet.setBalance(cursor.getString(3));
        }

        cursor.close();
        db.close();

        return wallet;
    }

}
