package com.casii.droid.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.casii.droid.inventoryapp.data.ProductContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri mCurrentProductUri;
    private static final int PRODUCT_LOADER = 1;
    private TextView productName;
    private TextView productQuantity;
    private TextView productPrice;
    private ImageButton increase_btn;
    private ImageButton decrease_btn;
    private Button delete_btn;
    private Button order_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        productName = (TextView) findViewById(R.id.product_name);
        productQuantity = (TextView) findViewById(R.id.product_quantity);
        productPrice = (TextView) findViewById(R.id.product_price);
        increase_btn = (ImageButton) findViewById(R.id.plus);
        decrease_btn = (ImageButton) findViewById(R.id.minus);
        delete_btn = (Button) findViewById(R.id.delete_all_btn);
        order_btn = (Button) findViewById(R.id.order_btn);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri != null) {
            getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,                       // Parent activity context
                mCurrentProductUri,         // Table to query
                null,                       // Projection
                null,                       // Selection clause
                null,                       // Selection arguments
                null                        // Default sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        if (data.moveToFirst()) {
            final int nameIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME);
            final int quantityIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);
            final int priceIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
            productName.setText(data.getString(nameIndex));
            productQuantity.setText((data.getInt(quantityIndex) + ""));
            productPrice.setText((data.getInt(priceIndex) + ""));
            increase_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setQuantity(data.getInt(quantityIndex) + 1);
                }
            });
            decrease_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = data.getInt(quantityIndex) - 1;
                    if (quantity >= 0) {
                        setQuantity(quantity);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.current_stock) + quantity, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete();
                }
            });
            order_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMail(data.getString(nameIndex), data.getInt(quantityIndex), data.getInt(priceIndex));
                }
            });
        }
    }

    private void sendMail(String name, int quantity, int price) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\n Quantity: " + quantity + "\nPrice: " + price);
            startActivity(intent);
        }
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
                        if (rowsDeleted == 0) {
                            Toast.makeText(getApplicationContext(), R.string.deletion_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.deletion_successful,
                                    Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public int setQuantity(int quantity) {
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quantity);
        return getContentResolver().update(mCurrentProductUri, values, null, null);
    }
}
