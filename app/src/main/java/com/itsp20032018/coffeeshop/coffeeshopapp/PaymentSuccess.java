package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class PaymentSuccess extends AppCompatActivity {

    // TAG for device logs
    private static final String TAG = "POSActivity";

    // declare views
    Button emailButton;
    Button printButton;
    Button sendButton;
    TextView orderNumber;
    TextView printInstructions;
    EditText emailAddress;

    // order details variables
    double orderTotal;
    String orderDetails;
    String orderId;

    // mode text
    String mode = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // set up custom tool bar
        Toolbar appToolbar = findViewById(R.id.printAppToolbar);
        setSupportActionBar(appToolbar);

        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        // get order details from intent
        Intent i = getIntent();
        orderTotal = i.getDoubleExtra("ORDER_TOTAL", 0);
        orderId = i.getStringExtra("ORDER_ID");
        orderDetails = i.getStringExtra("ORDER_DETAILS");

        // get views
        emailButton = findViewById(R.id.printEmailButton);
        printButton = findViewById(R.id.printButton);
        sendButton = findViewById(R.id.sendButton);
        orderNumber = findViewById(R.id.printNotificationTextView);
        printInstructions = findViewById(R.id.printInstructionsTextView);
        emailAddress = findViewById(R.id.printEmailEditText);

        // set up views
        orderNumber.setText("Would you like to print to receipt for \norder # " +  orderId + "?");
        togglePrintMode("email");

        // set up button click listeners
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePrintMode("email");
            }
        });

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePrintMode("print");
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printReceipt();
            }
        });

    }

    /**
     * printReceipt     method to print or email reciept
     */
    private void printReceipt() {
        // TODO: email reciept or connect to wireless printer and print
        if(mode.equals("email")){
            Toast.makeText(PaymentSuccess.this, "Receipt emailed", Toast.LENGTH_SHORT).show();
        } else if(mode.equals("print")){
            Toast.makeText(PaymentSuccess.this, "Receipt printed", Toast.LENGTH_SHORT).show();
        }

        // got to home screen + destroy stack
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * togglePrintMode      change print/email mode
     */
    private void togglePrintMode(String mode){
        if(mode.equals("email")){
            // toggle active button
            printButton.setBackgroundResource(R.drawable.rounded_corner_button);
            printButton.setTextAppearance(getApplicationContext(), R.style.mediumLightBlueButton);
            printButton.setTextSize(12);

            emailButton.setBackgroundResource(R.drawable.rounded_corner_button_filled);
            emailButton.setTextAppearance(getApplicationContext(), R.style.mediumFilledBlueButton);
            emailButton.setTextSize(12);

            // show email input
            emailAddress.setVisibility(View.VISIBLE);

            // set instructions text
            printInstructions.setText("Enter customer's e-mail address and tap 'send'");

            // change button text
            sendButton.setText("send");

            // set mode
            mode = "email";

        } else if(mode.equals("print")){
            // toggle active button
            printButton.setBackgroundResource(R.drawable.rounded_corner_button_filled);
            printButton.setTextAppearance(getApplicationContext(), R.style.mediumFilledBlueButton);
            printButton.setTextSize(12);

            emailButton.setBackgroundResource(R.drawable.rounded_corner_button);
            emailButton.setTextAppearance(getApplicationContext(), R.style.mediumLightBlueButton);
            emailButton.setTextSize(12);

            // hide email input
            emailAddress.setVisibility(View.GONE);

            // set instructions text
            printInstructions.setText("connect wireless printer to device and tap 'print'");

            // change button text
            sendButton.setText("print");

            // set mode
            mode = "print";

        }

    }

    @Override
    public void onBackPressed() {
        printReceipt();
    }
}
