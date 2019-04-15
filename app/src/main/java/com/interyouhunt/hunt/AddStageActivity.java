package com.interyouhunt.hunt;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AddStageActivity extends AppCompatActivity {

    HashMap<String, Object> map;
    private boolean isEditing;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button addStageButton;
    private EditText dateEditText;
    private Calendar myCalendar;
    private TimePicker timePicker;
    CheckableSpinnerAdapter spinnerAdapter;

    private ProgressDialog mProgressDialog;

    // TODO: make stage, types, and datetime required fields for Sprint 5

    // Info to store
    private Timestamp datetime;
    private EditText notes;
    private EditText location;
    private EditText stage;
    private NumberPicker stageNumberPicker;
    private final List<CheckableSpinnerAdapter.SpinnerItem<String>> spinnerItems = new ArrayList<>();
    private final Set<String> selectedItems = new HashSet<>();

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

        // fill the 'spinner_items' array with all items to show
        List<String> stageTypes = Arrays.asList(
                "Behavioral",
                "Technical",
                "Case Study",
                "System Design",
                "Design",
                "Panel",
                "Problem Solving",
                "HR"
        );
        for(String stageType : stageTypes) {
            CheckableSpinnerAdapter.SpinnerItem<String> spinnerItem = new CheckableSpinnerAdapter.SpinnerItem<>(stageType, stageType);
            spinnerItems.add(spinnerItem);
        }

        String headerText = "Stage Types";
        Spinner spinner = findViewById(R.id.my_spinner);
        spinnerAdapter = new CheckableSpinnerAdapter<>(this, headerText, spinnerItems, selectedItems);
        spinner.setAdapter(spinnerAdapter);

        timePicker = findViewById(R.id.input_time_picker);
        dateEditText = findViewById(R.id.input_date);
        myCalendar = Calendar.getInstance();

        stage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        notes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

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
                //createNotification();
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
        selectedItems.addAll(typesData);
        spinnerAdapter.notifyDataSetChanged();
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
        types.addAll(selectedItems);
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

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//    private void createNotification() {
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        String token = db.collection("users").document(uid).get("FMCToken");
//    }
}

class CheckableSpinnerAdapter<T> extends BaseAdapter {

    static class SpinnerItem<T> {
        private String text;
        private T item;

        SpinnerItem(T t, String s) {
            item = t;
            text = s;
        }
    }

    private Context context;
    private Set<T> selected_items;
    private List<SpinnerItem<T>> all_items;
    private String headerText;

    CheckableSpinnerAdapter(Context context,
                            String headerText,
                            List<SpinnerItem<T>> all_items,
                            Set<T> selected_items) {
        this.context = context;
        this.headerText = headerText;
        this.all_items = all_items;
        this.selected_items = selected_items;
    }

    @Override
    public int getCount() {
        return all_items.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if( position < 1 ) {
            return null;
        }
        else {
            return all_items.get(position-1);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null ) {
            LayoutInflater layoutInflator = LayoutInflater.from(context);
            convertView = layoutInflator.inflate(R.layout.checkable_spinner_item, parent, false);

            holder = new ViewHolder();
            holder.mTextView = convertView.findViewById(R.id.text);
            holder.mCheckBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if( position < 1 ) {
            holder.mCheckBox.setVisibility(View.GONE);
            holder.mTextView.setText(headerText);
        }
        else {
            final int listPos = position - 1;
            holder.mCheckBox.setVisibility(View.VISIBLE);
            holder.mTextView.setText(all_items.get(listPos).text);

            final T item = all_items.get(listPos).item;
            boolean isSel = selected_items.contains(item);

            holder.mCheckBox.setOnCheckedChangeListener(null);
            holder.mCheckBox.setChecked(isSel);

            holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if( isChecked ) {
                        selected_items.add(item);
                    }
                    else {
                        selected_items.remove(item);
                    }
                }
            });

            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mCheckBox.toggle();
                }
            });
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}
