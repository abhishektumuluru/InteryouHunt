package com.interyouhunt.hunt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView name = findViewById(R.id.name);
        String userName = "Faisal";
        userName += " "+"Gedi";
        name.setText(userName);

        TextView email = findViewById(R.id.userEmail);
        String userEmail = "faisalgedi@gmail.com";
        email.setText(userEmail);

        TextView phone = findViewById(R.id.userPhone);
        String userPhone  = "678-974-9193";
        phone.setText(userPhone);
    }
}
