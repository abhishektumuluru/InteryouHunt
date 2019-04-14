package com.interyouhunt.hunt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumActivity extends AppCompatActivity {

    private final String TAG = "ForumActivity";
    FirebaseFirestore db;
    private ListView forumListView;
    private String[] titles;
    private String[] descriptions;
    private String[] companies;
    private String[] positions;
    private String[] interviewTypes;
    final Map<String, Integer> companyToLogoMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        db = FirebaseFirestore.getInstance();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.homenav:
                                startActivity(new Intent(ForumActivity.this, HomeActivity.class));
                                break;
                            case R.id.todonav:
                                startActivity(new Intent(ForumActivity.this, ToDoActivity.class));
                                break;
                            case R.id.forumnav:
                                startActivity(new Intent(ForumActivity.this, ForumActivity.class));
                        }
                        return true;
                    }
                });

        forumListView = findViewById(R.id.listView);

        final List<String> titlesList = new ArrayList<>();
        final List<String> descriptionsList = new ArrayList<>();
        final List<String> companiesList = new ArrayList<>();
        final List<String> positionsList = new ArrayList<>();
        final List<String> interviewTypesList = new ArrayList<>();


        companyToLogoMap.put("Google", R.drawable.common_google_signin_btn_icon_dark);
        companyToLogoMap.put("Facebook", R.drawable.fblogo);
        companyToLogoMap.put("Snapchat", R.drawable.snaplogo);
        companyToLogoMap.put("GTRI", R.drawable.gtlogo);
        companyToLogoMap.put("Pinterest", R.drawable.pinterestlogo);



        // Titles and descriptions are arrays you get from firebase

        getTips(new GetTipsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data) {
                Log.d(TAG, "data size" + data.size());
                for (Map<String, Object> interview: data) {
                    String company = (String) interview.get("Company");
                    String description = (String) interview.get("description");
                    String postTitle = (String) interview.get("postTitle");
                    String position = (String) interview.get("position");
                    String interviewType = (String) interview.get("type");
                    companiesList.add(company);
                    descriptionsList.add(description);
                    titlesList.add(postTitle);
                    positionsList.add(position);
                    interviewTypesList.add(interviewType);

                }
                titles = titlesList.toArray(new String[titlesList.size()]);
                descriptions = descriptionsList.toArray(new String[descriptionsList.size()]);
                companies = companiesList.toArray(new String[companiesList.size()]);
                positions = positionsList.toArray(new String[positionsList.size()]);
                interviewTypes = interviewTypesList.toArray(new String[interviewTypesList.size()]);


                final CustomListAdapter adapter = new CustomListAdapter(ForumActivity.this, titles, descriptions, companies, positions, interviewTypes);
                forumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Start a new activity or open up a new fragment with the post in it
                        Toast.makeText(ForumActivity.this, "Opening", Toast.LENGTH_SHORT).show();
                        openPost(titles[position], descriptions[position], companies[position], positions[position], interviewTypes[position]);
                    }
                });

                Button searchButton = findViewById(R.id.company_search_button);

                final List<String> companyQueryResults = new ArrayList<>();
                final List<String> descriptionQueryResults = new ArrayList<>();
                final List<String> postTitleQueryResults = new ArrayList<>();
                final List<String> positionsQueryResults = new ArrayList<>();
                final List<String> typesQueryResults = new ArrayList<>();


                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText searchEditText = findViewById(R.id.company_search_edittext);
                        final String searchQuery = searchEditText.getText().toString().toLowerCase();
                        for (int i = 0; i < companies.length; i++) {
                            String company = companies[i];
                            if ((company.toLowerCase()).contains(searchQuery)) {
                                companyQueryResults.add(company);
                                descriptionQueryResults.add(descriptions[i]);
                                postTitleQueryResults.add(titles[i]);
                                positionsQueryResults.add(positions[i]);
                                typesQueryResults.add(interviewTypes[i]);

                            }
                        }

                        String[] searchTitles = postTitleQueryResults.toArray(new String[postTitleQueryResults.size()]);
                        String[] searchDescriptions = descriptionQueryResults.toArray(new String[descriptionQueryResults.size()]);
                        String[] searchCompanies = companyQueryResults.toArray(new String[companyQueryResults.size()]);
                        String[] searchPositions = positionsQueryResults.toArray(new String[positionsQueryResults.size()]);
                        String[] searchTypes = typesQueryResults.toArray(new String[typesQueryResults.size()]);

                        postTitleQueryResults.clear();
                        descriptionQueryResults.clear();
                        companyQueryResults.clear();
                        typesQueryResults.clear();
                        postTitleQueryResults.clear();

                        forumListView.setAdapter(null);
                        final CustomListAdapter newAdapter = new CustomListAdapter(ForumActivity.this, searchTitles, searchDescriptions, searchCompanies, searchPositions, searchTypes);
                        forumListView.setAdapter(newAdapter);
                        adapter.notifyDataSetChanged();
                    }
                });


                // hack to start search
                searchButton.performClick();

                final Button addPostButton = findViewById(R.id.add_post_button);
                addPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCreatePost();
                    }
                });
            }
        });





    }

    class CustomListAdapter extends ArrayAdapter<String> {
        Context context;
        String title[];
        String description[];
        String company[];
        String[] interviewTypes;
        String[] positions;


        CustomListAdapter(Context context, String title[], String description[], String company[], String[] positions, String[] interviewTypes) {
            super(context, R.layout.forumrow, R.id.main_title_textview, title);
            this.context = context;
            this.title = title;
            this.description = description;
            this.positions = positions;
            this.interviewTypes = interviewTypes;
            this.company = company;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.forumrow, parent, false);
            ImageView image = row.findViewById(R.id.thumbnail);
            TextView title = row.findViewById(R.id.main_title_textview);
            TextView description = row.findViewById(R.id.main_description_textview);
            TextView company = row.findViewById(R.id.company_textview);

            TextView positionTextView = row.findViewById(R.id.company_position);
            TextView type = row.findViewById(R.id.company_interview_type);

            title.setText(this.title[position]);
            description.setText(this.description[position]);
            company.setText(this.company[position]);
            positionTextView.setText(this.positions[position]);
            type.setText(this.interviewTypes[position]);

            if (companyToLogoMap.containsKey(this.company[position])) {
                image.setImageDrawable(getResources().getDrawable(companyToLogoMap.get(this.company[position])));
            } else {
                // If the company's logo is not hardcoded in our map, then use this
                image.setImageDrawable(getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark));
            }
            return row;
        }
    }



    private void getTips(final GetTipsCallback callback) {
        final List<Map<String, Object>> tips = new ArrayList<>();
        db.collection("tips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                                tips.add(data);
                            }
                            callback.onCallback(tips);
                        } else {
                            Log.w(TAG, "Error getting documents. ", task.getException());
                        }
                    }
                });
    }

    protected void openPost(String title, String description, String company, String position, String interviewType) {
        ViewDialog alert = new ViewDialog();
        alert.showOpenedPostDialog(this, title, description, company, position, interviewType);
    }

    protected void openCreatePost() {
        ViewDialog alert = new ViewDialog();
        alert.createPostDialog(this);
    }

    public interface GetTipsCallback {
        void onCallback(List<Map<String, Object>> value);
    }

    public class ViewDialog {

        public void showOpenedPostDialog(Activity activity, String title, String description, String company, String position, String interviewType){
            final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Light_Dialog_Alert);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog);

            TextView titleTextView = dialog.findViewById(R.id.dialog_title_textview);
            TextView descriptionTextView = dialog.findViewById(R.id.dialog_description_textview);
            TextView companyTextView = dialog.findViewById(R.id.dialog_company_textview);

            titleTextView.setText(title);
            descriptionTextView.setText(description);
            companyTextView.setText(company);

            Button dialogButton = dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }

        public void createPostDialog(Activity activity){
            final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Light_Dialog_Alert);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.openpostdialog);

            final EditText titleTextView = dialog.findViewById(R.id.open_dialog_title_textview);
            final EditText descriptionTextView = dialog.findViewById(R.id.open_dialog_description_textview);
            final EditText companyTextView = dialog.findViewById(R.id.open_dialog_company_textview);
            final EditText positionTextView = dialog.findViewById(R.id.open_dialog_position_textview);
            final Spinner interviewTypeSpinner = dialog.findViewById(R.id.interview_type_spinner);


            Button postButton = dialog.findViewById(R.id.post_btn_dialog);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String titleText = titleTextView.getText().toString();
                    final String descriptionText = descriptionTextView.getText().toString();
                    final String companyText = companyTextView.getText().toString();
                    final String position = positionTextView.getText().toString();
                    final String interviewType = interviewTypeSpinner.getSelectedItem().toString();

                    if (TextUtils.isEmpty(interviewType) || TextUtils.isEmpty(titleText) || TextUtils.isEmpty(descriptionText) || TextUtils.isEmpty(companyText) || TextUtils.isEmpty(position)) {
                        Toast.makeText(ForumActivity.this, "Please complete all fields", Toast.LENGTH_LONG).show();
                        return;
                    }
                    submitPost(titleText, descriptionText, companyText, position, interviewType);
                    Toast.makeText(ForumActivity.this, "Posting", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }

    protected void submitPost(String titleText, String descriptionText, String companyText, String position, String type) {
        Map<String, Object> postInfo = new HashMap<>();
        postInfo.put("Company", companyText);
        postInfo.put("description", descriptionText);
        postInfo.put("postTitle", titleText);
        postInfo.put("position", position);
        postInfo.put("type", type);


        db.collection("tips").document().set(postInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                Toast.makeText(ForumActivity.this, "Added company", Toast.LENGTH_LONG).show();
                ForumActivity.this.startActivity(new Intent(ForumActivity.this, HomeActivity.class));
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

}
