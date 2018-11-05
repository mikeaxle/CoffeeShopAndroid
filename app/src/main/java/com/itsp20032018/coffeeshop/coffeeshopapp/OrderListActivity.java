package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.itsp20032018.coffeeshop.coffeeshopapp.adapters.ItemAdapter;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.Order;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.Shop;

import java.util.Arrays;
import java.util.Objects;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * An activity representing a list of Orders.
 * lead to a {@link OrderDetailActivity} representing
 */
public class OrderListActivity extends AppCompatActivity {
    // TAG for device logs
    private static final String TAG = "OrderListActivity";

    // FireBase database instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // FireBase list reference
    CollectionReference listRef = db.collection("orders");

    // RecyclerView
    RecyclerView orderRecyclerView;

    // custom adapter
    private ItemAdapter adapter;

    // material design dialog
    MaterialDialog dialog;

    // layout inflater for dialog
    LayoutInflater inflater;

    // View for custom dialog
    View dialogView;

    //  views for dialog
    TextView dialogOrderNumber;
    MaterialSpinner dialogOrderStatus;
    TextView dialogOrderTotal;
    TextView dialogOrderItems;
    Button dialogPayForOrder;
    ImageButton dialogClose;

    // object to current store object
    Order order;

    // object to store shop details
    Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        // set up custom tool bar
        Toolbar appToolbar = findViewById(R.id.orderAppToolbar);
        setSupportActionBar(appToolbar);

        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // get shop details
        shop = new TinyDB(getApplicationContext()).getObject("SHOP", Shop.class);

        // init inflater
        inflater = LayoutInflater.from(this);

        // init custom dialog
        dialogView = inflater.inflate(R.layout.order_detail_dialog, null);

        // init dialog
        dialog = new MaterialDialog.Builder(this)
                .customView(dialogView, false)
                .build();

        // load list from FireBase
        loadList();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // start listening for changes in FireStore
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // stop listening for changes in FireStore
        adapter.stopListening();
    }

    /**
     * loadList     method to set up recycler view and load list
     */
    private void loadList() {
        // create FireStore query
        Query query = listRef.whereEqualTo("shop", shop.getOwner())
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // create FireStore recycler options
        FirestoreRecyclerOptions<Order> options = new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        // assign ItemAdapter, type is the item type to list
        adapter = new ItemAdapter(options, "orders");

        // get recycler view
        orderRecyclerView = findViewById(R.id.orderRecyclerView);

        // create new layout manager - can be set to gridLayout or linerLayout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        // set layout to linear
        orderRecyclerView.setLayoutManager(layoutManager);

        // set recycler view adapter to ItemAdapter
        orderRecyclerView.setAdapter(adapter);

        // TODO: make order numbers be IDS, everyday starts at 00

        // set click listener
        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {


                // get reference to object and store in string
                final String path = documentSnapshot.getReference().getPath();

                // init order and convert document snapshot to order
                order = documentSnapshot.toObject(Order.class);

                if (!order.getStatus().equals("Cancelled")) {

                    // create views for dialog
                    dialogOrderNumber = dialogView.findViewById(R.id.orderDialogNumberTextView);
                    dialogOrderStatus = dialogView.findViewById(R.id.orderDialogStatusSpinner);
                    dialogOrderTotal = dialogView.findViewById(R.id.orderDialogTotalTextView);
                    dialogOrderItems = dialogView.findViewById(R.id.orderDialogOrderItemsTextView);
                    dialogPayForOrder = dialogView.findViewById(R.id.orderDialogPaybutton);
                    dialogClose = dialogView.findViewById(R.id.orderDialogCloseImageButton);

                    // assign up text views
                    dialogOrderNumber.setText("Order #: " + documentSnapshot.getId());
                    dialogOrderTotal.setText("R" + order.getTotal());
                    dialogOrderItems.setText(order.getOrderItemsString());
                    dialogOrderStatus.setSelection(getIndexOfItem() + 2);


                    // enable or disable payment button
                    if(order.isPaid()){
                        dialogPayForOrder.setVisibility(View.GONE);
                    } else {
                        dialogPayForOrder.setVisibility(View.VISIBLE);
                    }

                    /**
                     * set up views
                     */
                    // set button click listeners
                    dialogPayForOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // go to point of sale
                            Intent i = new Intent(getApplicationContext(), POSActivity.class);

                            // add extras
                            i.putExtra("PATH", path);

                            // start activity
                            startActivity(i);

                            // close dialog
                            dialog.dismiss();
                        }
                    });

                    dialogClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // close dialog
                            dialog.dismiss();
                        }
                    });

                    dialogOrderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                            Toast.makeText(OrderListActivity.this, "i: " + 1 + " long: " + l, Toast.LENGTH_SHORT).show();

//                            Toast.makeText(OrderListActivity.this, "Selected: " + getResources().getStringArray(R.array.order_status_role_array)[i - 1], Toast.LENGTH_SHORT).show();
                            // if selection is not placeholder
                            i = i - 1;
                            if (i > -1) {

                                // if selection == cancelled
                                if (i == 3) {
                                    // disable pay button
                                    dialog.dismiss();
                                }

                                order.setStatus(getResources().getStringArray(R.array.order_status_role_array)[i]);

                                documentSnapshot.getReference().set(order)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(OrderListActivity.this, "Order status changed to: " + order.getStatus(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(OrderListActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.e(TAG, e.getMessage());
                                            }
                                        });

                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {

                        }
                    });
                    // show dialog
                    dialog.show();

                } else {
                    Toast.makeText(OrderListActivity.this, "Order #: " + documentSnapshot.getId() + " was cancelled and cannot be edited.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // TODO: show text when no staff members are in list

    }

    private int getIndexOfItem() {
        int currentStatus;
        String tempArray[];

        tempArray = getApplicationContext().getResources().getStringArray(R.array.order_status_role_array);
        currentStatus = Arrays.asList(tempArray).lastIndexOf(order.getStatus());

        Log.d(TAG, "current status: " + currentStatus);

        return currentStatus;
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
