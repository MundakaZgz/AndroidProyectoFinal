package com.example.joseaherrero.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Jose A. Herrero on 15/01/2017.
 */

public class InventoryProvider extends ContentProvider {

    /* Tag for the log messages */
    private static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /* URI matcher code for the content URI for the items table */
    private static final int ITEMS = 100;

    /* URI matcher code for the content URI for a single pet in the items table */
    private static final int ITEM_ID = 101;

    /* UriMatcher object to match a content URI to corresponding code */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, ITEMS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", ITEM_ID);
    }

    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {
        // Check the name is not null
        String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Item requires a name");
        }

        // Check the quantity is not null
        Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        if (quantity == -1) {
            throw new IllegalArgumentException("Item requires a quantity");
        }

        // Check the price is not null
        Float price = values.getAsFloat(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
        if (price == -1) {
            throw new IllegalArgumentException("Item requires a price");
        }

        // Check the image is not null
        String image = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Item requires an image");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new item with the given values
        long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log error and return null.
        if(id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM_ID:
                return updateItem(uri, values);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        String selection = InventoryContract.InventoryEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
        return database.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM_ID:
                return deleteItem(uri);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    private int deleteItem(Uri uri) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Delete the selected item with the given values
        String selection = InventoryContract.InventoryEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
        long id = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
        // If the ID is -1, then the insertion failed. Log error and return null.
        if(id == -1) {
            Log.e(LOG_TAG, "Failed to deleting row for " + uri);
            return -1;
        }

        return 0;
    };

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
