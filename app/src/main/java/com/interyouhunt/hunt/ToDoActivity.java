package com.interyouhunt.hunt;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ToDoActivity extends AppCompatActivity {

    private static final String TAG = "ToDoActivity";
    //LinearLayout linearLayout;
    ListView listView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = this.getIntent().getExtras();

        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);


        if(bundle != null){
            int nav_id = bundle.getInt("nav_id");
            bottomNavigationView.setSelectedItemId(nav_id);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.homenav:
                                int value1= R.id.homenav;
                                Intent i1 = new Intent(ToDoActivity.this, HomeActivity.class);
                                Bundle b1 = new Bundle();
                                b1.putInt("nav_id", value1);
                                i1.putExtras(b1);
                                startActivity(i1);
                                break;
                            case R.id.todonav:
                                int value2= R.id.todonav;
                                Intent i2 = new Intent(ToDoActivity.this, ToDoActivity.class);
                                Bundle b2 = new Bundle();
                                b2.putInt("nav_id", value2);
                                i2.putExtras(b2);
                                startActivity(i2);
                                break;
                            case R.id.forumnav:
                                int value3= R.id.forumnav;
                                Intent i3 = new Intent(ToDoActivity.this, ForumActivity.class);
                                Bundle b3 = new Bundle();
                                b3.putInt("nav_id", value3);
                                i3.putExtras(b3);
                                startActivity(i3);
                                break;
                        }
                        return true;
                    }
                });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }
        getSortedInterviews(uid, new HomeActivity.GetInterviewsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data) {
                ArrayList<ToDo> interviews = new ArrayList<>();
                for (Map<String, Object> interview: data) {
                    StringBuilder sbType = new StringBuilder();
                    for (String type: (List<String>) interview.get("type")) {
                        sbType.append(type + "/");
                    }
                    if (sbType.length() > 0) {
                        sbType.setLength(sbType.length() - 1);
                    }
                    String datetime = "N/A";
                    Timestamp ts =  (Timestamp) interview.get("datetime");
                    if (ts != null) {
                        datetime = ts.toDate().toString();
                    }
                    Log.d(TAG, "onCallback: datetime " + datetime);
                    String companyName = (String) interview.get("companyName");
                    ToDo str = new ToDo(companyName, sbType.toString(), datetime, "");
                    interviews.add(str);
                }

                // Defined Array values to show in ListView

                listView = findViewById(R.id.list);

                // Define a new Adapter
                // First parameter - Context
                // Second parameter - Layout for the row
                // Third parameter - ID of the TextView to which the data is written
                // Forth - the Array of data

                ToDoAdapter adapter = new ToDoAdapter(ToDoActivity.this, interviews);

                // Assign adapter to ListView
                listView.setAdapter(adapter);
            }
        });

    }

    private void getSortedInterviews(String uid, final HomeActivity.GetInterviewsCallback callback) {
        final List<Map<String, Object>> interviews = new ArrayList<>();
        final CollectionReference interviewCollection = db.collection("users").document(uid).collection("Interviews");
        interviewCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot interviewDoc : task.getResult()) {
                        Log.d(TAG, "Interview: " + interviewDoc.getId() + " => " + interviewDoc.getData() + "\n");
                        final Map<String, Object> interviewData = interviewDoc.getData();
                        final List<Map<String, Object>> stages = (List<Map<String, Object>>) interviewData.get("stages");
                        Log.d(TAG, "Stages: " + Arrays.toString(stages.toArray()) + "\n");
                        for (Map<String, Object> stage : stages) {
                            stage.put("companyName", interviewData.get("companyName"));
                        }
                        interviews.addAll(stages);
                    }
                    System.out.println(Arrays.toString(interviews.toArray()));
                    Collections.sort(interviews, new Comparator<Map<String, Object>>() {
                        @Override
                        public int compare(Map<String, Object> t1, Map<String, Object> t2) {
                            Timestamp ts1 =  (Timestamp) t1.get("datetime");
                            Timestamp ts2 =  (Timestamp) t2.get("datetime");
                            return ts1.compareTo(ts2);
                        }
                    });
                    callback.onCallback(interviews);
                } else {
                    Log.w(TAG, "Error getting interview documents. ", task.getException());
                }
            }
        });
    }

    class ToDo {
        private String companyName;
        private String interviewType;
        private String date;
        private String time;

        ToDo(String companyName, String interviewType, String date, String time) {
            this.companyName = companyName;
            this.interviewType = interviewType;
            this.date = date;
            this.time = time;
        }
    }

    class ToDoAdapter extends ArrayAdapter<ToDo> {

        ToDoAdapter(Context context, List<ToDo> todoList) {
            super(context, R.layout.todo_row, todoList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(R.layout.todo_row, parent, false);

            TextView companyNameText = customView.findViewById(R.id.company_name);
            TextView interviewTypesText = customView.findViewById(R.id.interview_type);
            TextView dateText = customView.findViewById(R.id.date);
            TextView timeText = customView.findViewById(R.id.time);

            companyNameText.setText(getItem(position).companyName);
            interviewTypesText.setText(getItem(position).interviewType);
            dateText.setText(getItem(position).date);
            timeText.setText(getItem(position).time);

            return customView;
        }

    }
}