package com.haedrian.haedrian.HomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.haedrian.haedrian.HomeScreen.IntroDemo.Fragments.IntroDemoActivity;
import com.haedrian.haedrian.UserInteraction.PinActivity;

public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        // TODO check internal db if new user
        Boolean newuser = true;

        // TODO:: Logan - on login it would be useful to have logic to see it is a newuser. If so it
        // should redirect to the signup page instead of login page

        if (newuser){
            startActivity(new Intent(this, IntroDemoActivity.class));
        }
        else {
            startActivity(new Intent(this, PinActivity.class));
        }
    }

}
