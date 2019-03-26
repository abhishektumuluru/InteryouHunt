package com.interyouhunt.hunt;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class stageFrag extends Fragment {
    private TextView textView;
    private static final String TAG = "stageFrag";
    public static final String ARG_OBJECT = "object";

    public stageFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "View SHould be displayed");
        View view =  inflater.inflate(R.layout.fragment_stage, container, false);
//        textView = view.findViewById(R.id.interview);
//        textView.setText(getArguments().getString("message"));
        Bundle args = getArguments();
//        ((TextView) view.findViewById(R.id.interview)).setText(
//                Integer.toString(args.getInt(ARG_OBJECT)));
        return  view;
    }

}
