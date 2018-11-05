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
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.Shop;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.StaffMember;

import java.util.ArrayList;
import java.util.List;

public class LoginScreen extends AppCompatActivity {

    String TAG = "LoginScreen";

    //TODO: add user access control

    // views

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;

    String email;
    String password;


    private ProgressDialog progressDialog;

    // firebase authentication
    private FirebaseAuth firebaseAuth;

    // FireBase database instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // shared preferences helper
    TinyDB tinyDB;

    // shop object
    Shop shop;

    ArrayList<StaffMember> staffList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // init views
        progressDialog = new ProgressDialog(this);

        editTextEmail = findViewById(R.id.emailLoginEditText);
        editTextPassword = findViewById(R.id.passwordLoginEditText);

        textInputEmail = findViewById(R.id.emailInputLayout);
        textInputPassword = findViewById(R.id.passwordInputLayout);

        buttonSignIn = findViewById(R.id.loginButton);


        // set login button click listener
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // check if user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
            // send to main activity
            Toast.makeText(this, "Logged in as " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        // get entire staff list
        db.collection("staff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<StaffMember>  _staffList = task.getResult().toObjects(StaffMember.class);
                            staffList = new ArrayList<>();
                            staffList.addAll(_staffList);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginScreen.this, "Please connect your phone to the internet", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void userLogin() {
        // get email address from text edit
        email = editTextEmail.getText().toString().trim();

        // check if email is valid.
        if (!isEmailValid(email)) return;


        // show a progressbar
        progressDialog.setMessage("Checking for user....");
        progressDialog.show();

        // check sign in methods for email
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        // dismiss progress dialog
                        progressDialog.dismiss();

                        // check if task was successful
                        if(task.isSuccessful()){
                            // get result
                            SignInMethodQueryResult result = task.getResult();

                            // get sign in methods and add to string List
                            List<String> signInMethods = result.getSignInMethods();

                            // check for email/password sign in (admins)
                            if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                                // User can sign in with email/password
                                Toast.makeText(LoginScreen.this, email + " is Admin", Toast.LENGTH_SHORT).show();

                                // show password field
                                textInputPassword.setVisibility(View.VISIBLE);

                                // change listener of login button
                                buttonSignIn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        emailPasswordSignIn();
                                    }
                                });


                                // check for email link sign in (employees)
                            } else if (signInMethods.contains(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD)) {
                                // User can sign in with email/link
                                Toast.makeText(LoginScreen.this, email + " is Employee", Toast.LENGTH_SHORT).show();

                                // login user
                                emailLinkSignIn();

                                // change listener of login button
                                buttonSignIn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        emailLinkSignIn();
                                    }
                                });

                            } else {
                                Toast.makeText(LoginScreen.this, "There is no user linked to the email: " + email, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Error getting sign in methods for email: " + task.getException());

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginScreen.this, "An error has occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                });


    }

    /**
     *  signs user in via email link
     */
    private void emailLinkSignIn(){
        // get intent
        Intent intent = getIntent();

        // check if intent is null
        if(intent != null) {
            String emailLink = intent.getData().toString();

            // Confirm the link is a sign-in with email link.
            if (firebaseAuth.isSignInWithEmailLink(emailLink)) {

                // Retrieve this from wherever you stored it
//                String email = "someemail@domain.com";

                // The client SDK will parse the code from the link for you.
                firebaseAuth.signInWithEmailLink(email, emailLink)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Successfully signed in with email link!");
                                    AuthResult result = task.getResult();
                                    // You can access the new user via result.getUser()
                                    // Additional user info profile *not* available via:
                                    // result.getAdditionalUserInfo().getProfile() == null
                                    // You can check if the user is new or existing:
                                    // result.getAdditionalUserInfo().isNewUser()

                                    // write shop to local storage
                                    tinyDB = new TinyDB(getApplicationContext());
                                    tinyDB.putObject("SHOP", new Shop("Coffee Shop", "QJBz3oHZIzTvfVdUTJUhhsamqyi2"));

                                    // destroy activity and send to main screen
                                    finish();
                                    Toast.makeText(LoginScreen.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                } else {
                                    Log.e(TAG, "Error signing in with email link", task.getException());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginScreen.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            // instruct user to open sign in email
            Toast.makeText(this, "Please click the email sign in link sent to " + email + " on this device.", Toast.LENGTH_LONG).show();
        }

    }


    /**
     * logs user into firebase with email and password
     */
    private void emailPasswordSignIn(){
        // get password from textedit
        password = editTextPassword.getText().toString().trim();

        // check if password is entered
        if(!isPasswordValid(password)) return;

        // show a progressbar
        progressDialog.setMessage("Logging in user....");
        progressDialog.show();

        // firebase auth login with username and password
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .continueWithTask(new Continuation<AuthResult, Task<QuerySnapshot>>() {
                    @Override
                    public Task<QuerySnapshot> then(@NonNull Task<AuthResult> task) throws Exception {
                        // if there is an error
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        String shopID = "";

                        for(StaffMember member: staffList){
                            if(email.equals(member.getEmailAddress())){
                                shopID = member.getShop();
                            }
                        }

                        // get shop from FireStore
                        return db.collection("shop")
                                .whereEqualTo("owner", shopID).limit(1)
                                .get();

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // dismiss progress dialog
                        progressDialog.dismiss();

                        // check if task was successful
                        if (task.isSuccessful()) {

                            // loop through results
                            for(DocumentSnapshot documentSnapshot: task.getResult()){

                                // get shop
                                shop = documentSnapshot.toObject(Shop.class);
                            }

                            // write shop to local storage
                            tinyDB = new TinyDB(getApplicationContext());
                            tinyDB.putObject("SHOP", shop);

                            // destroy activity and send to main screen
                            finish();
                            Toast.makeText(LoginScreen.this, "Login successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginScreen.this, "Something went wrong: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginScreen.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.getMessage());

                    }
                });
    }

    /**
     * validates password edit text
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        if (TextUtils.isEmpty(password)) {
            // password is empty
            textInputPassword.setError("Please enter password");

            // return false
            return false;
        } else {
            // set error to null
            textInputPassword.setError(null);

            // return true
            return true;
        }
    }

    /**
     * validates email edit text
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {

            textInputEmail.setError("Please enter email");

            return false;

        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            textInputEmail.setError("Please enter a valid email address");

            return  false;

        } else {

            textInputEmail.setError(null);

            return true;
        }
    }

//    @Override
//    public void onClick(View v) {
//
//        if (v == buttonSignIn) {
//            userLogin();
//        }
//
//        //  if (v = textViewSignUp ){
//        //    finish();
//        // startActivity(new Intent(this, LoginScreen.class));
//        //}
//
//    }
}
