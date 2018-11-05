package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.itsp20032018.coffeeshop.coffeeshopapp.adapters.ItemAdapter;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.MenuEntry;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.Order;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.Shop;
import com.itsp20032018.coffeeshop.coffeeshopapp.transforms.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.Date;
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

    // material design dialog
    MaterialDialog dialog;

    // layout inflater for dialog
    LayoutInflater inflater;

    // View for custom dialog
    View dialogView;

    // custom adapter
    private ItemAdapter adapter;

    //  views for dialog
    TextView dialogName;
    TextView dialogPrice;
    ImageView dialogImage;
    TextView dialogQuantity;
    SeekBar dialogSeek;
    Button dialogAddToOrder;
    Button dialogEditMenu;
    ImageButton dialogClose;

    // dialog quantity variables
    int min, max, current;

    // object to current store object
    public Order currentOrder;


    // object to store shop details
    Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuitem_list);

        // set up custom tool bar
        Toolbar appToolbar = findViewById(R.id.menuAppToolbar);
        setSupportActionBar(appToolbar);

        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // get shop details
        shop = new TinyDB(getApplicationContext()).getObject("SHOP", Shop.class);

        // init inflater
        inflater = LayoutInflater.from(this);

        // init custom dialog
        dialogView = inflater.inflate(R.layout.add_to_order_dialog, null);

        // init dialog
        dialog = new MaterialDialog.Builder(this)
                .customView(dialogView, false)
                .build();

        // init order
        currentOrder = new Order();
        currentOrder.setShop(shop.getOwner());

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
                .orderBy("name", Query.Direction.ASCENDING);

        // create FireStore recycler options
        FirestoreRecyclerOptions<MenuEntry> options = new FirestoreRecyclerOptions.Builder<MenuEntry>()
                .setQuery(query, MenuEntry.class)
                .build();

        // assign ItemAdapter, type is the item type to list
        adapter = new ItemAdapter(options, "menu");

        // get recycler view
        menuRecyclerView = findViewById(R.id.menuRecyclerView);

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

                // get reference to object and store in string
                final String path = documentSnapshot.getReference().getPath();

                // object to store document
                final MenuEntry menuItem = documentSnapshot.toObject(MenuEntry.class);

                // create views for dialog
                dialogName = dialogView.findViewById(R.id.menuDialogNameTextView);
                dialogPrice = dialogView.findViewById(R.id.menuDialogPriceTextView);
                dialogImage = dialogView.findViewById(R.id.menuDialogImageView);
                dialogQuantity = dialogView.findViewById(R.id.menuDialogQuantityTextView);
                dialogSeek = dialogView.findViewById(R.id.menuDialogSeekBar);
                dialogAddToOrder = dialogView.findViewById(R.id.menuDialogAddOrderbutton);
                dialogEditMenu = dialogView.findViewById(R.id.menuDialogEditOrderbutton);
                dialogClose = dialogView.findViewById(R.id.menuDialogCloseImageButton);

                /**
                 *  set up views
                 */
                // add button click listeners
                dialogAddToOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // set menu item quantity
                        menuItem.setQuantity(current);

                        // add to order array list
                        currentOrder.orderItems.add(menuItem);

                        // show toast to user
                        Toast.makeText(MenuItemListActivity.this, "(x" + menuItem.getQuantity() + ")" + menuItem.getName() + " added to order", Toast.LENGTH_SHORT).show();

                        // close dialog
                        dialog.dismiss();
                    }
                });
                dialogEditMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // go to edit screen
                        // create intent
                        Intent i = new Intent(getApplicationContext(), MenuItemDetailActivity.class);

                        // add extras
                        i.putExtra("PATH", path);

                        // set mode to edit
                        i.putExtra("MODE", "edit");

                        // start activity
                        startActivity(i);

                        // destroy modal
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

                // assign  text views
                dialogName.setText(menuItem.getName());
                dialogPrice.setText("R" + menuItem.getPrice());
                Picasso.get().load(menuItem.getImage())
                        .centerCrop()
                        .transform(new CircleTransform(15, 0))
                        .fit()
                        .placeholder(R.drawable.rounded_corner_placeholder)
                        .into(dialogImage);

                // set up seek bar + quantity text view
                min = 1;
                max = 10;
                current = 1;
                dialogSeek.setMax(max - min);
                dialogSeek.setProgress(current - min);
                dialogQuantity.setText("Quantity: " + current);

                // add seek bar listeners
                dialogSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        current = progress + min;
                        dialogQuantity.setText("Quantity: " + current);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                // show dialog
                dialog.show();
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
        if(currentOrder.orderItems.size() == 0) {
            Toast.makeText(this, "Please add items to order.", Toast.LENGTH_SHORT).show();
            return;
        }

        // add time stamp
        currentOrder.setTimestamp(new Date());

        Toast.makeText(this, "order created", Toast.LENGTH_SHORT).show();

        //add order to FireBase
        db.collection("orders")
                .add(currentOrder)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        // check if successful
                        if(task.isSuccessful()) {
                            // go to order list
                            Intent i = new Intent(getApplicationContext(), OrderListActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(MenuItemListActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuItemListActivity.this, "something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.getMessage() );
                    }
                });
    }

    /**
     * addNewMenuItem       method to go to add new menu item screen
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
