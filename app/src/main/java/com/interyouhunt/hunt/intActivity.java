package com.interyouhunt.hunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class intActivity extends AppCompatActivity {

    private static final String TAG = "IntActivity";


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentStatePagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static SectionsPagerAdapter mSectionsPagerAdapter;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;
    private static TextView emptyMessage;
    private static TabLayout tabLayout;

    static HashMap<String, Object> map;
    static int numPages;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static FirebaseAuth mAuth;
    static String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null) {
            map  = (HashMap<String, Object>) bundle.getSerializable("interviewMap");
        }
        List<Map<String, Object>> stages = (List<Map<String, Object>>) map.get("stages");
        numPages = stages.size();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        emptyMessage = findViewById(R.id.empty_message);
        if (numPages == 0) {
            emptyMessage.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
        }
        mSectionsPagerAdapter.notifyDataSetChanged();
        if (user != null) {
            uid = user.getUid();
        }

//        for(int i = 0; i < tabLayout.getTabCount(); i++) {
//            tabLayout.getTabAt(i).setText("Stage " + (i + 1));
//        }
        tabLayout.setupWithViewPager(mViewPager);
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
        private static final String ARG_STAGE_NUMBER = "stage_number";

        public Activity activity;

        @Override
        public void onAttach(Activity activity){
            super.onAttach(activity);
            this.activity = activity;
        }

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
            final int ind = getArguments().getInt(ARG_SECTION_NUMBER);
            List<Map<String, Object>> stages = (List<Map<String, Object>>) map.get("stages");
            Map<String, Object> stage = stages.get(ind);
            View rootView = inflater.inflate(R.layout.fragment_int, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.company);
            String company = (String) map.get("companyName");
            Long stageNum = (Long) stage.get("stageNum");
            textView.setText(company + " Stage " + stageNum);
            TextView tv1 = (TextView) rootView.findViewById(R.id.dateTime);
            TextView tv2 = (TextView) rootView.findViewById(R.id.userLocation);
            TextView tv3 = (TextView) rootView.findViewById(R.id.roundType);
            TextView tv4 = (TextView) rootView.findViewById(R.id.interviewType);
            TextView tv5 = (TextView) rootView.findViewById(R.id.notesText);
            Timestamp ts =  (Timestamp)(stage.get("datetime"));
            String datetime = "N/A";
            if (ts != null) {
                datetime = ts.toDate().toString();
            }
            tv1.setText(datetime);
            tv2.setText((String)(stage.get("location")));
            tv3.setText((String)(stage.get("stage")));
            StringBuilder sbType = new StringBuilder();
            for (String type: (List<String>) (stage.get("type"))) {
                sbType.append(type + "/");
            }
            if (sbType.length() > 0) {
                sbType.setLength(sbType.length() - 1);
            }
            tv4.setText(sbType.toString());
            tv5.setText((String)(stage.get("notes")));

            Button editStageButton = rootView.findViewById(R.id.btn_edit_stage);
            Button deleteStageButton = rootView.findViewById(R.id.btn_delete_stage);

            editStageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddStageActivity.class);
                    Bundle extras = new Bundle();
                    extras.putSerializable("interviewMap", map);
                    extras.putInt("stageNum", ind);
                    extras.putBoolean("isEditing", true);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });

            deleteStageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog diaBox = AskOption();

                    //2. now setup to change color of the button
                    diaBox.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            diaBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.argb(200,102, 205, 170));
                        }
                    });
                    diaBox.show();
                }
            });
            return rootView;
        }

        private AlertDialog AskOption() {
            AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())
            //set message, title, and icon
                .setTitle("Delete Stage")
                    .setMessage("Do you want to delete this stage?")
                    .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //your deleting code
                            removeStageFromFirestore();
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            return myQuittingDialogBox;
        }

        private void removeStageFromFirestore() {
            FirebaseUser user = mAuth.getCurrentUser();
            String uid = user.getUid();
            String docID = (String) map.get("docID");
            final int stageNum = mViewPager.getCurrentItem();
            final ArrayList<Map<String, Object>> stages = (ArrayList<Map<String, Object>>) map.get("stages");
            final ArrayList<Map<String, Object>> stagesClone = (ArrayList<Map<String, Object>>) stages.clone();
            stagesClone.remove(stageNum);
            db.collection("users").document(uid).collection("Interviews").document(docID).update(
                    "stages", stagesClone
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    stages.remove(stageNum);
                    numPages = stages.size();
                    mSectionsPagerAdapter.deletePage(stageNum);
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    Toast.makeText(activity, "Removed stage", Toast.LENGTH_LONG).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error removing stage", e);
                        }
                    });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Integer> pageIndexes;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            pageIndexes = new ArrayList<>();
            for (int i = 0; i < numPages; i++) {
                pageIndexes.add(new Integer(i));
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return pageIndexes.size();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);

        }
        // This is called when notifyDataSetChanged() is called
        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            if (numPages == 0) {
                mViewPager.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.GONE);
            } else {
                mViewPager.setVisibility(View.VISIBLE);
                emptyMessage.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
            }
            return PagerAdapter.POSITION_NONE;
        }


        // Delete a page at a `position`
        public void deletePage(int position) {
            // Remove the corresponding item in the data set
            pageIndexes.remove(position);
            // Notify the adapter that the data set is changed
            notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            List<Map<String, Object>> stages = (List<Map<String, Object>>) map.get("stages");
            Map<String, Object> stage = stages.get(position);
            Long stageNum = (Long) stage.get("stageNum");
            return "Stage  " + String.valueOf(stageNum);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(intActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
