package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.MenuEntry;
import com.squareup.picasso.Picasso;
import com.victor.loading.book.BookLoading;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImageConfig;

/**
 * An activity representing a single MenuEntry detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MenuItemListActivity}.
 */
public class MenuItemDetailActivity extends AppCompatActivity {

    // TAG for device logs
    private static final String TAG = " MenuEntryActivity";

    // FireBase database instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // FireBase item reference
    DocumentReference itemRef;

    // FireBase storage instance
    FirebaseStorage storage = FirebaseStorage.getInstance();

    // get storage reference, assign name of file to store image in
    StorageReference storageRef = storage.getReference().child("menu");

    // get image reference
    StorageReference imageRef;

    // Menu item
    MenuEntry menuItem;

    // mode
    String mode;

    // views
    TextView title;
    TextView menuID;
    EditText name;
    EditText price;
    EditText description;
    Switch available;
    Button save;
   // Button saveButton;
    ImageButton deleteButton;

    // loading views
    ConstraintLayout loadLayout;
    BookLoading loadingBar;

    // image variables
    CircleImageView image;
    File newImage = null;
    boolean isImageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuitem_detail);

        // set up custom tool bar
        Toolbar toolbar = findViewById(R.id.menuAppToolbar);
        setSupportActionBar(toolbar);

        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // configure image picker settings
        EasyImage.configuration(this)
                .setImagesFolderName("Coffee Shop")
                .setCopyExistingPicturesToPublicLocation(true);

        // init views
        title = findViewById(R.id.menuDetailTitleTextView);
        menuID = findViewById(R.id.menuIDDetailTextView);
        name = findViewById(R.id.menuNameDetailTextView);
        price = findViewById(R.id.menuPriceEditText);
        description = findViewById(R.id.menuDescriptionEdit);
        available = findViewById(R.id.menuAvailableSwitch);
        image = findViewById(R.id.menuDetailImageView);

        save = findViewById(R.id.addMenubutton);
        deleteButton = findViewById(R.id.menuDeleteButton);

        loadLayout = findViewById(R.id.menuLoadingLayout);
        loadingBar = findViewById(R.id.menuLoadingBar);


        // set listeners
        setClickListeners();

        // get extras from intent from the list
        Intent i = getIntent();
        mode = i.getStringExtra("MODE");

        if (mode.equals("edit")) {
            String path = i.getStringExtra("PATH");
            loadEdit(path);
        } else {
            title.setText("adding new menu item");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mode.equals("edit")) {
            // add FireBase listener
            itemRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    // check for error
                    if (e != null) {
                        Toast.makeText(MenuItemDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                    }

                    // check if document exists
                    if (documentSnapshot.exists()) {
                        // cast to item
                        menuItem = documentSnapshot.toObject(MenuEntry.class);

                        // assign views
                        title.setText("editing '" + menuItem.getName() + "'");
                        menuID.setText("Menu ID: " + documentSnapshot.getId());
                        name.setText(menuItem.getName());
                        price.setText(String.valueOf(menuItem.getPrice()));
                        description.setText(menuItem.getDescription());
                        available.setChecked(menuItem.isAvailable());

                        // TODO: fix bug where image changed does not load into imageview because of real time sync
                    }
                }
            });

        }
    }

    /**
     * onActivityResult     handle result of image picker in this method
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // handle result from image picker
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            //Handle error
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                // show error toast
                Toast.makeText(MenuItemDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                // log error
                Log.d(TAG, e.getMessage());
            }

            // handle image successfully picked
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                // show success toast
                Toast.makeText(MenuItemDetailActivity.this, "Image changed", Toast.LENGTH_SHORT).show();

                // load selected image into image view
                Picasso.get().load(imageFile).into(image);

                // assign image file to public variable
                newImage = imageFile;

                // update is changed boolean
                isImageChanged = true;
            }

            // handle on cancelled
            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                // Cancel handling, remove photo if one was taken but not selected
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MenuItemDetailActivity.this);
                    if (photoFile != null) photoFile.delete();
                }

                // show cancelled toast
                Toast.makeText(MenuItemDetailActivity.this, "No image was selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * setClickListeners        set click listeners for buttons on this activity
     */
    private void setClickListeners() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }



    /**
     * selectImage      method to bring up image picker. allows user to pick image from gallery or take new picture
     *
     * @param
     */
    void selectImage() {
        // open image picker
        EasyImage.openChooserWithGallery(MenuItemDetailActivity.this, "Select an image", EasyImageConfig.REQ_PICK_PICTURE_FROM_GALLERY);
    }

    /**
     * loadEdit     method to load existing data into views
     *
     * @param path this is the FireStore path for the document to fetch
     */
    void loadEdit(String path) {
        // get FireStore document
        itemRef = db.document(path);
        itemRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // check if document exists
                        if (documentSnapshot.exists()) {
                            // cast to item
                            menuItem = documentSnapshot.toObject(MenuEntry.class);

                            // assign view
                            menuID.setText("Menu ID: " + documentSnapshot.getId());
                            name.setText(menuItem.getName());
                            price.setText(String.valueOf(menuItem.getPrice()));
                            description.setText(menuItem.getDescription());
                            available.setChecked(menuItem.isAvailable());

                            if (!menuItem.getImage().equals(""))
                                Picasso.get().load(menuItem.getImage()).into(image);
                        } else {
                            // show does not exists toast
                            Toast.makeText(MenuItemDetailActivity.this, "Item does not exists", Toast.LENGTH_SHORT).show();

                            // redirect back to list
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // show failure toast
                        Toast.makeText(MenuItemDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        // log error
                        Log.e(TAG, e.getMessage());

                        // redirect back to list
                        finish();
                    }
                });

    }

    /**
     * save     method to save changes to FireBase
     *
     * @param
     */
    void save() {
        // TODO: validations

        // show loading bar
        showLoadingBar();

        // get text from views and initialize new menu item with default stock image
        final MenuEntry newMenuEntry = new MenuEntry(name.getText().toString(),Double.parseDouble(price.getText().toString()),
                description.getText().toString(), available.isChecked(),
                "https://firebasestorage.googleapis.com/v0/b/coffee-shop-app-d8f60.appspot.com/o/menu%2Fsp_menu.png?alt=media&token=b93c0adc-511d-4bcd-b7b7-14bc087325f2");

        // check if image was selected or image was changed
        if (isImageChanged) {
            // create reference to FireBase storage with image name as file name
            imageRef = storageRef.child(newImage.getName());

            try {
                // create new input stream from new image
                InputStream stream = new FileInputStream(newImage);

                // upload image and store task in variable
                UploadTask uploadTask = imageRef.putStream(stream);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            // get download uri
                            Uri downloadUri = task.getResult();

                            // set new stock item image to download url
                            newMenuEntry.setImage(downloadUri.toString());

                            // save to FireBase
                            saveToFireBase(newMenuEntry);
                        } else {
                            // show error toast
                            Toast.makeText(MenuItemDetailActivity.this, "getting image download url failed", Toast.LENGTH_SHORT).show();

                            // log error
                            Log.e(TAG, "getting image download url failed");

                            // destroy activity
                            finish();
                        }
                    }
                });
            } catch (Exception e) {
                // show error toast
                Toast.makeText(this, "something went wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();

                // log error
                Log.e(TAG, e.getMessage());

                // destroy activity
                finish();
            }
        } else {
            // check if mode is edit
            if (mode.equals("edit")) {
                // set new stock item image to old stock item image
                newMenuEntry.setImage(menuItem.getImage());
            }
            // save to FireBase
            saveToFireBase(newMenuEntry);
        }
    }

    /**
     * showLoadingBar       method to show/hide loading bar
     */
    private void showLoadingBar() {
        if (!loadingBar.isStart()) {
            // show fullscreen white background
            loadLayout.setVisibility(View.VISIBLE);

            // start loading animation
            loadingBar.start();

            // disable save button to prevent user from trying to save more than once
            save.setEnabled(false);
        } else {
            // hide fullscreen white background
            loadLayout.setVisibility(View.GONE);

            // stop loading animation
            loadingBar.stop();

            // enable save button to prevent user from trying to save more than once
            save.setEnabled(true);
        }
    }

    /**
     * saveToFireBase       method that adds new item to FireBase or updates an existing one based on 'mode' setting
     *
     * @param newMenuEntry custom object to save to FireBase
     */
    private void saveToFireBase(MenuEntry newMenuEntry) {
        // check mode
        switch (mode) {
            // adding new item to FireBase
            case "add":
                db.collection("menu").add(newMenuEntry)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // show success toast
                                Toast.makeText(MenuItemDetailActivity.this, "Stock item added.", Toast.LENGTH_SHORT).show();

                                // redirect back to list
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // show failure toast
                                Toast.makeText(MenuItemDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                // log error
                                Log.e(TAG, "Something went wrong: " + e.getMessage());

                                // hide loading bar
                                showLoadingBar();
                            }
                        });
                break;
            case "edit":
                // update existing item in FireBase
                itemRef.set(newMenuEntry, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // show success toast
                                Toast.makeText(MenuItemDetailActivity.this, "Changes saved successfully.", Toast.LENGTH_SHORT).show();

                                // redirect back to list
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // show failure toast
                                Toast.makeText(MenuItemDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                // log error
                                Log.e(TAG, "Something went wrong: " + e.getMessage());

                                // hide loading bar
                                showLoadingBar();
                            }
                        });
                break;

        }
    }

    /**
     * deleteItem       method to delete item from FireStore
     */
    void deleteItem() {
        // TODO: add dialog to confirm/decline delete
        // check is in edit mode
        if (mode.equals("edit")) {
            // show loading bar
            showLoadingBar();
            itemRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // show success toast
                            Toast.makeText(MenuItemDetailActivity.this, "Item deleted.", Toast.LENGTH_SHORT).show();

                            // redirect back to list
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // show failure toast
                            Toast.makeText(MenuItemDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                            // log error
                            Log.e(TAG, "Something went wrong: " + e.getMessage());

                            // hide loading bar
                            showLoadingBar();
                        }
                    });
        } else {
            // destroy activity
            finish();
        }
    }



   /* @Override
    public boolean onOptionsItemSelected(MenuEntry item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MenuEntryListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    } */
}
