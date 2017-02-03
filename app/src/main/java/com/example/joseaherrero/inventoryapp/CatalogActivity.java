package com.example.joseaherrero.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.joseaherrero.inventoryapp.data.InventoryContract;

public class CatalogActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOAD_PRODUCTS = 1;
    private InventoryCursorAdapter mInventoryCursorAdapter;

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

        // Find the ListView which will be populated with the pet data
        ListView inventoryListView = (ListView) findViewById(R.id.list);
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link InventoryEntry#CONTENT_URI}.
                Uri currentItemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                startDetailActivity(currentItemUri);
            }
        });

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        mInventoryCursorAdapter = new InventoryCursorAdapter(this);

        // Add header if needed
        if(inventoryListView.getHeaderViewsCount() == 0) {
            ViewGroup header = (ViewGroup)getLayoutInflater().inflate(R.layout.list_header, inventoryListView, false);
            inventoryListView.addHeaderView(header, null, false);
        }
        inventoryListView.setAdapter(mInventoryCursorAdapter);

        getLoaderManager().initLoader(LOAD_PRODUCTS, null, this);
    }

    private void startDetailActivity(Uri currentItemUri) {
        // Create new intent to go to {@link DetailActivity}
        Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);

        // Set the URI on the data field of the intent
        intent.setData(currentItemUri);

        // Launch the {@link DetailActivity} to display the data for the current pet.
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOAD_PRODUCTS:
                String[] projection = {
                        InventoryContract.InventoryEntry._ID,
                        InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                        InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                        InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE
                };

                return new CursorLoader(this, InventoryContract.InventoryEntry.CONTENT_URI, projection, null, null, null);
            default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mInventoryCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mInventoryCursorAdapter.swapCursor(null);
    }
}
