package com.example.joseaherrero.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
    private TextView mSellerEmailTextView;
    private Uri currentItemUri;
    private EditText mQuantityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mNameTextView = (TextView) findViewById(R.id.detail_item_name);
        mQuantityTextView = (TextView) findViewById(R.id.detail_item_quantity);
        TextView mPriceTextView = (TextView) findViewById(R.id.detail_item_price);
        mSellerEmailTextView = (TextView) findViewById(R.id.detail_item_seller_email);
        ImageView mImageView = (ImageView) findViewById(R.id.detail_item_image);
        Button mDeleteItemButton = (Button) findViewById(R.id.detail_item_remove_button);
        Button mGetShipmentButton = (Button) findViewById(R.id.detail_item_get_shipment_button);
        Button mOrderShipmentButton = (Button) findViewById(R.id.detail_item_order_button);
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

                if(quantityToRequest <= 0) {
                    return;
                }

                composeEmail(quantityToRequest);
            }
        });

        mDeleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                getContentResolver().delete(currentItemUri, null, null);
                                onBackPressed();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setMessage(getString(R.string.confirmation_dialog_message));
                builder.setPositiveButton(getString(R.string.confirmation_dialog_yes), dialogClickListener);
                builder.setNegativeButton(getString(R.string.confirmation_dialog_no), dialogClickListener);
                builder.show();

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

        cursor.close();
    }

    private void composeEmail(int requiredQuantity) {

        String productName = mNameTextView.getText().toString().trim();
        String[] emails = {mSellerEmailTextView.getText().toString().trim()};

        String subject = "New order";

        StringBuilder message = new StringBuilder();
        message.append("Dear seller, \nI would like to order ");
        message.append(requiredQuantity);
        message.append(" unit");
        if (requiredQuantity > 1) {
            message.append("s");
        }
        message.append(" of your product ");
        message.append(productName);
        message.append(".\nThank you very much!");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message.toString());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
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
