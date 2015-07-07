package com.haedrian.haedrian.HomeScreen.IntroDemo.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Adapters.IntroDemoPager;
import com.haedrian.haedrian.HomeScreen.ApplyForLoan.Projects.ProjectsActivity;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.LoginActivity;
import com.haedrian.haedrian.UserInteraction.PinActivity;
import com.viewpagerindicator.*;

//IntroDemoActivity
public class IntroDemoActivity extends FragmentActivity {
    ViewPager viewPager;
    Button skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_demo);

        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        Boolean firstTime = sp.getBoolean("first_time", true);

        if ( ! firstTime) {
            startActivity(new Intent(getApplication(), LoginActivity.class));
            finish();
        }

        viewPager = (ViewPager) findViewById(R.id.intro_demo_pager);
        skipButton = (Button) findViewById(R.id.skip_button);

        viewPager.setAdapter(new IntroDemoPager(getSupportFragmentManager()));

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), LoginActivity.class));
                finish();
            }
        });

//      https://github.com/JakeWharton/ViewPagerIndicator
//      Bind the title indicator to the adapter
        CirclePageIndicator mIndicator;
        mIndicator = (CirclePageIndicator)findViewById(R.id.pageIndicator);
        mIndicator.setViewPager(viewPager);

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.v("TEST", "position: " + position);
                if (position == 3) {
                    skipButton.setVisibility(View.GONE);
                }
                else {
                    skipButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
        FlurryAgent.logEvent(this.getClass().getName() + " opened.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
        FlurryAgent.onEndSession(this);
    }



}
