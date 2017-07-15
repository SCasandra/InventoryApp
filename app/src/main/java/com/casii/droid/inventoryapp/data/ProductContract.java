package com.casii.droid.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Casi on 15.07.2017.
 */

public final class ProductContract {
    public static final String CONTENT_AUTHORITY = "com.casii.droid.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "product";

    public static class ProductEntry implements BaseColumns {
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static String TABLE_NAME = "product";
        public static String COLUMN_NAME = "name";
        public static String COLUMN_QUANTITY = "quantity";
        public static String COLUMN_PRICE = "price";
        public static String COLUMN_PICTURE = "picture";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
    }
}
