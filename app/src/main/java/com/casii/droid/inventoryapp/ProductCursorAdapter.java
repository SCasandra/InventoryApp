package com.casii.droid.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.casii.droid.inventoryapp.data.ProductContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Casi on 15.07.2017.
 */

public class ProductCursorAdapter extends CursorAdapter {
    private ViewHolder viewHolder;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        viewHolder = (ViewHolder) view.getTag();
        int idColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);

        String productName = cursor.getString(nameColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);
        final String productPrice = cursor.getString(priceColumnIndex);
        final Uri productEntryUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, cursor.getInt(idColumnIndex));
        viewHolder.name.setText(productName);
        viewHolder.quantity.setText(productQuantity);
        viewHolder.price.setText(productPrice);
        viewHolder.sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(productQuantity);
                if (quantity > 0) {
                    quantity--;
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quantity);
                    context.getContentResolver().update(productEntryUri, values, null, null);
                }
                Toast.makeText(context, context.getString(R.string.current_stock) + " " + quantity, Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ViewHolder {
        @BindView(R.id.product_name)
        TextView name;
        @BindView(R.id.product_quantity)
        TextView quantity;
        @BindView(R.id.product_price)
        TextView price;
        @BindView(R.id.sell_btn)
        Button sell;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
