package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar appToolbar = (Toolbar) findViewById(R.id.mainAppToolbar);

    }

    /**
     * Click handler
     * @param view
     */
    public void onClick(View view) {

        // create null intent
        Intent intent = null;

        // id of button clicked
        int id = view.getId();

        // check button that was clicked & create intent to corresponding screen
        if (id == R.id.staffTextView) intent = new Intent(this, StaffMemberListActivity.class);

        if(id == R.id.stockTextView) intent = new Intent(this, StockItemListActivity.class);

        if(id == R.id.menuTextView) intent = new Intent(this, MenuItemListActivity.class);

        if(id == R.id.ordersTextView) intent = new Intent(this, OrderListActivity.class);

        if(id == R.id.posTextView) intent = new Intent(this, POSActivity.class);

        if(id == R.id.helpTextView) intent = new Intent(this, HelpActivity.class);

        // start intent - go to screen
        startActivity(intent);

    }
}
