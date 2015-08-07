package com.haedrian.curo.HomeScreen.ApplyForLoan.Projects;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.flurry.android.FlurryAgent;
import com.haedrian.curo.HomeScreen.ApplyForLoan.Projects.CreateProjectFragments.ProjectAboutFragment;
import com.haedrian.curo.HomeScreen.ApplyForLoan.Projects.CreateProjectFragments.ProjectCategoryFragment;
import com.haedrian.curo.HomeScreen.ApplyForLoan.Projects.CreateProjectFragments.ProjectDurationFragment;
import com.haedrian.curo.HomeScreen.ApplyForLoan.Projects.CreateProjectFragments.ProjectGoalFragment;
import com.haedrian.curo.HomeScreen.ApplyForLoan.Projects.CreateProjectFragments.ProjectLocationFragment;
import com.haedrian.curo.HomeScreen.ApplyForLoan.Projects.CreateProjectFragments.ProjectTitleFragment;
import com.haedrian.curo.R;


public class CreateProjectActivity extends ActionBarActivity
        implements ProjectTitleFragment.OnFragmentInteractionListener,
        ProjectAboutFragment.OnFragmentInteractionListener,
        ProjectCategoryFragment.OnFragmentInteractionListener,
        ProjectLocationFragment.OnFragmentInteractionListener,
        ProjectDurationFragment.OnFragmentInteractionListener,
        ProjectGoalFragment.OnFragmentInteractionListener {


    private Button nextButton, backButton, submitButton;
    private ImageView progressBar;
    private int currentFragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        nextButton = (Button) findViewById(R.id.next_button);
        backButton = (Button) findViewById(R.id.back_button);
        submitButton = (Button) findViewById(R.id.submit_button);

        progressBar = (ImageView) findViewById(R.id.progress_bar);


        // Fragment stuff
        currentFragment = 1;
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        ProjectTitleFragment projectTitleFragment = new ProjectTitleFragment();
        fragmentTransaction.add(R.id.fragment_container, projectTitleFragment);
        fragmentTransaction.commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_projects, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.next_button:
                nextButtonHandler();
                break;
            case R.id.back_button:
                backButtonHandler();
                break;
            case R.id.submit_button:
                submitButtonHandler();
                break;
            default:
                break;
        }

    }

    private void nextButtonHandler() {
        if (currentFragment == 1) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_left);
            ProjectAboutFragment projectAboutFragment = new ProjectAboutFragment();
            fragmentTransaction.replace(R.id.fragment_container, projectAboutFragment);
            fragmentTransaction.commit();
            currentFragment = 2;

            progressBar.setBackgroundResource(R.drawable.progresstwo);
            backButton.setVisibility(View.VISIBLE);
        } else if (currentFragment == 2) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_left);
            ProjectCategoryFragment projectCategoryFragment = new ProjectCategoryFragment();
            fragmentTransaction.replace(R.id.fragment_container, projectCategoryFragment);
            fragmentTransaction.commit();
            currentFragment = 3;

            progressBar.setBackgroundResource(R.drawable.progressthree);
        } else if (currentFragment == 3) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_left);
            ProjectLocationFragment projectLocationFragment = new ProjectLocationFragment();
            fragmentTransaction.replace(R.id.fragment_container, projectLocationFragment);
            fragmentTransaction.commit();
            currentFragment = 4;

            progressBar.setBackgroundResource(R.drawable.progressfour);
        } else if (currentFragment == 4) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_left);
            ProjectDurationFragment projectDurationFragment = new ProjectDurationFragment();
            fragmentTransaction.replace(R.id.fragment_container, projectDurationFragment);
            fragmentTransaction.commit();
            currentFragment = 5;

            progressBar.setBackgroundResource(R.drawable.progressfive);
        } else if (currentFragment == 5) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_left);
            ProjectGoalFragment projectGoalFragment = new ProjectGoalFragment();
            fragmentTransaction.replace(R.id.fragment_container, projectGoalFragment);
            fragmentTransaction.commit();
            currentFragment = 6;

            progressBar.setBackgroundResource(R.drawable.progresssix);
            nextButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    private void backButtonHandler() {
        if (currentFragment == 2) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_out_right);
            ProjectTitleFragment projectTitleFragment = new ProjectTitleFragment();
            fragmentTransaction.replace(R.id.fragment_container, projectTitleFragment);
            fragmentTransaction.commit();
            currentFragment = 1;

            progressBar.setBackgroundResource(R.drawable.progressone);
            backButton.setVisibility(View.GONE);
        } else if (currentFragment == 3) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_out_right);
            ProjectAboutFragment projectAboutFragment = new ProjectAboutFragment();
            fragmentTransaction.replace(R.id.fragment_container, projectAboutFragment);
            fragmentTransaction.commit();
            currentFragment = 2;

            progressBar.setBackgroundResource(R.drawable.progresstwo);
        } else if (currentFragment == 4) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_out_right);
            ProjectCategoryFragment projectCategoryFragment = new ProjectCategoryFragment();
            fragmentTransaction.replace(R.id.fragment_container, projectCategoryFragment);
            fragmentTransaction.commit();
            currentFragment = 3;

            progressBar.setBackgroundResource(R.drawable.progressthree);

        } else if (currentFragment == 5) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_out_right);
            ProjectLocationFragment projectLocationFragment = new ProjectLocationFragment();
            fragmentTransaction.replace(R.id.fragment_container, projectLocationFragment);
            fragmentTransaction.commit();
            currentFragment = 4;

            progressBar.setBackgroundResource(R.drawable.progressfour);
        } else if (currentFragment == 6) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_out_right);
            ProjectDurationFragment projectDurationFragment = new ProjectDurationFragment();
            fragmentTransaction.replace(R.id.fragment_container, projectDurationFragment);
            fragmentTransaction.commit();
            currentFragment = 5;

            progressBar.setBackgroundResource(R.drawable.progressfive);
            nextButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
    }

    private void submitButtonHandler() {
        Intent intent = new Intent(this, ProjectsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
