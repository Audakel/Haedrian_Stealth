package com.haedrian.haedrian.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.Button;

import com.haedrian.haedrian.HomeScreen.IntroDemo.Fragments.IntroDemo1Fragment;
import com.haedrian.haedrian.HomeScreen.IntroDemo.Fragments.IntroDemo2Fragment;
import com.haedrian.haedrian.HomeScreen.IntroDemo.Fragments.IntroDemo3Fragment;
import com.haedrian.haedrian.HomeScreen.IntroDemo.Fragments.IntroDemo4Fragment;

/**
 * Created by audakel on 6/29/15.
 */
public class IntroDemoPager extends FragmentPagerAdapter {

    public IntroDemoPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position == 0) {
            fragment = new IntroDemo1Fragment();
        }
        else if (position == 1) {
            fragment = new IntroDemo2Fragment();
        }
        else if (position == 2) {
            fragment = new IntroDemo3Fragment();
        }
        else if (position == 3) {
            fragment = new IntroDemo4Fragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
