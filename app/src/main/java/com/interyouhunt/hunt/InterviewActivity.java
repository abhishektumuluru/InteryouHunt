package com.interyouhunt.hunt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InterviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        TextView company = findViewById(R.id.company);
        String userCompany = "Google";
        company.setText(userCompany);

        TextView roundType = findViewById(R.id.roundType);
        String userRound = "Behavioral";
        roundType.setText(userRound);

        TextView recruiterEmail = findViewById(R.id.recruiterEmail);
        String userRecruitEmail  = "faisalgedi@gmail.com";
        recruiterEmail.setText(userRecruitEmail);

        TextView notes = findViewById(R.id.notesText);
        String userNotes  = "Aye let me tell you something she asked me why I wanna work at Google? I acted like it wasn't for the bread.";
        notes.setText(userNotes);

    }
}
