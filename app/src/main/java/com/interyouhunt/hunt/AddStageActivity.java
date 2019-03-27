package com.interyouhunt.hunt;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddStageActivity extends AppCompatActivity {

    HashMap<String, Object> map;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button addStageButton;
    private EditText dateEditText;
    private Calendar myCalendar;
    private TimePicker timePicker;

    private ProgressDialog mProgressDialog;

    // TODO: make stage, types, and datetime required fields for Sprint 5

    // Info to store
    private Timestamp datetime;
    private EditText notes;
    private EditText location;
    private EditText stage;
    private CheckBox behavioralCheckBox;
    private CheckBox techicalCheckBox;
    private CheckBox caseStudyCheckBox;
    private boolean isBehavioral;
    private boolean isTechnical;
    private boolean isCaseStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null) {
            map  = (HashMap<String, Object>) bundle.getSerializable("interviewMap");
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        addStageButton = findViewById(R.id.btn_add_stage);
//        addStageButton.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);

        stage = findViewById(R.id.input_stage);
        notes = findViewById(R.id.input_notes);
        location = findViewById(R.id.input_location);
        behavioralCheckBox = findViewById(R.id.checkbox_behavioral);
        techicalCheckBox = findViewById(R.id.checkbox_technical);
        caseStudyCheckBox = findViewById(R.id.checkbox_case_study);
        isBehavioral = false;
        isTechnical = false;
        isCaseStudy = false;

        timePicker = findViewById(R.id.input_time_picker);
        dateEditText = findViewById(R.id.input_date);
        myCalendar = Calendar.getInstance();

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                myCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                myCalendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            }
        });

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
                new DatePickerDialog(AddStageActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        addStageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> stageInfo = loadFields();
                mProgressDialog.setMessage("Adding Stage");
                mProgressDialog.show();
                writeToFirestore(stageInfo);
            }
        });

    }

    private Map<String, Object> loadFields() {
        List<String> types = new ArrayList<>();
        if (isBehavioral) {
            types.add("Behavioral");
        }
        if (isTechnical) {
            types.add("Technical");
        }
        if (isCaseStudy) {
            types.add("Case Study");
        }
        datetime = new Timestamp(new Date(myCalendar.getTimeInMillis()));

        // end info to store
        Map<String, Object> stageInfo = new HashMap<>();
        stageInfo.put("stage", stage.getText().toString());
        stageInfo.put("type", types);
        stageInfo.put("location", location.getText().toString());
        stageInfo.put("datetime", datetime);
        stageInfo.put("notes", notes.getText().toString());
        return stageInfo;
    }


    private void writeToFirestore(Map<String, Object> stageInfo) {
        FirebaseUser user = mAuth.getCurrentUser();
        final String TAG = "AddStageActivity";
        String uid = user.getUid();
        String docID = (String) map.get("docID");
        List<Map<String, Object>> stages = (List<Map<String, Object>>) map.get("stages");
        stages.add(stageInfo);

        db.collection("users").document(uid).collection("Interviews").document(docID).update(
                "stages", stages
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                Toast.makeText(AddStageActivity.this, "Added new stage", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddStageActivity.this, intActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable("interviewMap", map);
                intent.putExtras(extras);
                startActivity(intent);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_behavioral:
                if (checked) {
                    isBehavioral = true;
                } else {
                    isBehavioral = false;
                }
            case R.id.checkbox_technical:
                if (checked) {
                    isTechnical = true;
                } else {
                    isTechnical = false;
                }
            case R.id.checkbox_case_study:
                if (checked) {
                    isCaseStudy = true;
                } else {
                    isCaseStudy = false;
                }
        }
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }
}
