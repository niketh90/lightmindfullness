package com.seraphic.lightapp.onbaording;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerADapter extends FragmentPagerAdapter {
    public ViewPagerADapter(FragmentManager fragmentManager ){
        super(fragmentManager);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
             return    new OnBoarding1Frag();
             case 1:
              return   new Onboarding2Frag();

             case 2:
             return    new OnBoarding3Frag();

             default:
             return    new OnBoarding1Frag();



        }
     }

    @Override
    public int getCount() {
        return 3;
    }
}
