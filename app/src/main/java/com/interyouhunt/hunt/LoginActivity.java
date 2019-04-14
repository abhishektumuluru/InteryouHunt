package com.interyouhunt.hunt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private TextView signupLink;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    private Button registrationButton;

    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressDialog = new ProgressDialog(this);
        emailField = findViewById(R.id.input_email);
        passwordField = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);
        signupLink = findViewById(R.id.link_signup);
        mAuth = FirebaseAuth.getInstance();

        emailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        passwordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        signupLink.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable spans = (Spannable) signupLink.getText();
        ClickableSpan clickSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget)
            {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        };
        spans.setSpan(clickSpan, 16, spans.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

        registrationButton = findViewById(R.id.btn_register);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistrationActivity();
            }


        });
    }

    protected void signInUser() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Please enter your username and password.", Toast.LENGTH_LONG).show();
            return;
        }
        mProgressDialog.setMessage("Logging in...");
        mProgressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mProgressDialog.dismiss();
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "There was a problem signing in.", Toast.LENGTH_LONG).show();
                } else {
                    startHomeActivity();
                }

            }
        });
    }
    private void startHomeActivity() {
        this.startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void startRegistrationActivity() {
        this.startActivity(new Intent(this, RegistrationActivity.class));

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
