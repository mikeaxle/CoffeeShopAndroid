package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import fr.ganfra.materialspinner.MaterialSpinner;


public class POSActivity extends AppCompatActivity {

    // TAG for device logs
    private static final String TAG = "POSActivity";

    // FireBase database instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // FireBase item reference
    DocumentReference itemRef;

    // declare views
    ConstraintLayout posLayout;
    ConstraintLayout posNoOrderLayout;
    Button cardButton;
    Button cashButton;
    Button processPaymentButton;
    TextView orderTotal;
    TextView orderNumber;
    TextView statusDate;
    TextView posInstructions;
    MaterialSpinner orderListSpinner;

    // order object
    Order order;

    // order arraylist
    ArrayList<Order> orderList;

    // FireStore document path
    String path;

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

        Intent i = getIntent();
        if(!i.hasExtra("PATH")) {

            // load screen with no order selected
            loadOrderDropDown();

        } else {
            // get path from intent
            path = i.getStringExtra("PATH");

            // assign views
            loadOrderInfo();
        }

    }

    private void loadOrderDropDown(){
        db.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            orderList = (ArrayList<Order>) task.getResult().toObjects(Order.class);
                            orderListSpinner = findViewById(R.id.posOrderListSpinner);
                            ArrayAdapter<Order> adapter = new ArrayAdapter<Order>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, orderList);

                            orderListSpinner.setAdapter(adapter);

                            orderListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    // TODO: get order with ID from firestore and display on page
                                    Toast.makeText(POSActivity.this, "TODO: open order: " + i, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Toast.makeText(POSActivity.this, "Please Select an order from the drop down list above", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(POSActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(POSActivity.this, "Failed to load order list. Something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    /**
     * loadOrderInfo    -   Method to load order details in views
     */
    private void loadOrderInfo() {
        posLayout = findViewById(R.id.posLinerLayout);
        posNoOrderLayout = findViewById(R.id.posNoOrderConstraintLayout);

        //set up views
        cashButton = findViewById(R.id.posCashButton);
        cardButton = findViewById(R.id.posCardButton);
        processPaymentButton = findViewById(R.id.posProcessPaymentButton);
        orderTotal = findViewById(R.id.posTotalTextView);
        orderNumber = findViewById(R.id.posOrderNumberTextView);
        statusDate = findViewById(R.id.posStatusDateTextView);
        posInstructions = findViewById(R.id.posInstructionsTextView);



        // get FireStore document
        itemRef = db.document(path);
        itemRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // formatting the date
                        final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, d MMM yyyy");

                        // check if document exists
                        if (documentSnapshot.exists()) {
                            // cast to item
                            order = documentSnapshot.toObject(Order.class);

                            // hide spinner
                            posNoOrderLayout.setVisibility(View.GONE);

                            // show order views
                            posLayout.setVisibility(View.VISIBLE);

                            // show process payment button
                            processPaymentButton.setVisibility(View.VISIBLE);

                            // assign views
                            orderTotal.setText("R" + order.getTotal());
                            orderNumber.setText("Order #: " + documentSnapshot.getId());
                            statusDate.setText(order.getStatus() + " - " +  dateFormat.format(order.getTimestamp()));
                            togglePaymentMode("card");


                            // set button click listeners
                            cashButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    togglePaymentMode("cash");
                                }
                            });

                            cardButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    togglePaymentMode("card");
                                }
                            });

                            processPaymentButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    processPayment();
                                }
                            });

                        } else {
                            // show does not exists toast
                            Toast.makeText(getApplicationContext(), "Item does not exists", Toast.LENGTH_SHORT).show();

                            // redirect back to list
                            finish();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // show failure toast
                        Toast.makeText(getApplicationContext(), "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        // log error
                        Log.e(TAG, e.getMessage());

                        // redirect back to list
                        finish();
                    }
                });

    }

    /**
     * togglePaymentMode        change Payment mode
     */
    private void togglePaymentMode(String mode) {
        // check requested mode
        if(mode.equals("cash")) {
            // toggle active button
            cardButton.setBackgroundResource(R.drawable.rounded_corner_button);
            cardButton.setTextAppearance(getApplicationContext(), R.style.mediumLightBlueButton);
            cardButton.setTextSize(12);

            cashButton.setBackgroundResource(R.drawable.rounded_corner_button_filled);
            cashButton.setTextAppearance(getApplicationContext(), R.style.mediumFilledBlueButton);
            cashButton.setTextSize(12);

            // set instructions text
            posInstructions.setText("Collect R" + order.getTotal() + " from customer and tap 'next'");

            // change button text
            processPaymentButton.setText("next");
        } else if (mode.equals("card")) {
            // toggle active button
            cardButton.setBackgroundResource(R.drawable.rounded_corner_button_filled);
            cardButton.setTextAppearance(getApplicationContext(), R.style.mediumFilledBlueButton);
            cardButton.setTextSize(12);

            cashButton.setBackgroundResource(R.drawable.rounded_corner_button);
            cashButton.setTextAppearance(getApplicationContext(), R.style.mediumLightBlueButton);
            cashButton.setTextSize(12);

            // set instructions text
            posInstructions.setText("Connect card reader to device and tap ‘process payment’ ");

            // change button text
            processPaymentButton.setText("process payment");
        }
    }

    /**
     * processPayment       method to process payment and go to payment success screen
     */
    private void processPayment(){
        // update order
        order.setPaid(true);
        itemRef.set(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // go to payment success page
                        startActivity(new Intent(getApplicationContext(), PaymentSuccess.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(POSActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.getMessage());
                        finish();
                    }
                });
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
