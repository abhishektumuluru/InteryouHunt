package com.interyouhunt.hunt;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements Serializable {

    ListView listView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    private static final String TAG = "HomeActivity";
    String uid;
    FloatingActionButton plusButton;
    Button forumButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();

        forumButton = findViewById(R.id.toForum);
        forumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ForumActivity.class);
                startActivity(i);
            }
        });


        plusButton = findViewById(R.id.btn_plus);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.this.startActivity(new Intent(HomeActivity.this, AddPositionActivity.class));
            }
        });
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        getInterviews(uid, new GetInterviewsCallback() {
            @Override
            public void onCallback(final List<Map<String, Object>> data) {
                List<String> interviews = new ArrayList<>();
                for (Map<String, Object> interview: data) {
                    String str = interview.get("companyName") + ", " + interview.get("position");
                    interviews.add(str);
                }
                String[] values = interviews.toArray(new String[interviews.size()]);

                // Defined Array values to show in ListView

                // Get ListView object from xml
                listView = (ListView) findViewById(R.id.list);

                // Define a new Adapter
                // First parameter - Context
                // Second parameter - Layout for the row
                // Third parameter - ID of the TextView to which the data is written
                // Forth - the Array of data

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, values);


                // Assign adapter to ListView
                listView.setAdapter(adapter);


                // ListView Item Click Listener
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        HashMap<String,Object> map = (HashMap<String, Object>) data.get(position);
//                        for (String name: map.keySet()){
//                            String key =name.toString();
//                            String value = map.get(name).toString();
//                            System.out.println(key + " " + value);
//                            Log.d(TAG, "MAP: " + key + "  " + value);
//                        }
                        Intent intent = new Intent(HomeActivity.this, intActivity.class);
                        Bundle extras = new Bundle();
                        extras.putSerializable("interviewMap", map);
                        intent.putExtras(extras);
                        startActivity(intent);

                    }

                });
            }
        });

        Button home = findViewById(R.id.toHome);
        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            }
        });

        Button toDo = findViewById(R.id.toDo);
        toDo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ToDoActivity.class));
            }
        });

        Button profile = findViewById(R.id.toProfile);
        profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });
    }

    private void getInterviews(String uid, final GetInterviewsCallback callback) {
        final List<Map<String, Object>> interviews = new ArrayList<>();
        DocumentReference userDoc = db.collection("users").document(uid);
        userDoc.collection("Interviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Map<String, Object> data = document.getData();
                        data.put("docID", document.getId());
                        interviews.add(data);
                    }
                    callback.onCallback(interviews);
                } else {
                    Log.w(TAG, "Error getting documents. ", task.getException());
                }
            }
        });
    }

    public interface GetInterviewsCallback {
        void onCallback(List<Map<String, Object>> value);
    }
}
