package com.haedrian.haedrian;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


public class ProjectsActivity extends ActionBarActivity {


    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    private static String addressString;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        // Location stuff
        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(svcName);


        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        String provider = locationManager.getBestProvider(criteria, true);

        Location l = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(l);

        locationManager.requestLocationUpdates(provider, 200, 10, locationListener);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_projects, menu);
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

    public void switchFragment(int id) {
        mViewPager.setCurrentItem(id);
    }

    private void updateWithNewLocation(Location location) {
        if(location != null) {
            double latitude = location.getLatitude();
            double lng = location.getLongitude();

            Geocoder gc = new Geocoder(this, Locale.getDefault());

            try {
                List<Address> addresses = gc.getFromLocation(latitude, lng, 1);
                StringBuilder sb = new StringBuilder();
                if(addresses.size() > 0) {
                    Address address = addresses.get(0);
//
//                    for(int i = 0; i < address.getMaxAddressLineIndex(); i++){
//                        sb.append(address.getAddressLine(i)).append("\n");
//                    }

                    sb.append(address.getLocality()).append(", ");
                    sb.append(address.getCountryName());
                }

                addressString = sb.toString();

            } catch(IOException e) {
                Log.v("Test-Error", String.valueOf(e));
            }

        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.

            switch (position) {
                case 0:
                    return ProjectTitleFragment.newInstance(position + 1);
                case 1:
                    return ProjectAboutFragment.newInstance(position + 1);
                case 2:
                    return ProjectCategoryFragment.newInstance(position + 1);
                case 3:
                    return ProjectLocationFragment.newInstance(position + 1);
                case 4:
                    return ProjectDurationFragment.newInstance(position + 1);
                case 5:
                    return ProjectGoalFragment.newInstance(position + 1);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }


    public static class ProjectTitleFragment extends Fragment {

        Button nextButton;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProjectTitleFragment newInstance(int sectionNumber) {
            ProjectTitleFragment fragment = new ProjectTitleFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ProjectTitleFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_project_title, container, false);

            nextButton = (Button) rootView.findViewById(R.id.next_button);

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProjectsActivity)getActivity()).switchFragment(1);
                }
            });

            return rootView;
        }
    }

    public static class ProjectAboutFragment extends Fragment {

        Button backButton, nextButton;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProjectAboutFragment newInstance(int sectionNumber) {
            ProjectAboutFragment fragment = new ProjectAboutFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ProjectAboutFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_project_about, container, false);

            backButton = (Button) rootView.findViewById(R.id.back_button);
            nextButton = (Button) rootView.findViewById(R.id.next_button);

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProjectsActivity)getActivity()).switchFragment(0);
                }
            });

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProjectsActivity)getActivity()).switchFragment(2);
                }
            });

            return rootView;
        }
    }

    public static class ProjectCategoryFragment extends Fragment {

        Spinner categorySpinner;
        Button backButton, nextButton;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProjectCategoryFragment newInstance(int sectionNumber) {
            ProjectCategoryFragment fragment = new ProjectCategoryFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ProjectCategoryFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_project_category, container, false);

            categorySpinner = (Spinner) rootView.findViewById(R.id.category_spinner);
            backButton = (Button) rootView.findViewById(R.id.back_button);
            nextButton = (Button) rootView.findViewById(R.id.next_button);

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProjectsActivity)getActivity()).switchFragment(1);
                }
            });

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProjectsActivity)getActivity()).switchFragment(3);
                }
            });

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                    R.array.categories_array, android.R.layout.simple_spinner_dropdown_item);

            categorySpinner.setAdapter(adapter);

            return rootView;
        }
    }

    public static class ProjectLocationFragment extends Fragment {

        private Button locationButton, backButton, nextButton;
        private TextView locationText;

        Context context;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProjectLocationFragment newInstance(int sectionNumber) {
            ProjectLocationFragment fragment = new ProjectLocationFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ProjectLocationFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_project_location, container, false);

            context = rootView.getContext();

            locationButton = (Button) rootView.findViewById(R.id.location_button);
            locationText = (TextView) rootView.findViewById(R.id.location_text);
            backButton = (Button) rootView.findViewById(R.id.back_button);
            nextButton = (Button) rootView.findViewById(R.id.next_button);

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProjectsActivity)getActivity()).switchFragment(2);
                }
            });

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProjectsActivity)getActivity()).switchFragment(4);
                }
            });

            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locationButton.setVisibility(View.GONE);
                    locationText.setVisibility(View.VISIBLE);
                    locationText.setText(addressString);
                }
            });

            return rootView;
        }
    }

    public static class ProjectDurationFragment extends Fragment {

        Button backButton, nextButton;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProjectDurationFragment newInstance(int sectionNumber) {
            ProjectDurationFragment fragment = new ProjectDurationFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ProjectDurationFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_project_duration, container, false);

            backButton = (Button) rootView.findViewById(R.id.back_button);
            nextButton = (Button) rootView.findViewById(R.id.next_button);

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProjectsActivity)getActivity()).switchFragment(3);
                }
            });

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProjectsActivity)getActivity()).switchFragment(5);
                }
            });
            return rootView;
        }
    }

    public static class ProjectGoalFragment extends Fragment {

        Button backButton;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProjectGoalFragment newInstance(int sectionNumber) {
            ProjectGoalFragment fragment = new ProjectGoalFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ProjectGoalFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_project_goal, container, false);

            backButton = (Button)rootView.findViewById(R.id.back_button);

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProjectsActivity)getActivity()).switchFragment(4);
                }
            });

            return rootView;
        }
    }



}
