package com.interyouhunt.hunt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        db = FirebaseFirestore.getInstance();

        forumListView = findViewById(R.id.listView);

        final List<String> titlesList = new ArrayList<>();
        final List<String> descriptionsList = new ArrayList<>();
        final List<String> companiesList = new ArrayList<>();

        // Titles and descriptions are arrays you get from firebase

        getTips(new GetTipsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data) {
                Log.d(TAG, "data size" + data.size());
                for (Map<String, Object> interview: data) {
                    String company = (String) interview.get("Company");
                    String description = (String) interview.get("description");
                    String postTitle = (String) interview.get("postTitle");
                    companiesList.add(company);
                    descriptionsList.add(description);
                    titlesList.add(postTitle);

                }
                titles = titlesList.toArray(new String[titlesList.size()]);
                descriptions = descriptionsList.toArray(new String[descriptionsList.size()]);
                companies = companiesList.toArray(new String[companiesList.size()]);
                final int images[] = new int[]{0, 0};



                final CustomListAdapter adapter = new CustomListAdapter(ForumActivity.this, titles, descriptions, images, companies);
                forumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Start a new activity or open up a new fragment with the post in it
                        Toast.makeText(ForumActivity.this, "Opening", Toast.LENGTH_SHORT).show();
                        openPost(titles[position], descriptions[position], companies[position]);
                    }
                });

                Button searchButton = findViewById(R.id.company_search_button);

                final List<String> companyQueryResults = new ArrayList<>();
                final List<String> descriptionQueryResults = new ArrayList<>();
                final List<String> postTitleQueryResults = new ArrayList<>();

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
                            }
                        }

                        String[] searchTitles = postTitleQueryResults.toArray(new String[postTitleQueryResults.size()]);
                        String[] searchDescriptions = descriptionQueryResults.toArray(new String[descriptionQueryResults.size()]);
                        String[] searchCompanies = companyQueryResults.toArray(new String[companyQueryResults.size()]);
                        postTitleQueryResults.clear();
                        descriptionQueryResults.clear();
                        companyQueryResults.clear();

                        forumListView.setAdapter(null);
                        final CustomListAdapter newAdapter = new CustomListAdapter(ForumActivity.this, searchTitles, searchDescriptions, images, searchCompanies);
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

        int images[]; // optional

        CustomListAdapter(Context context, String title[], String description[], int images[], String company[]) {
            super(context, R.layout.forumrow, R.id.main_title_textview, title);
            this.context = context;
            this.title = title;
            this.description = description;
            this.images = images;
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
            title.setText(this.title[position]);
            description.setText(this.description[position]);
            company.setText(this.company[position]);
            // image.setImageDrawable(this.images[position]);
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

    protected void openPost(String title, String description, String company) {
        ViewDialog alert = new ViewDialog();
        alert.showOpenedPostDialog(this, title, description, company);
    }

    protected void openCreatePost() {
        ViewDialog alert = new ViewDialog();
        alert.createPostDialog(this);
    }

    public interface GetTipsCallback {
        void onCallback(List<Map<String, Object>> value);
    }

    public class ViewDialog {

        public void showOpenedPostDialog(Activity activity, String title, String description, String company){
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


            Button postButton = dialog.findViewById(R.id.post_btn_dialog);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String titleText = titleTextView.getText().toString();
                    final String descriptionText = descriptionTextView.getText().toString();
                    final String companyText = companyTextView.getText().toString();
                    submitPost(titleText, descriptionText, companyText);
                    Toast.makeText(ForumActivity.this, "Posting", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }

    protected void submitPost(String titleText, String descriptionText, String companyText) {
        Map<String, Object> postInfo = new HashMap<>();
        postInfo.put("Company", companyText);
        postInfo.put("description", descriptionText);
        postInfo.put("postTitle", titleText);


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
