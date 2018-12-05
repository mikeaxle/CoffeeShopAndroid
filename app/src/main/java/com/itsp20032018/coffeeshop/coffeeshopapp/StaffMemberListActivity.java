package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.itsp20032018.coffeeshop.coffeeshopapp.adapters.ItemAdapter;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.Shop;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.StaffMember;

import java.util.Objects;

/**
 * An activity representing a list of Staff. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StaffMemberDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class StaffMemberListActivity extends AppCompatActivity {
    // TAG for device logs
    private static final String TAG = "StaffMemberListActivity";

    // FireBase database instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // FireBase list reference
    CollectionReference listRef  = db.collection("staff");

    // RecyclerView
    RecyclerView staffRecyclerView;

    // add staff button
    Button addStaffButton;

    // custom adapter
    private ItemAdapter adapter;

    // object to store shop details
    Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staffmember_list);

        // set up custom tool bar
        Toolbar appToolbar = (Toolbar) findViewById(R.id.staffAppToolbar);
        setSupportActionBar(appToolbar);

        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // get shop details
        shop = new TinyDB(getApplicationContext()).getObject("SHOP", Shop.class);

        // set up button & click listener
        addStaffButton = (Button) findViewById(R.id.addStaffbutton);
        addStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to add staff detail screen
                Intent i = new Intent(getApplicationContext(), StaffMemberDetailActivity.class);

                // set mode to edit
                i.putExtra("MODE", "add");

                startActivity(i);
            }
        });

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
        // TODO: checked logged in user role, hide admin based on result (make different query)
        // create FireStore query
        Query query = listRef.whereEqualTo("shop", shop.getOwner())
                .orderBy("firstName", Query.Direction.ASCENDING);

        // create FireStore recycler options
        FirestoreRecyclerOptions<StaffMember> options =  new FirestoreRecyclerOptions.Builder<StaffMember>()
                .setQuery(query, StaffMember.class)
                .build();

        // assign ItemAdapter, type is the item type to list
        adapter = new ItemAdapter(options, "staff");

        // get recycler view
        staffRecyclerView = (RecyclerView) findViewById(R.id.staffRecyclerView);



        // create new layout manager - can be set to gridLayout or linerLayout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        // set layout to linear
        staffRecyclerView.setLayoutManager(layoutManager);

        staffRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // set recycler view adapter to ItemAdapter
        staffRecyclerView.setAdapter(adapter);

        // set click listener
        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // create intent
                Intent i = new Intent(getApplicationContext(), StaffMemberDetailActivity.class);

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

        // TODO: show text when no staff members are in list

    }

    /**
     * onSupportNavigateUp -  Return to previous activity/screen
     * @return boolean
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
