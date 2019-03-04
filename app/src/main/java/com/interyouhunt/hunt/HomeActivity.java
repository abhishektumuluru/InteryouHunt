package com.interyouhunt.hunt;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    ListView listView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    private static final String TAG = "HomeActivity";
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        getInterviews(uid, new GetInterviewsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data) {
                List<String> interviews = new ArrayList<>();
                for (Map<String, Object> interview: data) {
                    String str = interview.get("companyName") + ", " + interview.get("position");
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

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this,
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

    private void getInterviews(String uid, final GetInterviewsCallback callback) {
        final List<Map<String, Object>> interviews = new ArrayList<>();
        DocumentReference userDoc = db.collection("users").document("PxchPbrpnQvjdkvyaxIF");
        userDoc.collection("Interviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Map<String, Object> data = document.getData();
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
