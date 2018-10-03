package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
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
    private TextView textViewSignUp;

    private ProgressDialog progressDialog;

    // firebase authentication
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen);

        // init views

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        buttonRegister = findViewById(R.id.buttonRegister);
        editTextEmail = findViewById(R.id.emailRegisterEditText);
        editTextPassword = findViewById(R.id.passwordRegisterEditText);

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
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email))
        {
            //email is empty
            Toast.makeText(this, "Plese enter email", Toast.LENGTH_SHORT).show();
            //stop the function execution further
            return;
        }

        if (TextUtils.isEmpty(password))
        {
            // password is empty
            Toast.makeText(this, "Plese enter password", Toast.LENGTH_SHORT).show();
            //stop the function execution further
            return;
        }

        // if validation is okay
        // we will first show a progressbar

        progressDialog.setMessage("Registering user....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                           // updateUI(user);
                            finish();
                            //user is successfully registred and logged in
                            //we will start the profile activity here
                           Toast.makeText(registrationScreen.this, "Registered succesfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent (getApplicationContext(), MainActivity.class));
                        }else
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
