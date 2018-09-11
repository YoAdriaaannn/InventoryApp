/*
 * Copyright (c) 2018.  Adrian Raff AKA Fr0stsp1re
 * ************PROJECT LICENSE*************
 *
 * This project was submitted by Adrian Raff as part of the  Android Basics Nanodegree At Udacity.
 *
 * The Udacity Honor code requires your submissions must be your own work.
 * Submitting this project as yours will cause you to break the Udacity Honor Code
 * and may result in disiplinary action.
 *
 * The author of this project allows you to check the code as a reference only. You may not submit this project or any part
 * of the code as your own.
 *
 * Besides the above notice, the following license applies and this license notice
 * must be included in all works derived from this project.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.fr0stsp1re.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.fr0stsp1re.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryProvider extends ContentProvider {

    // uri int matcher for product table
    private static final int PRODUCT = 100;

    // uri int matcher for single product
    private static final int PRODUCT_ID = 101;

    // uri matcher object
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCT, PRODUCT);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    // db helper object
    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        // match uri code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCT:

                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case PRODUCT_ID:

                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:

                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case PRODUCT:

                return insertProduct(uri, contentValues);

            default:

                throw new IllegalArgumentException("Insertion is not supported for " + uri);

        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        long id = 0;

        // != null check for product name
        String name = values.getAsString(InventoryEntry.COL_PRODUCT_NAME);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Name of item is required", Toast.LENGTH_SHORT).show();
        }

        // != null check for product name
        String description = values.getAsString(InventoryEntry.COL_PRODUCT_DESCRIPTION);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Description of item is required", Toast.LENGTH_SHORT).show();
        }

        // != null check for product name
        String supplier = values.getAsString(InventoryEntry.COL_PRODUCT_SUPPLIER);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Supplier is required", Toast.LENGTH_SHORT).show();
        }

        // != null check for product name
        String supplierPhone = values.getAsString(InventoryEntry.COL_PRODUCT_SUPPLIER_PHONE);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Supplier phone is required", Toast.LENGTH_SHORT).show();
        }

        // != null check for product name
        String price = values.getAsString(InventoryEntry.COL_PRODUCT_PRICE);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Price is required", Toast.LENGTH_SHORT).show();
        }

        // check to see if quantity is 0 or >
        Integer quantity = values.getAsInteger(InventoryEntry.COL_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            Toast.makeText(getContext(), "Quantity is required. Min 0", Toast.LENGTH_SHORT).show();
        }

        // check to see if there is an image
        String image = values.getAsString(InventoryEntry.COL_PRODUCT_PICTURE);
        if (TextUtils.isEmpty(image)){
            Toast.makeText(getContext(), "An image is required", Toast.LENGTH_SHORT).show();
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new product only if all fields are not empty
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(supplier)
                && !TextUtils.isEmpty(supplierPhone) && !TextUtils.isEmpty(price) && quantity != null && !TextUtils.isEmpty(image)) {
            id = database.insert(InventoryEntry.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id);
        }
        if (id == -1) {
            return null;
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case PRODUCT:

                return updateProduct(uri, contentValues, selection, selectionArgs);

            case PRODUCT_ID:

                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateProduct(uri, contentValues, selection, selectionArgs);

            default:

                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // != null check for item name
        if (values.containsKey(InventoryEntry.COL_PRODUCT_NAME)) {
            String name = values.getAsString(InventoryEntry.COL_PRODUCT_NAME);
            if (name == null) {
                Toast.makeText(getContext(), "Item requires a name", Toast.LENGTH_SHORT).show();
            }
        }

        // != null check for item description
        if (values.containsKey(InventoryEntry.COL_PRODUCT_DESCRIPTION)) {
            String description = values.getAsString(InventoryEntry.COL_PRODUCT_DESCRIPTION);
            if (description == null) {
                Toast.makeText(getContext(), "Item requires a name", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Item requires a description");
            }
        }

        // != null check for supplier
        if (values.containsKey(InventoryEntry.COL_PRODUCT_SUPPLIER)) {
            String name = values.getAsString(InventoryEntry.COL_PRODUCT_SUPPLIER);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a supplier name");
            }
        }

        // != null check for supplier phone
        if (values.containsKey(InventoryEntry.COL_PRODUCT_SUPPLIER_PHONE)) {
            String name = values.getAsString(InventoryEntry.COL_PRODUCT_SUPPLIER_PHONE);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a phone number");
            }
        }

        // != null check for supplier phone
        if (values.containsKey(InventoryEntry.COL_PRODUCT_PRICE)) {
            String name = values.getAsString(InventoryEntry.COL_PRODUCT_PRICE);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a price");
            }
        }

        // check that the quantity value is valid.
        if (values.containsKey(InventoryEntry.COL_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryEntry.COL_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Requires a quantity of at least zero");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        // notify all listeners that rows were affected
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // var to track number of rows deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case PRODUCT:

                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PRODUCT_ID:

                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:

                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }
        // notify all listeners that rows were deleted
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case PRODUCT:

                return InventoryEntry.CONTENT_LIST_TYPE;

            case PRODUCT_ID:

                return InventoryEntry.CONTENT_ITEM_TYPE;

            default:

                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
