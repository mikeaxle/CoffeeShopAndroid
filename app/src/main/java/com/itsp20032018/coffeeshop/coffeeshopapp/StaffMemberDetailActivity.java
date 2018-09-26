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
import android.widget.Spinner;
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
import com.itsp20032018.coffeeshop.coffeeshopapp.model.StaffMember;
import com.squareup.picasso.Picasso;
import com.victor.loading.book.BookLoading;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImageConfig;

/**
 * An activity representing a single Staff Member detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link StaffMemberListActivity}.
 */
public class StaffMemberDetailActivity extends AppCompatActivity {

    // TAG for device logs
    private static final String TAG = "StaffMemberActivity";

    // FireBase database instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // FireBase list reference
    DocumentReference itemRef;

    // FireBase storage instance
    FirebaseStorage storage = FirebaseStorage.getInstance();

    // get storage reference, assign name of file to store image in
    StorageReference storageRef = storage.getReference().child("staff");

    // get image reference
    StorageReference imageRef;

    // staff member
    StaffMember staffMember;

    // mode
    String mode;

    // views
    TextView title;
    TextView staffID;
    EditText firstName;
    EditText lastName;
    Spinner role;
    Spinner gender;
    EditText address;
    EditText phoneNumber;
    EditText emailAddress;
    Button save;

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
        setContentView(R.layout.activity_staffmember_detail);

        // set up custom tool bar
        Toolbar appToolbar = (Toolbar) findViewById(R.id.staffAppToolbar);
        setSupportActionBar(appToolbar);

        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // configure image picker settings
        EasyImage.configuration(this)
                .setImagesFolderName("Coffee Shop")
                .setCopyExistingPicturesToPublicLocation(true);

        // get extras from intent
        Intent i = getIntent();
        mode = i.getStringExtra("MODE");

        // init views
        title = (TextView) findViewById(R.id.staffDetailTitleTextView);
        staffID = (TextView) findViewById(R.id.staffIDDetailTextView);
        firstName = (EditText) findViewById(R.id.staffNameDetailEditText);
        lastName = (EditText) findViewById(R.id.staffSurnameDetailEditText);
        role = (Spinner) findViewById(R.id.staffJobRoleSpinner);
        gender = (Spinner) findViewById(R.id.staffGenderSpinner);
        address = (EditText) findViewById(R.id.staffAddressTextEdit);
        phoneNumber = (EditText) findViewById(R.id.staffPhoneEditText);
        emailAddress = (EditText) findViewById(R.id.staffEmailEditText);


        image = (CircleImageView) findViewById(R.id.staffDetailImageView);
        save = (Button) findViewById(R.id.addStaffbutton);
        loadLayout = (ConstraintLayout) findViewById(R.id.staffLoadingLayout);
        loadingBar = (BookLoading) findViewById(R.id.staffLoadingBar);

        if (mode.equals("edit")) {
            String path = i.getStringExtra("PATH");
            loadEdit(path);
        } else {
            title.setText("adding new staff member");
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
                        Toast.makeText(StaffMemberDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                    }

                    // check if document exists
                    if (documentSnapshot.exists()) {
                        // cast to item
                        staffMember = documentSnapshot.toObject(StaffMember.class);

                        // assign views
                        title.setText("editing '" + staffMember.getFirstName() + " " + staffMember.getLastName() + "'");
                        staffID.setText("staff ID: " + documentSnapshot.getId());
                        firstName.setText(staffMember.getFirstName());
                        lastName.setText(staffMember.getLastName());
                        role.setSelection(getIndexOfItem("role"), true);
                        gender.setSelection(getIndexOfItem("gender"), true);
                        address.setText(staffMember.getAddress());
                        phoneNumber.setText(staffMember.getPhoneNumber());
                        emailAddress.setText(staffMember.getEmailAddress());

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
                Toast.makeText(StaffMemberDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                // log error
                Log.d(TAG, e.getMessage());
            }

            // handle image successfully picked
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                // show success toast
                Toast.makeText(StaffMemberDetailActivity.this, "Image changed", Toast.LENGTH_SHORT).show();

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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(StaffMemberDetailActivity.this);
                    if (photoFile != null) photoFile.delete();
                }

                // show cancelled toast
                Toast.makeText(StaffMemberDetailActivity.this, "No image was selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * selectImage      method to bring up image picker. allows user to pick image from gallery or take new picture
     *
     * @param view
     */
    void selectImage(View view) {
        // open image picker
        EasyImage.openChooserWithGallery(StaffMemberDetailActivity.this, "Select an image", EasyImageConfig.REQ_PICK_PICTURE_FROM_GALLERY);
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
                            staffMember = documentSnapshot.toObject(StaffMember.class);

                            // assign view
                            staffID.setText("staff ID: " + documentSnapshot.getId());
                            firstName.setText(staffMember.getFirstName());
                            lastName.setText(staffMember.getLastName());
                            role.setSelection(getIndexOfItem("role"), true);
                            gender.setSelection(getIndexOfItem("gender"), true);
                            address.setText(staffMember.getAddress());
                            phoneNumber.setText(staffMember.getPhoneNumber());
                            emailAddress.setText(staffMember.getEmailAddress());

                            if (!staffMember.getImage().equals(""))
                                Picasso.get().load(staffMember.getImage()).into(image);
                        } else {
                            // show does not exists toast
                            Toast.makeText(StaffMemberDetailActivity.this, "Item does not exists", Toast.LENGTH_SHORT).show();

                            // redirect back to list
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // show failure toast
                        Toast.makeText(StaffMemberDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        // log error
                        Log.e(TAG, e.getMessage());

                        // redirect back to list
                        finish();
                    }
                });

    }

