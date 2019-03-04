package com.interyouhunt.hunt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private EditText name;
    private Button createAccountButton;
    FirebaseFirestore db;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.signup_email_edittext);
        name = findViewById(R.id.signup_name_edittext);
        password = findViewById(R.id.signup_password_edittext);

        db = FirebaseFirestore.getInstance();

        createAccountButton = findViewById(R.id.btn_register);
        mProgressDialog = new ProgressDialog(this);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupUser(name.getText().toString().trim(), email.getText().toString().trim(), password.getText().toString());
            }
        });
    }


    protected void signupUser(final String nameString, final String emailString, final String passwordString) {
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(RegistrationActivity.this, "Name is empty", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(emailString) || TextUtils.isEmpty(passwordString)) {
            Toast.makeText(RegistrationActivity.this, "Email or password is empty.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            Toast.makeText(RegistrationActivity.this, "Please enter a valid email.", Toast.LENGTH_LONG).show();
            return;
        }
        if (passwordString.length() < 5) {
            Toast.makeText(RegistrationActivity.this, "Please enter at least 5 characters.", Toast.LENGTH_LONG).show();
            return;
        }
        mProgressDialog.setMessage("Registering...");
        mProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast t = Toast.makeText(RegistrationActivity.this, "Sign up success.",
                                    Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            t.show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(nameString).build();
                            if (user != null) {
                                user.updateProfile(profileUpdate);
                                addUidToFirestore(user);
                            }
                            Intent loginActivity = new Intent(RegistrationActivity.this, LoginActivity.class);
                            RegistrationActivity.this.startActivity(loginActivity);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast t = Toast.makeText(RegistrationActivity.this, "Sign up failed.",
                                    Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            t.show();

                        }

                    }
                });
    }

    protected void addUidToFirestore(FirebaseUser user) {
        String uid = user.getUid();
        final String TAG = "RegistrationActivity";
        Map<String, Object> uidMap = new HashMap<>();
        db.collection("users").document(uid).set(uidMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

}
