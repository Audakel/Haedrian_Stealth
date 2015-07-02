package com.haedrian.haedrian.HomeScreen.IntroDemo.Fragments;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Adapters.IntroDemoPager;
import com.haedrian.haedrian.HomeScreen.ApplyForLoan.Projects.ProjectsActivity;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.PinActivity;
import com.viewpagerindicator.*;

//IntroDemoActivity
public class IntroDemoActivity extends FragmentActivity {
    ViewPager viewPager;
    TextView skipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_demo);
        viewPager = (ViewPager) findViewById(R.id.intro_demo_pager);
        viewPager.setAdapter(new IntroDemoPager(getSupportFragmentManager()));

        skipText = (TextView) findViewById(R.id.skipTV);
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), PinActivity.class));

        // TODO:: using some random .aar from maven, find legit one
        // https://github.com/JakeWharton/ViewPagerIndicator
        //Bind the title indicator to the adapter
//        CirclePageIndicator mIndicator;
//        mIndicator = (CirclePageIndicator)findViewById(R.id.pageIndicator);
//        mIndicator.setViewPager(viewPager);


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
