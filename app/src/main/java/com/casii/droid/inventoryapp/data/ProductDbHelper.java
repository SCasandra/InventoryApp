package com.casii.droid.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Casi on 15.07.2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "inventory.db";
    private static final int DB_VERSION = 4;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + "(" +
                    ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY," +
                    ProductContract.ProductEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    ProductContract.ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL," +
                    ProductContract.ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL," +
                    ProductContract.ProductEntry.COLUMN_PICTURE + " INTEGER NOT NULL)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME;

    public ProductDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
