package com.haedrian.haedrian.HomeScreen;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends ActionBarActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double latitude;
    private double longitude;
    private List<LatLng> latLngs;
    private List<String> titles;
    private List<String> descriptions;

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

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        latLngs = new ArrayList<>();
        titles = new ArrayList<>();
        descriptions = new ArrayList<>();

        // Get locations
        getLocations();

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

        setContentView(R.layout.activity_maps2);
        setUpMapIfNeeded();
    }

    public void getLocations() {

        for (int i = 0; i < 9; i++) {

            String url = "http://coinmap.org/data/data-overpass-bitcoin-" + i + ".json";
            JsonArrayRequest currencyRequest = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    titles.add(jsonArray.getJSONObject(i).getString("title"));
                                    if (jsonArray.getJSONObject(i).has("desc")) {
                                        descriptions.add(jsonArray.getJSONObject(i).getString("desc"));
                                    }
                                    else {
                                        descriptions.add("");
                                    }
                                    LatLng latLng = new LatLng(Double.parseDouble(jsonArray.getJSONObject(i).getString("lat")), Double.parseDouble(jsonArray.getJSONObject(i).getString("lon")));
                                    latLngs.add(latLng);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            }
            );
            // Adding request to request queue
            ApplicationController.getInstance().addToRequestQueue(currencyRequest);
        }
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
            latitude = location.getLatitude();
            longitude = location.getLongitude();
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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 7.0f));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("You")
                .snippet(""));


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
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
        });

    }
}
