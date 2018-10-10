package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.io.File;
import java.util.logging.FileHandler;

public class HelpActivity extends AppCompatActivity {

    PDFView pdfView;
    ImageView helpImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // set up custom tool bar
       // Toolbar appToolbar = findViewById(R.id.or);
        //setSupportActionBar(appToolbar);

        // enable back button
      Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        helpImageView = findViewById(R.id.imageHelp);
        pdfView = findViewById(R.id.helppdf);
        pdfView.fromAsset("help.pdf")
                .load();


    }


}
