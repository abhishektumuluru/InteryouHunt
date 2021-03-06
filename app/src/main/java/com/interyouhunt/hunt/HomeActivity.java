package com.interyouhunt.hunt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements Serializable {

    SwipeMenuListView swipeListView;
    ArrayAdapter<String> adapter;
    List<String> interviews;
    List<Map<String, Object>> interviewListData;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    private static final String TAG = "HomeActivity";
    String uid;
    FloatingActionButton plusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
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
                                Intent i1 = new Intent(HomeActivity.this, HomeActivity.class);
                                Bundle b1 = new Bundle();
                                b1.putInt("nav_id", value1);
                                i1.putExtras(b1);
                                startActivity(i1);
                                break;
                            case R.id.todonav:
                                int value2= R.id.todonav;
                                Intent i2 = new Intent(HomeActivity.this, ToDoActivity.class);
                                Bundle b2 = new Bundle();
                                b2.putInt("nav_id", value2);
                                i2.putExtras(b2);
                                startActivity(i2);
                                break;
                            case R.id.forumnav:
                                int value3= R.id.forumnav;
                                Intent i3 = new Intent(HomeActivity.this, ForumActivity.class);
                                Bundle b3 = new Bundle();
                                b3.putInt("nav_id", value3);
                                i3.putExtras(b3);
                                startActivity(i3);
                                break;
                        }
                        return true;
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
                interviewListData = data;
                // Get Array values to show in ListView
                final List<String> positions = new ArrayList<>();
                interviews = new ArrayList<>();
                for (Map<String, Object> interview: data) {
                    interviews.add((String) interview.get("companyName"));
                    positions.add((String) interview.get("position"));
                }

                // Get SwipeMenuListView object from xml
                swipeListView = findViewById(R.id.swipe_list);

                // Define a new Adapter
                // First parameter - Context
                // Second parameter - Layout for the row
                // Third parameter - ID of the TextView to which the data is written
                // Forth - the Array of data

                adapter = new ArrayAdapter<String>(HomeActivity.this,
                        android.R.layout.simple_list_item_2, android.R.id.text1, interviews) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = view.findViewById(android.R.id.text1);
                        TextView text2 = view.findViewById(android.R.id.text2);

                        text1.setText(interviews.get(position));
                        text1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                        text2.setText(positions.get(position));
                        text1.setTypeface(text1.getTypeface(), Typeface.BOLD_ITALIC);
                        text1.setPadding(0,0,0,10);

                        text2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        text2.setTypeface(text2.getTypeface(), Typeface.ITALIC);
                        return view;
                    }
                };

                // Assign adapter to SwipeMenuListView
                swipeListView.setAdapter(adapter);

                // SwipeMenuListView Item Click Listener
                swipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        HashMap<String,Object> map = (HashMap<String, Object>) data.get(position);
                        Intent intent = new Intent(HomeActivity.this, intActivity.class);
                        Bundle extras = new Bundle();
                        extras.putSerializable("interviewMap", map);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }

                });

                SwipeMenuCreator creator = new SwipeMenuCreator() {

                    @Override
                    public void create(SwipeMenu menu) {
                        // create "delete" item
                        SwipeMenuItem deleteItem = new SwipeMenuItem(
                                getApplicationContext());
                        // set item background
                        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                                0x3F, 0x25)));
                        // set item width
                        deleteItem.setWidth(170);
                        // set a icon
                        deleteItem.setIcon(R.drawable.ic_delete);
                        // add to menu
                        menu.addMenuItem(deleteItem);
                    }
                };

                // set creator
                swipeListView.setMenuCreator(creator);
                swipeListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                        switch (index) {
                            case 0:
                                // open
                                Log.d(TAG, "Clicked item: " + index);
                                removePosition(position);
                                break;
                        }
                        // false : close the menu; true : not close the menu
                        return false;
                    }
                });
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel = new NotificationChannel("Hunt", "Hunt",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notifChannel.setDescription("Hunt");
            NotificationManager notifMgr = getSystemService(NotificationManager.class);
            notifMgr.createNotificationChannel(notifChannel);
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d(TAG, "Token: " + token);
                        sendRegistrationToServer(token);
                    }
                });
    }

    private void sendRegistrationToServer(String token) {
        HashMap<String, String> fmcToken = new HashMap<>();
        fmcToken.put("FMCToken", token);
        DocumentReference doc = db.collection("users")
                .document(uid);
        doc.set(fmcToken);
    }

    private void removePosition(final int position) {
        Map<String, Object> data = interviewListData.get(position);
        final String docId = (String) data.get("docID");
        db.collection("users").document(uid)
                .collection("Interviews").document(docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document with id " + docId + " successfully deleted!");
                        interviewListData.remove(position);
                        interviews.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document with id " + docId, e);
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

    @Override
    public void onBackPressed() {
    }

}
