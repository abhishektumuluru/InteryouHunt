package com.interyouhunt.hunt;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterviewActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    private static final String TAG = "InterviewActivity";
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        Intent intent = getIntent();
        final HashMap<String,Object> map = (HashMap<String, Object>)intent.getSerializableExtra("interviewMap") ;

        getInterviews(uid, new HomeActivity.GetInterviewsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data) {

                TextView company = findViewById(R.id.company);
                String userCompany = map.get("companyName").toString();
                company.setText(userCompany);

                TextView positionType = findViewById(R.id.positionType);
                String userPositionType = map.get("position").toString();
                positionType.setText(userPositionType);

                TextView date = findViewById(R.id.dateTime);
                String userDate = map.get("date").toString();
                date.setText(userDate);

                TextView location = findViewById(R.id.userLocation);
                String userLocation = data.get(0).get("location").toString();
                location.setText(userLocation);

                TextView roundType = findViewById(R.id.roundType);
                String userRound = data.get(0).get("interviewType").toString();
                roundType.setText(userRound);

                TextView interviewType = findViewById(R.id.interviewType);
                String userInterview = data.get(0).get("interviewStage").toString();
                interviewType.setText(userInterview);

                TextView recruiterName = findViewById(R.id.recruiter);
                String userRecruitName  = data.get(0).get("recruiterName").toString();
                recruiterName.setText(userRecruitName);

                TextView recruiterEmail = findViewById(R.id.recruiterEmail);
                String userRecruitEmail  = data.get(0).get("recruiterEmail").toString();
                recruiterEmail.setText(userRecruitEmail);

                TextView notes = findViewById(R.id.notesText);
                String userNotes  = data.get(0).get("notes").toString();
                notes.setText(userNotes);
            }
        });


        Button home = findViewById(R.id.toHome);
        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(InterviewActivity.this, HomeActivity.class));
            }
        });

        Button toDo = findViewById(R.id.toDo);
        toDo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(InterviewActivity.this, HomeActivity.class));
            }
        });

        Button profile = findViewById(R.id.toProfile);
        profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(InterviewActivity.this, ProfileActivity.class));
            }
        });

    }

    private void getInterviews(String uid, final HomeActivity.GetInterviewsCallback callback) {
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
                    callback.onCallback(interviews);
                } else {
                    Log.w(TAG, "Error getting documents. ", task.getException());
                }
            }
        });
    }
}
