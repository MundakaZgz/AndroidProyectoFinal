package com.example.joseaherrero.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.joseaherrero.inventoryapp.data.InventoryContract;
import com.example.joseaherrero.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Jose A. Herrero on 07/01/2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        ImageButton sellOneButton = (ImageButton) view.findViewById(R.id.sellOneButton);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_INVENTORY_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_INVENTORY_QUANTITY));
        float price = cursor.getFloat(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_INVENTORY_PRICE));

        sellOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity - 1);
                    String selection = InventoryContract.InventoryEntry._ID;
                    Uri currentItemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry._ID)));
                    context.getContentResolver().update(currentItemUri, values, null, null);

                }
            }
        });

        nameTextView.setText(name);
        priceTextView.setText(Utils.formatPrice(price));
        quantityTextView.setText(Utils.formatQuantity(quantity));

    }


}
