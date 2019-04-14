package com.interyouhunt.hunt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddPositionActivity extends AppCompatActivity {

    private final String TAG = "AddPositionActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button addPositionButton;
    private boolean isEditing;

    private HashMap<String, Object> data;

    private ProgressDialog mProgressDialog;

    // Info to store
    private EditText companyName;
    private EditText position;
    private EditText positionType;
    private EditText recruiterEmail;
    private EditText recruiterName;
    private EditText recruiterPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_position);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        addPositionButton = findViewById(R.id.btn_add_position);
        addPositionButton.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);

        companyName = findViewById(R.id.input_company);
        position = findViewById(R.id.input_position);
        positionType = findViewById(R.id.input_position_type);
        recruiterEmail = findViewById(R.id.input_recruiter_email);
        recruiterName = findViewById(R.id.input_recruiter_name);
        recruiterPhoneNumber = findViewById(R.id.input_recruiter_phone);

        companyName.addTextChangedListener(watcher);
        position.addTextChangedListener(watcher);

        companyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        position.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        positionType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        recruiterName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        recruiterEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        recruiterPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        addPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> interviewInfo = loadFields();
                mProgressDialog.setMessage("Adding position");
                mProgressDialog.show();
                writeToFirestore(interviewInfo);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            data = (HashMap<String, Object>) extras.getSerializable("interviewMap");
            preFillFields();
            isEditing = true;
            addPositionButton.setText("Edit Position");
        } else {
            isEditing = false;
        }
    }

    private void preFillFields() {
        final String companyNameData = (String) data.get("companyName");
        final String positionData = (String) data.get("position");
        final String positionTypeData = (String) data.get("positionType");
        final String recruiterEmailData = (String) data.get("recruiterEmail");
        final String recruiterNameData = (String) data.get("recruiterName");
        final String recruiterPhoneNumberData = (String) data.get("recruiterPhoneNumber");
        companyName.setText(companyNameData, TextView.BufferType.EDITABLE);
        position.setText(positionData, TextView.BufferType.EDITABLE);
        positionType.setText(positionTypeData, TextView.BufferType.EDITABLE);
        recruiterEmail.setText(recruiterEmailData, TextView.BufferType.EDITABLE);
        recruiterName.setText(recruiterNameData, TextView.BufferType.EDITABLE);
        recruiterPhoneNumber.setText(recruiterPhoneNumberData, TextView.BufferType.EDITABLE);
    }

    private Map<String, Object> loadFields() {

        // end info to store
        Map<String, Object> interviewInfo = new HashMap<>();
        interviewInfo.put("companyName", companyName.getText().toString());
        interviewInfo.put("position", position.getText().toString());
        interviewInfo.put("positionType", positionType.getText().toString());
        interviewInfo.put("recruiterEmail", recruiterEmail.getText().toString());
        interviewInfo.put("recruiterName", recruiterName.getText().toString());
        interviewInfo.put("recruiterPhoneNumber", recruiterPhoneNumber.getText().toString());
        interviewInfo.put("stages", Arrays.asList());
        return interviewInfo;
    }


    private void writeToFirestore(final Map<String, Object> interviewInfo) {
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        DocumentReference doc;
        final String successMessage;
        final String failureMessage;
        if (isEditing) {
            successMessage = "Edited position";
            failureMessage = "Error editing position";
            String docID = (String) data.get("docID");
            doc = db.collection("users").document(uid).collection("Interviews").document(docID);
            interviewInfo.put("stages", data.get("stages"));
        } else {
            successMessage = "Added position";
            failureMessage = "Error adding position";
            doc = db.collection("users").document(uid).collection("Interviews").document();
        }
        doc.set(interviewInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mProgressDialog.dismiss();
                Log.d(TAG, "DocumentSnapshot successfully written!");
                Toast.makeText(AddPositionActivity.this, successMessage, Toast.LENGTH_LONG).show();
                Bundle extras = new Bundle();
                if (isEditing) {
                    data.put("companyName", interviewInfo.get("companyName"));
                    data.put("position", interviewInfo.get("position"));
                    data.put("positionType", interviewInfo.get("positionType"));
                    data.put("recruiterEmail", interviewInfo.get("recruiterEmail"));
                    data.put("recruiterName", interviewInfo.get("recruiterName"));
                    data.put("recruiterPhoneNumber", interviewInfo.get("recruiterPhoneNumber"));
                    Intent intent = new Intent(AddPositionActivity.this, intActivity.class);
                    extras.putSerializable("interviewMap", data);
                    extras.putString("toastSuccessMessage", successMessage);
                    intent.putExtras(extras);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(AddPositionActivity.this, HomeActivity.class);
                    extras.putString("toastSuccessMessage", successMessage);
                    intent.putExtras(extras);
                    startActivity(intent);
                    finish();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, failureMessage, e);
                        mProgressDialog.dismiss();
                    }
                });
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            if (companyName.getText().toString().length() == 0 || position.getText().toString().length() == 0) {
                addPositionButton.setEnabled(false);
            } else {
                addPositionButton.setEnabled(true);
            }
        }
    };

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
