package com.interyouhunt.hunt;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddCompanyActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button addCompanyButton;
//    private EditText dateEditText;
//    private Calendar myCalendar;
//    private TimePicker timePicker;

    private ProgressDialog mProgressDialog;


    // Info to store
    private EditText companyName;
    private EditText position;
    private EditText positionType;
    private EditText recruiterEmail;
    private EditText recruiterName;
    private EditText recruiterPhoneNumber;

//    private String time;
//    private String date;
//    private String notes;
//    private String location;
//    private String feedback;
//    private String passed;
//    private String interviewStage;
//    private String interviewType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        addCompanyButton = findViewById(R.id.btn_add_company);
        addCompanyButton.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);

        companyName = findViewById(R.id.input_company);
        position = findViewById(R.id.input_position);
        positionType = findViewById(R.id.input_position_type);
        recruiterEmail = findViewById(R.id.input_recruiter_email);
        recruiterName = findViewById(R.id.input_recruiter_name);
        recruiterPhoneNumber = findViewById(R.id.input_recruiter_phone);


        companyName.addTextChangedListener(watcher);
        position.addTextChangedListener(watcher);

        addCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> interviewInfo = loadFields();
                mProgressDialog.setMessage("Adding company");
                mProgressDialog.show();
                writeToFirestore(interviewInfo);
            }
        });

//        timePicker = findViewById(R.id.input_time_picker);
//        dateEditText = (EditText) findViewById(R.id.input_date);
//        myCalendar = Calendar.getInstance();
//        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel();
//            }
//
//        };
//        dateEditText.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                new DatePickerDialog(AddCompanyActivity.this, date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });

    }

    private Map<String, String> loadFields() {

        // end info to store
        Map<String, String> interviewInfo = new HashMap<>();
        interviewInfo.put("companyName", companyName.getText().toString());
        interviewInfo.put("position", position.getText().toString());
        interviewInfo.put("positionType", positionType.getText().toString());
        interviewInfo.put("recruiterEmail", recruiterEmail.getText().toString());
        interviewInfo.put("recruiterName", recruiterName.getText().toString());
        interviewInfo.put("recruiterPhoneNumber", recruiterPhoneNumber.getText().toString());
        return interviewInfo;

//        time = "" + timePicker.getHour() + ":" + timePicker.getMinute();
//        date = dateEditText.getText().toString();
//        notes = ((EditText)findViewById(R.id.input_notes)).getText().toString();
//        location = ((EditText)findViewById(R.id.input_location)).getText().toString();
//        feedback = ((EditText)findViewById(R.id.input_feedback)).getText().toString();
//        interviewStage = ((EditText)findViewById(R.id.input_stage)).getText().toString();
//        interviewType = ((EditText)findViewById(R.id.input_interview_type)).getText().toString();

//        stageInfo.put("date", date);
//        stageInfo.put("notes", notes);
//        stageInfo.put("location", location);
//        stageInfo.put("time", time);
//        stageInfo.put("feedback", feedback);
//        stageInfo.put("interviewStage", interviewStage);
//        stageInfo.put("interviewType", interviewType);
    }


    private void writeToFirestore(Map<String, String> interviewInfo) {
        FirebaseUser user = mAuth.getCurrentUser();
        final String TAG = "AddCompanyActivity";
        String uid = user.getUid();

        db.collection("users").document(uid).collection("Interviews").document().set(interviewInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                Toast.makeText(AddCompanyActivity.this, "Added company", Toast.LENGTH_LONG).show();
                AddCompanyActivity.this.startActivity(new Intent(AddCompanyActivity.this, HomeActivity.class));
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
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
            if (companyName.getText().toString().length() == 0 || position.getText().toString().length() == 0 ) {
                addCompanyButton.setEnabled(false);
            } else {
                addCompanyButton.setEnabled(true);
            }
        }
    };

    //    private void updateLabel() {
//        String myFormat = "MM/dd/yy"; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//
//        dateEditText.setText(sdf.format(myCalendar.getTime()));
//    }

}
