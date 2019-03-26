package com.interyouhunt.hunt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "ViewPageAdapter";
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        stageFrag sf  = new stageFrag();
//        Log.d(TAG, "getItem: fragment has been instantiated");
//        i = i + 1;
//        Bundle bundle = new Bundle();
//        bundle.putString("message", "Testtttt: " + i);
//        sf.setArguments(bundle);
        return sf;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
