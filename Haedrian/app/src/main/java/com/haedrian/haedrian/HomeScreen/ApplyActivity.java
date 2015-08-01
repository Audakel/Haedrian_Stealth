package com.haedrian.haedrian.HomeScreen;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.haedrian.haedrian.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class ApplyActivity extends AppCompatActivity {

    private Spinner microfinanceSpinner, officeSpinner, genderSpinner;
    private ArrayList<String> mfis = new ArrayList<>();
    private ArrayList<String> offices = new ArrayList<>();
    private ArrayList<String> genders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        microfinanceSpinner = (Spinner) findViewById(R.id.microfinance_spinner);
        officeSpinner = (Spinner) findViewById(R.id.office_spinner);
        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);

        mfis.add(getString(R.string.choose_microfinance));
        mfis.add(getString(R.string.mentors_international));

        offices.add(getString(R.string.choose_office));
        offices.add(getString(R.string.test_office));

        genders.add(getString(R.string.choose_gender));
        genders.add(getString(R.string.male));
        genders.add(getString(R.string.female));
        genders.add(getString(R.string.prefer_not_to_answer));

        ArrayAdapter<String> mfiAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mfis);
        ArrayAdapter<String> officeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, offices);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genders);

        microfinanceSpinner.setAdapter(mfiAdapter);
        officeSpinner.setAdapter(officeAdapter);
        genderSpinner.setAdapter(genderAdapter);

        microfinanceSpinner.setSelection(0);
        officeSpinner.setSelection(0);
        genderSpinner.setSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_apply, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.birthdate_picker:
                showDatePicker();
                break;
        }
    }

    private void showDatePicker() {

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
        }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            // 18 years and older
            int year = c.get(Calendar.YEAR) - 18;
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }

}
