package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class registrationScreen extends AppCompatActivity implements View.OnClickListener {

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

    // firebase authentication
    private FirebaseAuth firebaseAuth;

    private boolean isValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen);

        // init views

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        buttonRegister = findViewById(R.id.buttonRegister);

        editTextEmail = findViewById(R.id.registrationEmailEditText);
        emailInputLayout = findViewById(R.id.registrationEmailInputLayout);

        editTextPassword = findViewById(R.id.registrationPasswordEditText);
        passwordInputLayout = findViewById(R.id.registrationPasswordInputLayout);

        editTextStoreName = findViewById(R.id.registrationStoreNameEditText);
        storeNameInputLayout =  findViewById(R.id.registrationStoreNameInputLayout);

        textViewSignUp = findViewById(R.id.alreadyRegisterTextView);


        buttonRegister.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
       // updateUI(currentUser);
    }

    private void registerUser(){
        // store input values to strings and trim whitespaces from edit text views
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String storeName = editTextStoreName.getText().toString().trim();


        // validations
        if (TextUtils.isEmpty(email))
        {
            // show error
            emailInputLayout.setError("Please enter an email address");

            editTextEmail.setText(null);

            // set validity to false
            isValid = false;

        } else {

            // set error to null
            emailInputLayout.setError(null);

            // set validity to true
            isValid = true;

        }

        if (TextUtils.isEmpty(password))
        {
            // show error
            passwordInputLayout.setError("Please enter a password");

            editTextPassword.setText(null);

            // set validity to false
            isValid = false;

        } else {

            // set error to null
            passwordInputLayout.setError(null);

            // set validity to true
            isValid = true;
        }

        if(TextUtils.isEmpty(storeName)){

            // show error
            storeNameInputLayout.setError("Please enter a store name");

            editTextStoreName.setText(null);

            // set validity to false
            isValid = false;

        } else {

            // set error to null
            storeNameInputLayout.setError(null);

            // set validity to true
            isValid = true;
        }

        // exit if error is detected
        if(!isValid) return;

        // if validation is okay
        // we will first show a progressbar



        progressDialog.setMessage("Registering user....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                           // updateUI(user);
                            finish();

                            //user is successfully registred and logged in we will start the profile activity here
                            //user is successfully registred and logged in
                            //we will start the profile activity here

                           Toast.makeText(registrationScreen.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent (getApplicationContext(), MainActivity.class));
                        } else
                        {
                            Toast.makeText(registrationScreen.this, "Could not register please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (v == buttonRegister )
        {
            registerUser();
        }

        if (v == textViewSignUp)
        {
            startActivity(new Intent(this, LoginScreen.class));
        }
    }
}
