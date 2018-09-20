package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.itsp20032018.coffeeshop.coffeeshopapp.adapters.CustomAdapter;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.StockItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * An activity representing a list of Stock. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StockItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class StockItemListActivity extends AppCompatActivity {
    // TAG
    private static final String TAG = "StockItemListActivity";

    // FireBase database instance
    FirebaseFirestore db;

    // FireBase list reference
    CollectionReference listRef;

    // Array List to store items
    ArrayList<StockItem> stockItems;

    // grid list to display items
    GridView stockGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stockitem_list);

        // set up custome tool bar
        Toolbar appToolbar = (Toolbar) findViewById(R.id.stockAppToolbar);
        setSupportActionBar(appToolbar);

        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // set up variables
        db = FirebaseFirestore.getInstance();
        listRef = db.collection("stock");
        stockItems = new ArrayList<StockItem>();
        stockGridView = (GridView) findViewById(R.id.stockGridView);

        // load list
        loadItems();
    }

    /**
     * loadItems - loads list
     */
    //TODO: turn into static global method that takes item type: string, item type: ArrayList<item type>, gridview
    private void loadItems() {
        /**
         * add listener to item list reference
         * this watches FireBase for any changes in the data this is what makes the list load update in RealTime
         */
        listRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // check for error
                if (e != null) {
                    // show toast
                    Toast.makeText(StockItemListActivity.this, "Error while fetching data", Toast.LENGTH_SHORT).show();
                    // log to console
                    Log.d(TAG, e.toString());
                    return;
                }

                // check if there are results from search
                if (queryDocumentSnapshots != null) {
                    // convert results to temporary list
                    List<StockItem> itemList = queryDocumentSnapshots.toObjects(StockItem.class);

                    // add temporary list to items array list
                    stockItems.addAll(itemList);

                    // add items array list to list adapter up list adapter
                    CustomAdapter adapter = new CustomAdapter(StockItemListActivity.this, R.layout.stockitem_list_content, "stock");
                    adapter.addAll(stockItems);

                    if (stockItems.size() != 0) {
                        stockGridView.setAdapter(adapter);

                        // set up grid list click listener
                        stockGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                Toast.makeText(StockItemListActivity.this, "" + position,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // show no items text
                        TextView noContentTextView = (TextView) findViewById(R.id.noStockTextView);
                        noContentTextView.setVisibility(View.VISIBLE);

                    }
                }
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

    /**
     * onClick - add stock handle button click
     *
     * @param view
     */
    void onClick(View view) {
        // go to add stock screen
        Intent i = new Intent(this, StockItemDetailActivity.class);
        startActivity(i);
    }
}
