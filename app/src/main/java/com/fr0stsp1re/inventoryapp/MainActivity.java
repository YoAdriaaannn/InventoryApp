package com.fr0stsp1re.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fr0stsp1re.inventoryapp.data.InventoryContract;
import com.fr0stsp1re.inventoryapp.data.InventoryDbHelper;
import com.fr0stsp1re.inventoryapp.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity {

    private InventoryDbHelper mDbHelper;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create a new dBHelper object
        mDbHelper = new InventoryDbHelper(this);

        // insert test data from strings.xml
        insertNewProduct();

        //display that data
        displayDbInfo();

    }

    // call this to insert test data from strings.xml file
    private void insertNewProduct() {

        SQLiteDatabase productDb = mDbHelper.getWritableDatabase();

        ContentValues v = new ContentValues();

        v.put(InventoryEntry.COL_PRODUCT_NAME, getString(R.string.string_product_name));
        v.put(InventoryEntry.COL_PRODUCT_DESCRIPTION, getString(R.string.string_product_description));
        v.put(InventoryEntry.COL_PRODUCT_SUPPLIER, getString(R.string.string_product_supplier));
        v.put(InventoryEntry.COL_PRODUCT_SUPPLIER_PHONE, getString(R.string.string_product_supplier_phone));
        v.put(InventoryEntry.COL_PRODUCT_PRICE, getString(R.string.string_product_price));
        v.put(InventoryEntry.COL_PRODUCT_QUANTITY, getString(R.string.string_product_quantity));

        long newRowId = productDb.insert(InventoryEntry.TABLE_NAME, null, v);

        if (newRowId == -1) {

            Log.v(LOG_TAG, "Error with saving product");
        } else {

            Log.v(LOG_TAG, "Product saved with row id: " + newRowId);
        }

    }

    private void displayDbInfo() {

        mDbHelper = new InventoryDbHelper(this);

        // open db
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //create projection string
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COL_PRODUCT_NAME,
                InventoryEntry.COL_PRODUCT_DESCRIPTION,
                InventoryEntry.COL_PRODUCT_SUPPLIER,
                InventoryEntry.COL_PRODUCT_SUPPLIER_PHONE,
                InventoryEntry.COL_PRODUCT_PRICE,
                InventoryEntry.COL_PRODUCT_QUANTITY};

        // query table
        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        try {
            StringBuilder displayInfo = new StringBuilder();

            // create header and append to string builder
            displayInfo.append("\n\n\n\n\n" + InventoryContract.InventoryEntry._ID + " - " +
                    InventoryEntry.COL_PRODUCT_NAME + " - " +
                    InventoryEntry.COL_PRODUCT_DESCRIPTION + " - " +
                    InventoryEntry.COL_PRODUCT_SUPPLIER + " - " +
                    InventoryEntry.COL_PRODUCT_SUPPLIER_PHONE + " - " +
                    InventoryEntry.COL_PRODUCT_PRICE + " - " +
                    InventoryEntry.COL_PRODUCT_QUANTITY + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_DESCRIPTION);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_SUPPLIER_PHONE);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_QUANTITY);

            // StringBuilder displayRowInfo = new StringBuilder();
            while (cursor.moveToNext()) {

                // Display the values from each column of the current row in the cursor in the
                displayInfo.append(("\n" + cursor.getInt(idColumnIndex) + " - " +
                        cursor.getString(nameColumnIndex) + " - " +
                        cursor.getString(descriptionColumnIndex) + " - " +
                        cursor.getString(supplierNameColumnIndex) + " - " +
                        cursor.getInt(supplierPhoneColumnIndex) + " - " +
                        cursor.getInt(priceColumnIndex) + " - " +
                        cursor.getInt(quantityColumnIndex)));

            }
            // log the results to the console
            Log.v(LOG_TAG, "The products table contains " + cursor.getCount() + " products.\n\n");

            Log.v(LOG_TAG, displayInfo.toString());

        } finally {
            cursor.close();
        }

    }

    private void dropTable() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + InventoryContract.InventoryEntry.TABLE_NAME);
        Log.v(LOG_TAG, "Table named: " + InventoryContract.InventoryEntry.TABLE_NAME + " dropped");

    }

}