    /**
     * getIndexOfItem       helper method to get index selected gender or job role
     * @param type          type of array to search
     * @return
     */
    private int getIndexOfItem(String type) {
        int currentRole = 0;
        String tempArray[];
        switch (type){
            case "role":
                 tempArray = getApplicationContext().getResources().getStringArray(R.array.job_role_array);
                currentRole = Arrays.asList(tempArray).lastIndexOf(staffMember.getRole());
                break;
            case "gender":
                tempArray = getApplicationContext().getResources().getStringArray(R.array.gender_array);
                currentRole = Arrays.asList(tempArray).lastIndexOf(staffMember.getGender());
                break;
        }

        return currentRole;
    }

    /**
     * save     method to save changes to FireBase
     *
     * @param view
     */
    void save(View view) {
        // TODO: validations

        // show loading bar
        showLoadingBar();

        // get text from views and initialize new staff item with default staff image
        final StaffMember newStaffMember = new StaffMember(firstName.getText().toString(), lastName.getText().toString(),
                role.getSelectedItem().toString(), gender.getSelectedItem().toString(), address.getText().toString(),
                phoneNumber.getText().toString(), emailAddress.getText().toString(), "https://firebasestorage.googleapis.com/v0/b/coffee-shop-app-d8f60.appspot.com/o/staff%2Fsp_staff.png?alt=media&token=b879ab06-4c68-4bbf-923a-e4111ecc7616");

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

                            // set new staff member image to download url
                            newStaffMember.setImage(downloadUri.toString());

                            // save to FireBase
                            saveToFireBase(newStaffMember);
                        } else {
                            // show error toast
                            Toast.makeText(StaffMemberDetailActivity.this, "getting image download url failed", Toast.LENGTH_SHORT).show();

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
                // set new staff memberimage to old staff member image
                newStaffMember.setImage(staffMember.getImage());
            }
            // save to FireBase
            saveToFireBase(newStaffMember);
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
     * @param newStaffMember custom object to save to FireBase
     */
    private void saveToFireBase(StaffMember newStaffMember) {
        // check mode
        switch (mode) {
            // adding new item to FireBase
            case "add":
                db.collection("staff").add(newStaffMember)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // show success toast
                                Toast.makeText(StaffMemberDetailActivity.this, "Stock item added.", Toast.LENGTH_SHORT).show();

                                // redirect back to list
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // show failure toast
                                Toast.makeText(StaffMemberDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                // log error
                                Log.e(TAG, "Something went wrong: " + e.getMessage());

                                // hide loading bar
                                showLoadingBar();
                            }
                        });
                break;
            case "edit":
                // update existing item in FireBase
                itemRef.set(newStaffMember, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // show success toast
                                Toast.makeText(StaffMemberDetailActivity.this, "Changes saved successfully.", Toast.LENGTH_SHORT).show();

                                // redirect back to list
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // show failure toast
                                Toast.makeText(StaffMemberDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();

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
    void deleteItem(View view) {
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
                            Toast.makeText(StaffMemberDetailActivity.this, "Staff member deleted.", Toast.LENGTH_SHORT).show();

                            // redirect back to list
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // show failure toast
                            Toast.makeText(StaffMemberDetailActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();

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

}
