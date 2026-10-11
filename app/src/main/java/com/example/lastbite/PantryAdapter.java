package com.example.lastbite;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.math.BigDecimal;

public class PantryAdapter extends CursorAdapter {

    public PantryAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                android.R.layout.simple_list_item_2, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView = view.findViewById(android.R.id.text1);
        TextView quantityView = view.findViewById(android.R.id.text2);

        String name = cursor.getString(
                cursor.getColumnIndexOrThrow("name"));
        double quantity = cursor.getDouble(
                cursor.getColumnIndexOrThrow("quantity"));
        String unit = cursor.getString(
                cursor.getColumnIndexOrThrow("unit"));

        // Display whole quantities without an unnecessary decimal.
        String quantityText = BigDecimal.valueOf(quantity)
                .stripTrailingZeros().toPlainString();

        nameView.setText(name);
        nameView.setTextSize(18);
        nameView.setTextColor(Color.parseColor("#253A2E"));

        quantityView.setText(quantityText + " " + unit);
        quantityView.setTextSize(16);
        quantityView.setTextColor(Color.parseColor("#59665D"));
    }
}