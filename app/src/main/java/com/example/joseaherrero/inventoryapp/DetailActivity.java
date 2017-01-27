package com.example.joseaherrero.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joseaherrero.inventoryapp.data.InventoryContract;

public class DetailActivity extends AppCompatActivity {

    /* Tag for the log messages */
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private TextView mNameTextView;
    private TextView mQuantityTextView;
    private TextView mPriceTextView;
    private TextView mSellerEmailTextView;
    private ImageView mImageView;
    private Button mDeleteItemButton;
    private Uri currentItemUri;
    private Button mGetShipmentButton;
    private Button mOrderShipmentButton;
    private EditText mQuantityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mNameTextView = (TextView) findViewById(R.id.detail_item_name);
        mQuantityTextView = (TextView) findViewById(R.id.detail_item_quantity);
        mPriceTextView = (TextView) findViewById(R.id.detail_item_price);
        mSellerEmailTextView = (TextView) findViewById(R.id.detail_item_seller_email);
        mImageView = (ImageView) findViewById(R.id.detail_item_image);
        mDeleteItemButton = (Button) findViewById(R.id.detail_item_remove_button);
        mGetShipmentButton = (Button) findViewById(R.id.detail_item_get_shipment_button);
        mOrderShipmentButton = (Button) findViewById(R.id.detail_item_order_button);
        mQuantityEditText = (EditText) findViewById(R.id.detail_item_order_quantity);

        mGetShipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(mQuantityTextView.getText().toString());
                int quantityToRequest;
                try {
                    quantityToRequest = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Log.e(LOG_TAG, "Failed to parse value " + e.getMessage());
                    return;
                }

                int newQuantityInInventory = currentQuantity + quantityToRequest;

                if(quantityToRequest <= 0 || newQuantityInInventory <= 0) {
                    return;
                }

                updateQuantity(newQuantityInInventory);
            }
        });

        mOrderShipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(mQuantityTextView.getText().toString());
                int quantityToRequest;
                try {
                    quantityToRequest = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Log.e(LOG_TAG, "Failed to parse value " + e.getMessage());
                    return;
                }

                int newQuantityInInventory = currentQuantity - quantityToRequest;

                if(quantityToRequest <= 0 || newQuantityInInventory <= 0) {
                    return;
                }

                updateQuantity(newQuantityInInventory);
            }
        });

        mDeleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentResolver().delete(currentItemUri,null, null);
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        currentItemUri = intent.getData();

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SELLER_EMAIL,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE
        };

        Cursor cursor = getContentResolver().query(currentItemUri,
                projection,
                null,
                null,
                null);

        if(cursor.getCount() == 1) {
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY));
            float price = cursor.getFloat(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE));
            String sellerEmail = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SELLER_EMAIL));
            String image = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE));

            mNameTextView.setText(name);
            mQuantityTextView.setText(Utils.formatQuantity(quantity));
            mPriceTextView.setText(Utils.formatPrice(price));
            mSellerEmailTextView.setText(sellerEmail);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(image));

        }

    }

    private void updateQuantity(int newQuantityInInventory) {
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantityInInventory);
        String selection = InventoryContract.InventoryEntry._ID;

        getContentResolver().update(currentItemUri, values, null, null);
        onBackPressed();
    }

    private void clearFields() {
        mQuantityEditText.setText("");
    }
}
