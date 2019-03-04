package com.interyouhunt.hunt;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private EditText dateEditText;
    private Calendar myCalendar;
    private TimePicker timePicker;
    private Button addCompanyButton;
    private EditText positionEditText;

    private ProgressDialog mProgressDialog;



    // Info to store

    private String companyName;
    private String time;
    private String date;
    private String notes;
    private String location;
    private String position;

    private String positionType;
    private String recruiterEmail;
    private String recruiterName;
    private String recruiterPhoneNumber;
    private String feedback;
    private String passed;
    private String interviewStage;
    private String interviewType;


    // End info to store

    private Map<String, String> interviewInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        timePicker = findViewById(R.id.input_time_picker);
        addCompanyButton = findViewById(R.id.btn_add_company);
        positionEditText = findViewById(R.id.input_position);
        mProgressDialog = new ProgressDialog(this);

        myCalendar = Calendar.getInstance();
        dateEditText = (EditText) findViewById(R.id.input_date);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddCompanyActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        addCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> interviewMap = loadFields();
                mProgressDialog.setMessage("Adding company");
                mProgressDialog.show();
                writeToFirestore(interviewMap);

                // send to firebase
            }
        });

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }


    private Map<String, String> loadFields() {
        // Info to store

        time = "" + timePicker.getHour() + ":" + timePicker.getMinute();
        companyName = ((EditText)findViewById(R.id.input_company)).getText().toString();
        date = dateEditText.getText().toString();
        notes = ((EditText)findViewById(R.id.input_notes)).getText().toString();
        location = ((EditText)findViewById(R.id.input_location)).getText().toString();
        position = positionEditText.getText().toString();
        location = ((EditText)findViewById(R.id.input_location)).getText().toString();
        positionType = ((EditText)findViewById(R.id.input_position_type)).getText().toString();
        recruiterEmail = ((EditText)findViewById(R.id.input_recruiter_email)).getText().toString();
        recruiterName = ((EditText)findViewById(R.id.input_recruiter_name)).getText().toString();
        recruiterPhoneNumber = ((EditText)findViewById(R.id.input_recruiter_phone)).getText().toString();
        feedback = ((EditText)findViewById(R.id.input_feedback)).getText().toString();
        interviewStage = ((EditText)findViewById(R.id.input_stage)).getText().toString();
        interviewType = ((EditText)findViewById(R.id.input_interview_type)).getText().toString();
        passed = ((EditText)findViewById(R.id.input_passed)).getText().toString();


        // end info to store

        interviewInfo = new HashMap<>();
        interviewInfo.put("companyName", companyName);
        interviewInfo.put("date", date);
        interviewInfo.put("notes", notes);
        interviewInfo.put("location", location);
        interviewInfo.put("time", time);
        interviewInfo.put("position", position);
        interviewInfo.put("positionType", positionType);
        interviewInfo.put("recruiterEmail", recruiterEmail);
        interviewInfo.put("recruiterName", recruiterName);
        interviewInfo.put("recruiterPhoneNumber", recruiterPhoneNumber);
        interviewInfo.put("feedback", feedback);
        interviewInfo.put("passed", passed);
        interviewInfo.put("interviewStage", interviewStage);
        interviewInfo.put("interviewType", interviewType);






        return interviewInfo;
    }


    private void writeToFirestore(Map<String, String> interviewMap) {
        FirebaseUser user = mAuth.getCurrentUser();
        final String TAG = "AddCompanyActivity";
        String uid = user.getUid();
        db.collection("users").document(uid).collection("Interviews").document().set(interviewMap).addOnSuccessListener(new OnSuccessListener<Void>() {
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

}
