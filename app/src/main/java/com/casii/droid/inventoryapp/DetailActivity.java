package com.casii.droid.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.casii.droid.inventoryapp.data.ProductContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri mCurrentProductUri;
    private static final int PRODUCT_LOADER = 1;
    @BindView(R.id.product_name)
    TextView productName;
    @BindView(R.id.product_quantity)
    TextView productQuantity;
    @BindView(R.id.product_price)
    TextView productPrice;
    @BindView(R.id.plus_btn)
    ImageButton increase_btn;
    @BindView(R.id.minus_btn)
    ImageButton decrease_btn;
    @BindView(R.id.delete_all_btn)
    Button delete_btn;
    @BindView(R.id.order_btn)
    Button order_btn;
    @BindView(R.id.product_image_view)
    ImageView productImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        ButterKnife.bind(this);
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
            final int imageIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PICTURE);
            productName.setText(data.getString(nameIndex));
            productQuantity.setText((data.getInt(quantityIndex) + ""));
            productPrice.setText((data.getInt(priceIndex) + ""));
            Bitmap image = loadImageFromStorage(data.getInt(imageIndex) + "");
            if (image != null) {
                productImageView.setImageBitmap(image);
            }
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
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.name) + name + "\n" + getString(R.string.quantity)
                + quantity + "\n" + getString(R.string.price) + price);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                .setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public int setQuantity(int quantity) {
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quantity);
        return getContentResolver().update(mCurrentProductUri, values, null, null);
    }

    private Bitmap loadImageFromStorage(String path) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);
        try {
            File file = new File(directory, path + ".jpg");
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }
}
