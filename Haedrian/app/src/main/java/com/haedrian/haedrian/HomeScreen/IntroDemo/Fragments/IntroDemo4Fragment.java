package com.haedrian.haedrian.HomeScreen.IntroDemo.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.LoginActivity;
import com.haedrian.haedrian.UserInteraction.PinActivity;
import com.haedrian.haedrian.UserInteraction.SignupActivity;

public class IntroDemo4Fragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_intro_demo4, container, false);
        Button startButton = (Button) rootView.findViewById(R.id.start_now_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(rootView.getContext(), SignupActivity.class));
                getActivity().finish();
            }
        });

        Button loginButton = (Button) rootView.findViewById(R.id.sign_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(rootView.getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return rootView;
    }



}