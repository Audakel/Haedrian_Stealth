package com.haedrian.haedrian.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.haedrian.haedrian.Models.UserModel;

/**
 * Created by Logan on 3/24/2015.
 */
public class TableUsers {
    public static final String TABLE_USERS = "users";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PARSE_ID = "parse_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_WALLET_ID = "wallet_id";
    public static final String COLUMN_CREDIT_SCORE = "credit_score";


    public static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_USERS
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PARSE_ID + " TEXT, "
            + COLUMN_USERNAME + " TEXT, "
            + COLUMN_FIRST_NAME + " TEXT, "
            + COLUMN_LAST_NAME + " TEXT, "
            + COLUMN_PHONE_NUMBER + " TEXT, "
            + COLUMN_EMAIL + " TEXT, "
            + COLUMN_WALLET_ID + " INTEGER, "
            + COLUMN_CREDIT_SCORE + " INTEGER"
            + ");";

    private SQLiteOpenHelper openHelper;

    public TableUsers(SQLiteOpenHelper openHelper) {
        this.openHelper = openHelper;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    }

    public static void dropTable(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(database);
    }

    public UserModel insert(UserModel user) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PARSE_ID, user.getParseId());
        values.put(COLUMN_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_LAST_NAME, user.getLastName());
        values.put(COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_WALLET_ID, user.getWalletId());
        values.put(COLUMN_CREDIT_SCORE, user.getCreditScore());

        // Insert row
        db.insert(TABLE_USERS, null, values);

        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);

        if (cursor.moveToFirst()) {
            user.setId(cursor.getInt(0));
        }


        db.close();
        cursor.close();

        return user;

    }

    // For very simple queries
    public UserModel query(String column, String operator, String value) {
        UserModel user = new UserModel();

        String query = "SELECT * FROM " + TABLE_USERS
                + " WHERE "
                + column
                + " " + operator + " "
                + value + ";";

        SQLiteDatabase db = openHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            user.setId(cursor.getInt(0));
            user.setParseId(cursor.getString(1));
            user.setUsername(cursor.getString(2));
            user.setFirstName(cursor.getString(3));
            user.setLastName(cursor.getString(4));
            user.setPhoneNumber(cursor.getString(5));
            user.setEmail(cursor.getString(6));
            user.setWalletId(cursor.getInt(7));
            user.setCreditScore(cursor.getInt(8));
        }

        cursor.close();
        db.close();

        return user;
    }

}
