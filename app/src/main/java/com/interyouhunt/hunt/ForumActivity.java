package com.interyouhunt.hunt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
                final String titles[] = titlesList.toArray(new String[titlesList.size()]);
                final String descriptions[] = descriptionsList.toArray(new String[descriptionsList.size()]);
                final String companies[] = companiesList.toArray(new String[companiesList.size()]);
                final int images[] = new int[]{0, 0};


                CustomListAdapter adapter = new CustomListAdapter(ForumActivity.this, titles, descriptions, images, companies);
                forumListView.setAdapter(adapter);
                forumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Start a new activity or open up a new fragment with the post in it
                        Toast.makeText(ForumActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                        openForumPost(titles[position], descriptions[position]);
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

    protected void openForumPost(final String title, final String description) {
        // Open up a forum post here
        // Fragment or drawer
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

    public interface GetTipsCallback {
        void onCallback(List<Map<String, Object>> value);
    }
}
