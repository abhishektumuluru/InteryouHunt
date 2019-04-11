package com.interyouhunt.hunt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class intActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static final String TAG = "IntActivity";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    static HashMap<String, Object> map;
    static int numPages;


    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    static String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Bundle bundle1 = this.getIntent().getExtras();
        if(bundle1 != null) {
            map  = (HashMap<String, Object>) bundle1.getSerializable("interviewMap");
        }
        List<Map<String, Object>> stages = (List<Map<String, Object>>) map.get("stages");
        numPages = stages.size();
        mSectionsPagerAdapter.notifyDataSetChanged();
        if (user != null) {
            uid = user.getUid();
        }
        for (String name: map.keySet()){
            String key =name.toString();
            String value = map.get(name).toString();
            System.out.println(key + " " + value);
            Log.d(TAG, "MAP: " + key + "  " + value);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_int, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        Bundle extras = new Bundle();
        // Handle item selection
        switch (id) {
            case R.id.action_add:
                intent = new Intent(intActivity.this, AddStageActivity.class);
                extras.putSerializable("interviewMap", map);
                intent.putExtras(extras);
                startActivity(intent);
                return true;
            case R.id.action_edit:
                intent = new Intent(intActivity.this, AddPositionActivity.class);
                extras.putSerializable("interviewMap", map);
                intent.putExtras(extras);
                startActivity(intent);
                return true;
            case R.id.action_info:
                openInformationDialog(map);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            List<Map<String, Object>> stages = (List<Map<String, Object>>) map.get("stages");


            int ind = getArguments().getInt(ARG_SECTION_NUMBER);

            View rootView = inflater.inflate(R.layout.fragment_int, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.company);
            String company = (String) map.get("companyName");
            textView.setText(company + " Stage " + ind);

            TextView tv1 = (TextView) rootView.findViewById(R.id.dateTime);
            String datetime = "N/A";
            Timestamp ts =  (Timestamp)(stages.get(ind -1).get("datetime"));
            if (ts != null) {
                datetime = ts.toDate().toString();
            }
            tv1.setText(datetime);

            TextView tv2 = (TextView) rootView.findViewById(R.id.userLocation);
            tv2.setText((String)(stages.get(ind -1).get("location")));

            TextView tv3 = (TextView) rootView.findViewById(R.id.roundType);
            tv3.setText((String)(stages.get(ind -1).get("stage")));

            TextView tv4 = (TextView) rootView.findViewById(R.id.interviewType);
            StringBuilder sbType = new StringBuilder();
            for (String type: (List<String>) (stages.get(ind -1).get("type"))) {
                sbType.append(type + "/");
            }
            sbType.setLength(sbType.length() - 1);
            tv4.setText(sbType.toString());

            TextView tv5 = (TextView) rootView.findViewById(R.id.notesText);
            tv5.setText((String)(stages.get(ind -1).get("notes")));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return numPages;
        }
    }


    protected void openInformationDialog(final Map<String, Object> map) {
        intActivity.ViewDialog alert = new intActivity.ViewDialog();
        alert.openInformation(intActivity.this, map);
    }

    public class ViewDialog {

        private void openInformation(Activity activity, final Map<String, Object> interviewMap) {
            final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Light_Dialog_Alert);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.company_information_dialog);

            final String companyName = (String) interviewMap.get("companyName");
            final String position = (String) interviewMap.get("position");
            final String positionType = (String) interviewMap.get("positionType");
            final String recruiterEmail = (String) interviewMap.get("recruiterEmail");
            final String recruiterName = (String) interviewMap.get("recruiterName");
            final String recruiterPhoneNumber = (String) interviewMap.get("recruiterPhoneNumber");

            TextView companyNameTextView = dialog.findViewById(R.id.info_screen_company_name_displayed);
            TextView positionTextView = dialog.findViewById(R.id.info_screen_position_displayed);
            TextView positionTypeTextView = dialog.findViewById(R.id.info_screen_position_type_displayed);
            TextView recruiterEmailTextView = dialog.findViewById(R.id.info_screen_recruiter_email_displayed);
            TextView recruiterNameTextView = dialog.findViewById(R.id.info_screen_recruiter_name_displayed);
            TextView recruiterPhoneNumberTextView = dialog.findViewById(R.id.info_screen_recruiter_phone_number_displayed);


            companyNameTextView.setText(companyName);
            positionTextView.setText(position);
            positionTypeTextView.setText(positionType);
            recruiterEmailTextView.setText(recruiterEmail);
            recruiterNameTextView.setText(recruiterName);
            recruiterPhoneNumberTextView.setText(recruiterPhoneNumber);



            Button dialogButton = dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }

    }
}
