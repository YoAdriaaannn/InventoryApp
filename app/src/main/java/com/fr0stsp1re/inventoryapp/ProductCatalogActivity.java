package com.fr0stsp1re.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.content.CursorLoader;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fr0stsp1re.inventoryapp.data.InventoryDbHelper;
import com.fr0stsp1re.inventoryapp.data.InventoryContract.InventoryEntry;

public class ProductCatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    // listview adapter
    private ProductCursorAdapter mCursorAdapter;

    // int id for loader
    private static final int PRODUCT_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_catalog);

      //Floating AB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductCatalogActivity.this, ProductEditorActivity.class);

                startActivity(intent);

            }
        });

        ListView productListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

      //onclick listener for items to open in editor
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(ProductCatalogActivity.this, ProductEditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);

            }

        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }

    // call this to insert test data from strings.xml file
    private void insertNewDummyProduct() {

        ContentValues v = new ContentValues();

        v.put(InventoryEntry.COL_PRODUCT_NAME, getString(R.string.string_product_name));
        v.put(InventoryEntry.COL_PRODUCT_DESCRIPTION, getString(R.string.string_product_description));
        v.put(InventoryEntry.COL_PRODUCT_SUPPLIER, getString(R.string.string_product_supplier));
        v.put(InventoryEntry.COL_PRODUCT_SUPPLIER_PHONE, getString(R.string.string_product_supplier_phone));
        v.put(InventoryEntry.COL_PRODUCT_PRICE, getString(R.string.string_product_price));
        v.put(InventoryEntry.COL_PRODUCT_QUANTITY, getString(R.string.string_product_quantity));

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, v);

    }

    private void deleteAllProducts() {

        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_catalog, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_insert_dummy_data:

                insertNewDummyProduct();

                return true;

            case R.id.action_delete_all_entries:

                deleteAllProducts();

                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COL_PRODUCT_NAME,
                InventoryEntry.COL_PRODUCT_DESCRIPTION,
                InventoryEntry.COL_PRODUCT_SUPPLIER,
                InventoryEntry.COL_PRODUCT_SUPPLIER_PHONE,
                InventoryEntry.COL_PRODUCT_PRICE,
                InventoryEntry.COL_PRODUCT_QUANTITY};

        return new CursorLoader(this, InventoryEntry.CONTENT_URI, projection, null,
                null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);

    }
    @Override

    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
