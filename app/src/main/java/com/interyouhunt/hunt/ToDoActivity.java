package com.interyouhunt.hunt;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ToDoActivity extends AppCompatActivity {

    private static final String TAG = "ToDoActivity";
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

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        getSortedInterviews(uid, new HomeActivity.GetInterviewsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data) {
                List<String> interviews = new ArrayList<>();
                for (Map<String, Object> interview: data) {
                    String str = interview.get("companyName") + ", " + interview.get("interviewType") + "; Date = " + interview.get("date") + ", Time = " + interview.get("time");
                    interviews.add(str);
                }
                String[] values = interviews.toArray(new String[interviews.size()]);

                // Defined Array values to show in ListView

                // Get ListView object from xml
                listView = (ListView) findViewById(R.id.list);
                // Get ListView object from xml
                listView = (ListView) findViewById(R.id.list);

                // Define a new Adapter
                // First parameter - Context
                // Second parameter - Layout for the row
                // Third parameter - ID of the TextView to which the data is written
                // Forth - the Array of data

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ToDoActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, values);


                // Assign adapter to ListView
                listView.setAdapter(adapter);

                // ListView Item Click Listener
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        // ListView Clicked item index
                        int itemPosition     = position;

                        // ListView Clicked item value
                        String  itemValue    = (String) listView.getItemAtPosition(position);

                        // Show Alert
                        Toast.makeText(getApplicationContext(),
                                "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                                .show();

                    }

                });
            }
        });

    }
    
    private void getSortedInterviews(String uid, final HomeActivity.GetInterviewsCallback callback) {
        final List<Map<String, Object>> interviews = new ArrayList<>();
        DocumentReference userDoc = db.collection("users").document(uid);
        userDoc.collection("Interviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Map<String, Object> data = document.getData();
                        interviews.add(data);
                    }
                    Collections.sort(interviews, new Comparator<Map<String, Object>>() {
                        @Override
                        public int compare(Map<String, Object> t1, Map<String, Object> t2) {
                            try {
                                String time1 = t1.get("time").toString();
                                if (time1.split(":")[0].length() < 2) {
                                    time1 = "0" + time1;
                                }
                                String time2 = t1.get("time").toString();
                                if (time2.split(":")[0].length() < 2) {
                                    time2 = "0" + time1;
                                }
                                String str1 = t1.get("date").toString() + " " + time1;
                                String str2 = t2.get("date").toString() + " " + time2;
                                Date dt1 = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.ENGLISH).parse(str1);
                                Date dt2 = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.ENGLISH).parse(str2);
                                return dt1.compareTo(dt2);
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });
                    callback.onCallback(interviews);
                } else {
                    Log.w(TAG, "Error getting documents. ", task.getException());
                }
            }
        });
    }

}
