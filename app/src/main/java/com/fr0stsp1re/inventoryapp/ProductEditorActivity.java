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

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fr0stsp1re.inventoryapp.data.InventoryContract.InventoryEntry;

public class ProductEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // loader id
    private static final int EXISTING_PRODUCT_LOADER = 0;

    // content value object
    private final ContentValues values = new ContentValues();

    // phone permission
    private int PHONE_PERMISSION_REQUEST = 1;

    // product uri
    private Uri mCurrentProductUri;

    //flag for unlock button
    private boolean mUnlockFlag = false;

    // boolean flag for change indicator
    private boolean mProductHasChanged = false;

    // on touch listener
    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    // edittext fields
    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    // edit buttons

    private ImageButton mUnlockEditButton;
    private ImageButton mLockEditButton;
    private ImageButton mDeleteButton;
    private ImageButton mAdjustQuantityUpButton;
    private ImageButton mAdjustQuantityDownButton;
    private ImageButton mCallSupplierButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // edittext fields
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_product_description);
        mSupplierEditText = (EditText) findViewById(R.id.edit_supplier);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_supplier_phone);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);

        //edit buttons and action buttons
        mUnlockEditButton = findViewById(R.id.edit_unlock_single_item);
        mLockEditButton = findViewById(R.id.edit_lock_single_item);
        mDeleteButton = findViewById(R.id.edit_delete_single_item);
        mAdjustQuantityUpButton = findViewById(R.id.edit_quantity_button_plus);
        mAdjustQuantityDownButton = findViewById(R.id.edit_quantity_button_minus);
        mCallSupplierButton = findViewById(R.id.edit_call_supplier);

        // on touch listeners used to detrermine if data has been modified or touched for any particular field
        mNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);

        // disable edittext boxes until enabled for editing.
        mNameEditText.setEnabled(false);
        mDescriptionEditText.setEnabled(false);
        mSupplierEditText.setEnabled(false);
        mSupplierPhoneEditText.setEnabled(false);
        mDescriptionEditText.setEnabled(false);
        mPriceEditText.setEnabled(false);
        mQuantityEditText.setEnabled(false);

        // disable all but the editing unlock button
        mDeleteButton.setVisibility(View.INVISIBLE);

        // set unlock button and flag to be visible
        mUnlockEditButton.setVisibility(View.VISIBLE);
        mUnlockFlag = true; // flag set to one means button is visible

        mLockEditButton.setVisibility(View.INVISIBLE);
        mAdjustQuantityDownButton.setVisibility(View.INVISIBLE);
        mAdjustQuantityUpButton.setVisibility(View.INVISIBLE);


        // grab the intent from the catalog page and see if we need to set the title of the page to
        // edit or add and new product
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // check if uri is empty. if so set title to add new product
        if (mCurrentProductUri == null) {

            enableEdit();
            setTitle("Add New Product");
            invalidateOptionsMenu();

        } else {
            // the uri is not empty
            setTitle("Product Detail");
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }


        // unlock editing
        mUnlockEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEdit();
                // set title of activity
                setTitle("Edit Product");
            }
        });

        // lock editing
        mLockEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableEdit();
                // set title of activity
                setTitle("Product Details");
            }
        });

        // delete
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        // order more
        mCallSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderConfirmationDialog();
            }
        });

        // increase quantity
        mAdjustQuantityUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });

        //decrease quantity
        mAdjustQuantityDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });



    }

    private void saveProduct() {

        String nameString = mNameEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();

        // new product check and empty field check
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(descriptionString) &&
                TextUtils.isEmpty(supplierString) && TextUtils.isEmpty(supplierPhoneString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString)) {
            return;
        }

        values.put(InventoryEntry.COL_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COL_PRODUCT_DESCRIPTION, descriptionString);
        values.put(InventoryEntry.COL_PRODUCT_SUPPLIER, supplierString);
        values.put(InventoryEntry.COL_PRODUCT_SUPPLIER_PHONE, supplierPhoneString);
        values.put(InventoryEntry.COL_PRODUCT_PRICE, priceString);
        values.put(InventoryEntry.COL_PRODUCT_QUANTITY, quantityString);

        // if quantity is not provided, use zero by default
        int quantity = 0;

        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }

        values.put(InventoryEntry.COL_PRODUCT_QUANTITY, quantity);

        // Determine if this is a new or existing product by checking if mCurrentProductUri is null or not
        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error Saving",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            //existing product
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error Updating!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product Updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteProduct() {

        if (mCurrentProductUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Delete Failed", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // check to see if unlock button is unlocked. if so there must be an unsaved change
                // and the lock must be set before going back.
                if (!mUnlockFlag) {
                    if (!mProductHasChanged) {
                        NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                        return true;
                    }
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                                }
                            };
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged && mUnlockFlag) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * loader callbacks
     */

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

        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_DESCRIPTION);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_SUPPLIER_PHONE);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COL_PRODUCT_QUANTITY);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mDescriptionEditText.setText(description);
            mSupplierEditText.setText(supplier);
            mSupplierPhoneEditText.setText(supplierPhone);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
    }

    /**
     * confirmation dialog boxes
     */

    // usaved changes dialog
    private void showUnsavedChangesDialog(

            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();

                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    // alert dialog box for deleting a product
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //alert dialog to order more
    private void showOrderConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Call to order this item?");
        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSupplierPhoneEditText.getText().toString().trim()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // enable editing of product
    private void enableEdit() {


        //set unlock flag
        mUnlockFlag = false;

        // disable edittext boxes until enabled for editing.
        mNameEditText.setEnabled(true);
        mDescriptionEditText.setEnabled(true);
        mSupplierEditText.setEnabled(true);
        mSupplierPhoneEditText.setEnabled(true);
        mDescriptionEditText.setEnabled(true);
        mPriceEditText.setEnabled(true);
        mQuantityEditText.setEnabled(true);

        // hide and show buttons
        mDeleteButton.setVisibility(View.VISIBLE);
        mUnlockEditButton.setVisibility(View.INVISIBLE);
        mLockEditButton.setVisibility(View.VISIBLE);
        mAdjustQuantityDownButton.setVisibility(View.VISIBLE);
        mAdjustQuantityUpButton.setVisibility(View.VISIBLE);

    }

    // disable and lock editing
    private void disableEdit() {


        // set unlock flag
        mUnlockFlag = true;

        // disable edittext boxes until enabled for editing.
        mNameEditText.setEnabled(false);
        mDescriptionEditText.setEnabled(false);
        mSupplierEditText.setEnabled(false);
        mSupplierPhoneEditText.setEnabled(false);
        mDescriptionEditText.setEnabled(false);
        mPriceEditText.setEnabled(false);
        mQuantityEditText.setEnabled(false);

        // hide and show buttons
        mDeleteButton.setVisibility(View.INVISIBLE);
        mUnlockEditButton.setVisibility(View.VISIBLE);
        mLockEditButton.setVisibility(View.INVISIBLE);
        mAdjustQuantityUpButton.setVisibility(View.INVISIBLE);
        mAdjustQuantityDownButton.setVisibility(View.INVISIBLE);

        //save product
        saveProduct();
    }

    private void decreaseQuantity() {
        String previousValueString = mQuantityEditText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            mQuantityEditText.setText(String.valueOf(previousValue - 1));
        }
    }

    private void increaseQuantity() {
        String previousValueString = mQuantityEditText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        mQuantityEditText.setText(String.valueOf(previousValue + 1));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}

