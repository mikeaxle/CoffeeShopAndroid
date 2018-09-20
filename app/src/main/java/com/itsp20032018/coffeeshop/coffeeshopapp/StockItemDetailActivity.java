package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * An activity representing a single StockItem detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link StockItemListActivity}.
 */
public class StockItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stockitem_detail);

    }
}
