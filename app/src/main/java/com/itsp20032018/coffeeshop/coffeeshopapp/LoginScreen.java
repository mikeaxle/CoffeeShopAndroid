package com.itsp20032018.coffeeshop.coffeeshopapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    //TODO: add user access control

    // views

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;


    private ProgressDialog progressDialog;

    // firebase authentication
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // init views

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser()!= null){
            //profile activity here
        }

        progressDialog = new ProgressDialog(this);

        editTextEmail = findViewById(R.id.emailLoginEditText);
        editTextPassword = findViewById(R.id.passwordLoginEditText);
        buttonSignIn = findViewById(R.id.loginButton);

        // textViewSignUp = findViewById(R.id.textViewSignUp);

        buttonSignIn.setOnClickListener(this);
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email))
        {
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stop the function execution further
            return;
        }

        if (TextUtils.isEmpty(password))
        {
            // password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stop the function execution further
            return;
        }

        // if validation is okay
        // we will first show a progressbar

        progressDialog.setMessage("Login user....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()){
                            finish();
                            Toast.makeText(LoginScreen.this, "Login successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent (getApplicationContext(), MainActivity.class));
                        }else
                        {
                            Toast.makeText(LoginScreen.this, "Please make sure that the email and password are correct", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {

        if (v == buttonSignIn){
            userLogin();
        }

        //  if (v = textViewSignUp ){
        //    finish();
        // startActivity(new Intent(this, LoginScreen.class));
        //}

    }
}
