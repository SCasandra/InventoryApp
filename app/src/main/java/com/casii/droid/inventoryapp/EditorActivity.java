package com.casii.droid.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.casii.droid.inventoryapp.data.ProductContract;

import java.io.File;
import java.io.FileOutputStream;

public class EditorActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private int pictureId = 0;
    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Button add = (Button) findViewById(R.id.add_product_btn);
        final EditText name_editText = (EditText) findViewById(R.id.name);
        final EditText quatity_editText = (EditText) findViewById(R.id.quantity);
        final EditText price_editText = (EditText) findViewById(R.id.price);
        Button toCameraBtn = (Button) findViewById(R.id.toCameraBtn);
        toCameraBtn.setEnabled(hasCamera());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_editText.getText().toString();
                String quantity = quatity_editText.getText().toString();
                String price = price_editText.getText().toString();
                if (name.isEmpty() || quantity.isEmpty() || price.isEmpty() || photo == null) {
                    Toast.makeText(getApplicationContext(), R.string.null_content, Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_NAME, name);
                    values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, Integer.parseInt(quantity));
                    values.put(ProductContract.ProductEntry.COLUMN_PRICE, Integer.parseInt(price));
                    values.put(ProductContract.ProductEntry.COLUMN_PICTURE, pictureId);
                    pictureId++;
                    Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
                    if (newUri == null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.editor_insert_product_failed),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.editor_insert_product_successful),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void launchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();
        try {
            photo = (Bitmap) extras.get("data");
            saveToInternalStorage(photo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);
        File myPath = new File(directory, pictureId + ".jpg");
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

}
