package com.casii.droid.inventoryapp;

import android.content.ContentValues;
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

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class EditorActivity extends AppCompatActivity {
    private Button toCameraBtn;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Button add = (Button) findViewById(R.id.add_product_btn);
        final EditText name_editText = (EditText) findViewById(R.id.name);
        final EditText quatity_editText = (EditText) findViewById(R.id.quantity);
        final EditText price_editText = (EditText) findViewById(R.id.price);
        toCameraBtn = (Button) findViewById(R.id.toCameraBtn);
        toCameraBtn.setEnabled(hasCamera());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_editText.getText().toString();
                String quantity = quatity_editText.getText().toString();
                String price = price_editText.getText().toString();
                if (name.equals("") || quantity.equals("") || price.equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.null_content, Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_NAME, name);
                    values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, Integer.parseInt(quantity));
                    values.put(ProductContract.ProductEntry.COLUMN_PRICE, Integer.parseInt(price));
                    values.put(ProductContract.ProductEntry.COLUMN_PICTURE, picture);
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
        Bitmap photo = null;
        Bundle extras = data.getExtras();
        try {
            photo = (Bitmap) extras.get("data");
        } catch (Exception e) {

        }

        if (photo != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
             picture = Arrays.toString(baos.toByteArray());
        }
    }

}
