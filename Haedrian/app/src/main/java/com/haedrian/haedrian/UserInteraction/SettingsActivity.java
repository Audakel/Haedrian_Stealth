package com.haedrian.haedrian.UserInteraction;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.haedrian.haedrian.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.sign_out_container:
                // In the future lock the wallet and go to signin page
                // For now just finish() and system.exit(0) <- BAD PRACTICE
                finish();
                System.exit(0);
        }
    }


}
