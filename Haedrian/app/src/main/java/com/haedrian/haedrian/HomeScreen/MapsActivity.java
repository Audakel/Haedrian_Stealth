package com.haedrian.haedrian.HomeScreen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.PinActivity;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends ActionBarActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double latitude;
    private double longitude;
    private List<LatLng> latLngs;
    private List<String> titles;
    private List<String> descriptions;
    private Spinner locationSpinner, outletSpinner;

    private ProgressDialog progressDialog;

    private ArrayList<String> depositLocations = new ArrayList<>();
    private ArrayList<ArrayList<String>> outletLocations = new ArrayList<>();

    private Map<String, String> outletDictionary;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (ApplicationController.getToken().equals("")) {
            Intent intent = new Intent(this, PinActivity.class);
            startActivity(intent);
            finish();
        }

        latLngs = new ArrayList<>();
        titles = new ArrayList<>();
        descriptions = new ArrayList<>();

        locationSpinner = (Spinner) findViewById(R.id.location_spinner);
        outletSpinner = (Spinner) findViewById(R.id.outlet_spinner);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_loading));
        progressDialog.show();

        // Get exchange types
        getExchangeTypes();

        outletDictionary = new HashMap<>();
        fillOutletDictionary();

        // Location stuff
        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(svcName);


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

        setUpMapIfNeeded();
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
        progressDialog.dismiss();
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
        FlurryAgent.onEndSession(this);
    }

//    private void fillLocationInfo() {
//       depositLocations.add(getString(R.string.bdo));
//       depositLocations.add(getString(R.string.bpi));
//       depositLocations.add(getString(R.string.globe_gcash));
//       depositLocations.add(getString(R.string.security_bank));
//       depositLocations.add(getString(R.string.union_bank));
//
//    }

    public void getExchangeTypes() {
        String URL = ApplicationConstants.BASE + "exchanges/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray locations = response.getJSONArray("locations");
                            for (int i = 0; i < locations.length(); i++) {
                                depositLocations.add(locations.getJSONObject(i).getString("name"));
                                JSONArray outlets = locations.getJSONObject(i).getJSONArray("outlets");
                                ArrayList<String> outlet = new ArrayList<>();
                                for (int j = 0; j < outlets.length(); j++) {
                                    outlet.add(outlets.getJSONObject(j).getString("name"));
                                }
                                outletLocations.add(outlet);
                            }
                            // Get locations
                            getLocations();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Test", "Error: " + error.toString());
                progressDialog.dismiss();
            }

        }) {
            @Override
            public HashMap<String, String> getHeaders() {
                String token = ApplicationController.getToken();
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " + token);
                params.put("Content-Type", "application/json;charset=UTF-8");
                params.put("Accept", "application/json");
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new TimeoutRetryPolicy());

        // Adds request to the request queue
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void getLocations() {
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, depositLocations);
        locationSpinner.setAdapter(locationAdapter);
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Set up outlets here
                FlurryAgent.logEvent("User searched for the locations of this bank: " + depositLocations.get(position));
                ArrayList<String> tempOutlets = outletLocations.get(position);
                getOutlets(tempOutlets, position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void getOutlets(ArrayList<String> tempOutlets, final int locationPosition) {
        progressDialog.dismiss();
        ArrayAdapter<String> outletAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tempOutlets);
        outletSpinner.setAdapter(outletAdapter);
        outletSpinner.setVisibility(View.VISIBLE);
        outletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                latLngs.clear();
                titles.clear();
                descriptions.clear();
                mMap.clear();
                String outlet = outletLocations.get(locationPosition).get(position);
                FlurryAgent.logEvent("User searched for this outlet:" + outlet);
                getOutletLocations(outlet);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getOutletLocations(String outlet) {
        String query = "";

        // Dictionary search for the outlet to map to a google places api readable search query
        if (outletDictionary.containsKey(outlet)) {
            query = outletDictionary.get(outlet);
        }
        else {
            query = outlet;
        }

        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longitude);
        String url = ApplicationConstants.BASE
                + "locations/?query=" + query
                + "&lat=" + lat
                + "&lng=" + lng;

        JsonArrayRequest locationsRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Double lat = Double.parseDouble(object.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                                Double lng = Double.parseDouble(object.getJSONObject("geometry").getJSONObject("location").getString("lng"));
                                LatLng latLng = new LatLng(lat, lng);

                                String title = object.getString("name");
                                String description = object.getString("vicinity");

                                latLngs.add(latLng);
                                titles.add(title);
                                descriptions.add(description);
                            }
                            drawLocations();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("TEST", volleyError.toString());
                progressDialog.dismiss();
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() {
                String token = ApplicationController.getToken();
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " + token);
                params.put("Content-Type", "application/json;charset=UTF-8");
                params.put("Accept", "application/json");

                return params;
            }
        };

        locationsRequest.setRetryPolicy(new TimeoutRetryPolicy());

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(locationsRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

    private void updateWithNewLocation(Location location) {
        if (location != null) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
            latitude = 14.575426;
            longitude = 121.084511;
            FlurryAgent.logEvent("User/'s Latitude: " + latitude + " User/'s Longitude: " + longitude);
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14.0f));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(getResources().getString(R.string.you))
                .snippet(""));


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                drawLocations();
            }
        });

    }

    public void drawLocations() {
        long start = SystemClock.uptimeMillis();
        int count = 0;
        Projection projection = mMap.getProjection();
        LatLngBounds bounds = projection.getVisibleRegion().latLngBounds;
        for (int i = latLngs.size() - 1; i >= 0; i--) {
            LatLng position = latLngs.get(i);
            if (bounds.contains(position)) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLngs.get(i))
                        .title(titles.get(i))
                        .snippet(descriptions.get(i))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bitcoinmarker))
                        .snippet(""));

                latLngs.remove(i);
                count++;
            }
        }
        long end = SystemClock.uptimeMillis();
    }

    // This is really dumb but coins.ph doesn't provide us with a good search term to query the google places api with
    // so we are hardcoding a dictionary with the coins.ph response to a google places api query worthy string
    public void fillOutletDictionary() {
        outletDictionary.put("Rcbc (Via Dragonpay)", "rcbx");
        outletDictionary.put("Security Bank", "Security Bank");
        outletDictionary.put("Dragonpay Rcbx Deposit", "rcbx");
        outletDictionary.put("Securitybank Deposit", "Security Bank");
        outletDictionary.put("Union Deposit", "Union Bank");
        outletDictionary.put("Bpi Deposit", "bpi deposit");
        outletDictionary.put("Dragonpay Mbtx Deposit", "Metrobank");
        outletDictionary.put("Bdo Deposit", "banco de oro");
        outletDictionary.put("Dragonpay Cbcx Deposit", "cbcx");
        outletDictionary.put("Bdo 24 Hour Atm Deposit", "bdo atm");
        outletDictionary.put("Bdo Atm Deposit", "bdo atm");
        outletDictionary.put("M Lhuillier Epay", "m lhuillier");
        outletDictionary.put("Lbc Bills Xpress (Via Dragonpay)", "lbc");
        outletDictionary.put("Mlhuillier Deposit", "m lhuillier");
        outletDictionary.put("Dragonpay Lbc Deposit", "lbc");
        outletDictionary.put("Dragonpay Cebp Deposit", "dragonpay");
        outletDictionary.put("Dragonpay Bayd Deposit", "dragonpay");
    }
}
