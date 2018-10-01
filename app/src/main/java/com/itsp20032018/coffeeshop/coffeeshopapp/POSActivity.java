package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.Objects;

public class POSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);

        // set up custom tool bar
        Toolbar appToolbar = findViewById(R.id.posAppToolbar);
        setSupportActionBar(appToolbar);

        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * onSupportNavigateUp -  Return to previous activity/screen
     *
     * @return boolean
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
