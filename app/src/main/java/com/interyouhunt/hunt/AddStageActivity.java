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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.travijuu.numberpicker.library.NumberPicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddStageActivity extends AppCompatActivity {

    HashMap<String, Object> map;
    private boolean isEditing;

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
    private NumberPicker stageNumberPicker;
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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        addStageButton = findViewById(R.id.btn_add_stage);

        mProgressDialog = new ProgressDialog(this);

        stage = findViewById(R.id.input_stage);
        stageNumberPicker = findViewById(R.id.stage_number_picker);

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
                if (isEditing) {
                    mProgressDialog.setMessage("Editing Stage");
                } else {
                    mProgressDialog.setMessage("Adding Stage");
                }
                mProgressDialog.show();
                writeToFirestore(stageInfo);
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null) {
            map = (HashMap<String, Object>) bundle.getSerializable("interviewMap");
            if (bundle.getBoolean("isEditing") == true) {
                isEditing = true;
                addStageButton.setText("Edit Stage");
                preFillFields();
            } else {
                isEditing = false;
            }
        }
    }

    private void preFillFields() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) return;
        int stageNum = bundle.getInt("stageNum");
        final ArrayList<Map<String, Object>> stages = (ArrayList<Map<String, Object>>) map.get("stages");
        Map<String, Object> stageMap = stages.get(stageNum);
        List<String> typesData = (List<String>) stageMap.get("type");
        Timestamp tsData =  (Timestamp)stageMap.get("datetime");
        String locationData = (String) stageMap.get("location");
        String notesData = (String) stageMap.get("notes");
        String stageData = (String) stageMap.get("stage");
        Long stageNumData = (Long) stageMap.get("stageNum");
        for (String type: typesData) {
            switch (type) {
                case "Behavioral":
                    behavioralCheckBox.setChecked(true);
                    isBehavioral = true;
                    break;
                case "Technical":
                    techicalCheckBox.setChecked(true);
                    isTechnical = true;
                    break;
                case "Case Study":
                    caseStudyCheckBox.setChecked(true);
                    isCaseStudy = true;
                    break;
            }
        }
        Date date = tsData.toDate();
        myCalendar.setTime(date);
        timePicker.setCurrentHour(myCalendar.get(Calendar.HOUR_OF_DAY));
        updateLabel();
        location.setText(locationData, TextView.BufferType.EDITABLE);
        notes.setText(notesData, TextView.BufferType.EDITABLE);
        stage.setText(stageData, TextView.BufferType.EDITABLE);
        stageNumberPicker.setValue(stageNumData.intValue());
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
        Long stageNum = Long.valueOf(stageNumberPicker.getValue());
        // end info to store
        Map<String, Object> stageInfo = new HashMap<>();
        stageInfo.put("stage", stage.getText().toString());
        stageInfo.put("type", types);
        stageInfo.put("location", location.getText().toString());
        stageInfo.put("datetime", datetime);
        stageInfo.put("notes", notes.getText().toString());
        stageInfo.put("stageNum", stageNum);
        return stageInfo;
    }


    private void writeToFirestore(final Map<String, Object> stageInfo) {
        FirebaseUser user = mAuth.getCurrentUser();
        final String TAG = "AddStageActivity";
        String uid = user.getUid();
        String docID = (String) map.get("docID");
        final List<Map<String, Object>> stages = (List<Map<String, Object>>) map.get("stages");
        final String successMessage;
        final String failureMessage;
        if (isEditing) {
            Bundle bundle = this.getIntent().getExtras();
            if (bundle == null) return;
            int stageNum = bundle.getInt("stageNum");
            stages.set(stageNum, stageInfo);
            successMessage = "Edited stage";
            failureMessage = "Error editing stage";
        } else {
            stages.add(stageInfo);
            successMessage = "Added stage";
            failureMessage = "Error adding stage";
        }

        Collections.sort(stages, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> stage1, Map<String, Object> stage2) {
                Long stageNum1 = (Long) stage1.get("stageNum");
                Long stageNum2 = (Long) stage2.get("stageNum");
                Long res = stageNum1 - stageNum2;
                return res.intValue();
            }
        });

        db.collection("users").document(uid).collection("Interviews").document(docID).update(
                "stages", stages
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mProgressDialog.dismiss();
                Log.d(TAG, "DocumentSnapshot successfully written!");
                Toast.makeText(AddStageActivity.this, successMessage, Toast.LENGTH_LONG).show();
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
                        if (!isEditing) stages.remove(stageInfo);
                        mProgressDialog.dismiss();
                        Log.w(TAG, failureMessage, e);
                    }
                });
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        System.out.println("BEFORE");
        System.out.println("isBehavioral: " + isBehavioral);
        System.out.println("isTechnical: " + isTechnical);
        System.out.println("isCaseStudy: " + isCaseStudy);

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_behavioral:
                if (checked) {
                    isBehavioral = true;
                } else {
                    isBehavioral = false;
                }
                break;
            case R.id.checkbox_technical:
                if (checked) {
                    isTechnical = true;
                } else {
                    isTechnical = false;
                }
                break;
            case R.id.checkbox_case_study:
                if (checked) {
                    isCaseStudy = true;
                } else {
                    isCaseStudy = false;
                }
                break;
        }
        System.out.println("AFTER");
        System.out.println("isBehavioral: " + isBehavioral);
        System.out.println("isTechnical: " + isTechnical);
        System.out.println("isCaseStudy: " + isCaseStudy);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }
}
