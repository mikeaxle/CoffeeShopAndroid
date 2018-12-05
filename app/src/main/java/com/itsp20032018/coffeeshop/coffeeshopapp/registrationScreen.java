package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.Shop;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.StaffMember;

public class registrationScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "registrationScreen";

    //  views
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextStoreName;
    private TextView textViewSignUp;

    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout storeNameInputLayout;

    private ProgressDialog progressDialog;

    // shared preferences helper class
    TinyDB tinyDB;

    // object to store shop
    Shop shop;

    StaffMember staffMember;

    // firebase authentication instance
    private FirebaseAuth   firebaseAuth = FirebaseAuth.getInstance();

    // FireBase database instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private boolean isValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen);

        // init views
        progressDialog = new ProgressDialog(this);
        buttonRegister = findViewById(R.id.buttonRegister);
        editTextEmail = findViewById(R.id.registrationEmailEditText);
        emailInputLayout = findViewById(R.id.registrationEmailInputLayout);
        editTextPassword = findViewById(R.id.registrationPasswordEditText);
        passwordInputLayout = findViewById(R.id.registrationPasswordInputLayout);
        editTextStoreName = findViewById(R.id.registrationStoreNameEditText);
        storeNameInputLayout = findViewById(R.id.registrationStoreNameInputLayout);
        textViewSignUp = findViewById(R.id.alreadyRegisterTextView);

        // add click listeners
        buttonRegister.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();

        // check if user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
            // send to main activity
            Toast.makeText(this, "Logged in as " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    /**
     * Registers user on firebase and sends user to next screen
     */
    private void registerUser() {
        // store input values to strings and trim whitespaces from edit text views
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String storeName = editTextStoreName.getText().toString().trim();


        // validations
        if (isStoreNameValid(storeName) || isEmailValid(email) || isPasswordValid(password)) {
            // set validity to true
            isValid = true;
        } else {
            // set validity to false
            isValid = false;
        }

        // exit if error is detected
        if (!isValid) return;

        // if validation is okay, we will first show a progressbar
        progressDialog.setMessage("Registering user....");
        progressDialog.show();

        // firebase user registration with email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .continueWith(new Continuation<AuthResult, Object>() {
                    @Override
                    public Object then(@NonNull Task<AuthResult> task) throws Exception {
                        // if there is an error
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // init shop object
                        shop = new Shop( storeName, task.getResult().getUser().getUid());

                        // init staff member object
                        staffMember = new StaffMember();
                        staffMember.setShop(task.getResult().getUser().getUid());
                        staffMember.setAddress("Cape Town");
                        staffMember.setFirstName("Store");
                        staffMember.setLastName("Owner");
                        staffMember.setGender("Male");
                        staffMember.setRole("Admin");
                        staffMember.setPhoneNumber("0000000000");
                        staffMember.setEmail(task.getResult().getUser().getEmail());
                        staffMember.setImage("https://firebasestorage.googleapis.com/v0/b/coffee-shop-app-d8f60.appspot.com/o/staff%2Fsp_staff.png?alt=media&token=b879ab06-4c68-4bbf-923a-e4111ecc7616");

                        // create store with UID and name linked, UID is also firebase ID of shop
                        return db.collection("shop").document(shop.getOwner()).set(shop);

                    }
                })
                .continueWith(new Continuation<Object, Object>() {
                    @Override
                    public Object then(@NonNull Task<Object> task) throws Exception {

                        // if there is an error
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // create staff member with admin role
                        return db.collection("staff").add(staffMember);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Object>() {
                    @Override
                    public void onComplete(@NonNull Task<Object> task) {
                        if (task.isSuccessful()) {

                            // store shop in shared preferences
                            tinyDB = new TinyDB(getApplicationContext());
                            tinyDB.putObject("SHOP", shop);

                            //user is successfully registered and logged in we will start the profile activity here
                            Toast.makeText(registrationScreen.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {

                            Toast.makeText(registrationScreen.this, "Could not register please try again", Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(registrationScreen.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.getMessage());
                        progressDialog.dismiss();
                    }
                });
    }

    /**
     * Validates store name edit text
     * @param storeName
     * @return boolean
     */
    private boolean isStoreNameValid(String storeName) {
        if (TextUtils.isEmpty(storeName)) {

            // show error
            storeNameInputLayout.setError("Please enter a store name");

            // set validity to false
            return false;

        } else {

            // set error to null
            storeNameInputLayout.setError(null);

            // set validity to true
            return true;
        }
    }

    /**
     * Validates password edit text
     * @param password
     * @return boolean
     */
    private boolean isPasswordValid(String password) {
        if (TextUtils.isEmpty(password)) {
            // show error
            passwordInputLayout.setError("Please enter a password");

            // set validity to false
            return false;

        } else {

            // set error to null
            passwordInputLayout.setError(null);

            // set validity to true
            return true;
        }
    }

    /**
     * Validates email edit text
     * @param email
     * @return boolean
     */
    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            // show error
            emailInputLayout.setError("Please enter an email address");

            // set validity to false
            return false;

        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            // show error
            emailInputLayout.setError("Please enter a valid email address");

            // set validity to false
            return  false;
        } else {

            // set error to null
            emailInputLayout.setError(null);

            // set validity to true
            return true;

        }
    }


    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            registerUser();
        }

        if (v == textViewSignUp) {
            startActivity(new Intent(this, LoginScreen.class));
        }
    }
}
