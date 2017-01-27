package com.example.joseaherrero.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jose A. Herrero on 15/01/2017.
 */

public class InventoryContract {

    public InventoryContract() {
        super();
    }

    public static final String CONTENT_AUTHORITY = "com.example.joseaherrero.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "inventory";

    public static final class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
        public static final String CONTENT_PATH = CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_PATH;

        public final static String TABLE_NAME = "inventory";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_INVENTORY_NAME = "name";
        public final static String COLUMN_INVENTORY_QUANTITY = "quantity";
        public final static String COLUMN_INVENTORY_PRICE = "price";
        public final static String COLUMN_INVENTORY_SELLER_EMAIL = "seller_email";
        public final static String COLUMN_INVENTORY_IMAGE = "image";

    }
}
