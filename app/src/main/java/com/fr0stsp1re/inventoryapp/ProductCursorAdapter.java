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

package com.fr0stsp1re.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.fr0stsp1re.inventoryapp.data.InventoryContract.InventoryEntry;

import static android.content.ContentValues.TAG;

class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.name);
        TextView descriptionTextView = view.findViewById(R.id.description);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView quantityTextViewLabel = view.findViewById(R.id.quantity_label);
        ImageButton buyImageButton = view.findViewById(R.id.list_view_add_to_cart);

        final int productIdColumnIndex = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_NAME);
        int descriptionColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_DESCRIPTION);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_QUANTITY);

        // load from cursor to a variable
        String productName = cursor.getString(productNameColumnIndex);
        String productDescription = cursor.getString(descriptionColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);

        // if there is no product description display default message
        if (TextUtils.isEmpty(productDescription)) {

            productDescription = context.getString(R.string.no_product_description);
        }

        // if there is no product description display default message
        if (productQuantity <=0) {

            quantityTextViewLabel.setText("OUT OF STOCK");
        }

        // update textviews with current product
        nameTextView.setText(productName);
        descriptionTextView.setText(productDescription);
        priceTextView.setText("$" + productPrice);
        quantityTextView.setText(String.valueOf(productQuantity));

        // onclick listener for buy imagebutton
        buyImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri productUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, productIdColumnIndex);
                adjustProductQuantity(context, productUri, productQuantity);
            }
        });

    }

    private void adjustProductQuantity(Context context, Uri productUri, int currentQuantityInStock) {

        // Subtract 1 from current value if quantity of product >= 1
        int newQuantityValue = (currentQuantityInStock >= 1) ? currentQuantityInStock - 1 : 0;

        if (currentQuantityInStock == 0) {
            Toast.makeText(context.getApplicationContext(), "Item out of stock", Toast.LENGTH_SHORT).show();
        }

        // Update table by using new value of quantity
        ContentValues v = new ContentValues();
        v.put(InventoryEntry.COL_PRODUCT_QUANTITY, newQuantityValue);
        int numRowsUpdated = context.getContentResolver().update(productUri, v, null, null);
        if (numRowsUpdated > 0) {
            // Show error message in Logs with info about pass update.
            Log.i(TAG, "Item bought");
        } else {
            Toast.makeText(context.getApplicationContext(), "Product not in stock", Toast.LENGTH_SHORT).show();
            // Show error message in Logs with info about fail update.
            Log.e(TAG, "Update failed");
        }


    }
}

