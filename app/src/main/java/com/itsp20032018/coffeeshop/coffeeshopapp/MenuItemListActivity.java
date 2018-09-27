package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itsp20032018.coffeeshop.coffeeshopapp.adapters.ItemAdapter;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.MenuEntry;

import java.util.Objects;

/**
 * An activity representing a list of Menu. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MenuItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MenuItemListActivity extends AppCompatActivity {
    // TAG for device logs
    private static final String TAG = "MenuItemListActivity";

    // FireBase database instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // FireBase list reference
    CollectionReference listRef = db.collection("menu");

    // RecyclerView
    RecyclerView menuRecyclerView;

    // add menu button
    Button addMenuButton;

    // custom adapter
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuitem_list);

        // set up custom tool bar
        Toolbar appToolbar = (Toolbar) findViewById(R.id.menuAppToolbar);
        setSupportActionBar(appToolbar);

        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // set up button & click listener
        addMenuButton = (Button) findViewById(R.id.addOrderButton);
//        addMenuButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createNewOrder();
//            }
//        });

        // load list
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
//        Query query = listRef.orderBy("quantity", Query.Direction.DESCENDING);

        // create FireStore recycler options
        FirestoreRecyclerOptions<MenuEntry> options = new FirestoreRecyclerOptions.Builder<MenuEntry>()
                .setQuery(listRef, MenuEntry.class)
                .build();

        // assign ItemAdapter, type is the item type to list
        adapter = new ItemAdapter(options, "menu");

        // get recycler view
        menuRecyclerView = (RecyclerView) findViewById(R.id.menuRecyclerView);

        // create new layout manager - can be set to gridLayout or linerLayout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        // set layout to grid
        menuRecyclerView.setLayoutManager(layoutManager);

        // set recycler view adapter to ItemAdapter
        menuRecyclerView.setAdapter(adapter);

        // set click listener
        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // TODO: add to order in memory
                Toast.makeText(MenuItemListActivity.this, "TODO: add to order in memory", Toast.LENGTH_SHORT).show();

            }
        });
        // set long click listener
        adapter.setOnItemLongClickListener(new ItemAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentSnapshot documentSnapshot, int position) {
                // create intent
                Intent i = new Intent(getApplicationContext(), MenuItemDetailActivity.class);

                // get reference to object and store in string
                String path = documentSnapshot.getReference().getPath();

                // add extras
                i.putExtra("PATH", path);

                // set mode to edit
                i.putExtra("MODE", "edit");

                // start activity
                startActivity(i);
            }
        });

        // TODO: show text when no menu items are in list
    }

    /**
     * Method to create new order
     *
     * @param view
     */
    public void createNewOrder(View view) {
        Toast.makeText(getApplicationContext(), "TODO: Go to order Screen", Toast.LENGTH_SHORT).show();
        // TODO: go to order detail screen with new order

    }

    /**
     * addNewMenuItem       method to go to add new menu item screen
     *
     * @param view
     */
    public void addNewMenuItem(View view) {
        Intent i = new Intent(getApplicationContext(), MenuItemDetailActivity.class);

        // set mode to edit
        i.putExtra("MODE", "add");

        startActivity(i);
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
