package com.example.joseaherrero.inventoryapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;

import com.example.joseaherrero.inventoryapp.data.InventoryContract;
import com.example.joseaherrero.inventoryapp.data.InventoryDbHelper;
import com.example.joseaherrero.inventoryapp.data.InventoryProvider;

public class CatalogActivity extends AppCompatActivity {

    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new InventoryDbHelper(this);

        // Find the ListView which will be populated with the pet data
        ListView inventoryListView = (ListView) findViewById(R.id.list);
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link DetailActivity}
                Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link InventoryEntry#CONTENT_URI}.
                Uri currentItemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentItemUri);

                // Launch the {@link DetailActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE
        };

        Cursor cursor = getContentResolver().query(InventoryContract.InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        ListView listView = (ListView) findViewById(R.id.list);
        InventoryCursorAdapter inventoryCursorAdapter = new InventoryCursorAdapter(this, cursor);
        if(listView.getHeaderViewsCount() == 0) {
            ViewGroup header = (ViewGroup)getLayoutInflater().inflate(R.layout.list_header, listView, false);
            listView.addHeaderView(header, null, false);
        }
        listView.setAdapter(inventoryCursorAdapter);

    }
}
