package com.lenddo.widget.address;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lenddo.widget.address.utils.GeoAddress;
import com.lenddo.widget.address.utils.Geocoder;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.lenddo.sdk.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddressDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddressDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddressDialogFragment extends DialogFragment implements View.OnClickListener, GoogleMap.OnMapClickListener {
    public static final String PH_LOCALE = "PH";
    public static final String CO_LOCALE = "CO";
    public static final String MX_LOCALE = "MX";

    private static final String ARG_LOCALE = "locale";
    private static final String TAG = AddressDialogFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView dialogTitle;
    private boolean isModal;
    private TextView fieldZipCode;

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private AutoCompleteTextView placesAutoComplete;
    private String prefillAddress;
    private String headerLabel;
    private TextView titleTextView;
    private TextView instructionsTextView;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            ApplicationInfo ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);

            Bundle bundle = ai.metaData;
            String myApiKey = null;

            if (bundle != null) {
                myApiKey = bundle.getString("googlePlacesApiKey", null);
            }

            if (TextUtils.isEmpty(myApiKey)) {
                return new ArrayList<String>();
            }

            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + myApiKey);
            sb.append("&components=country:ph");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return resultList;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            Log.d(TAG, jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    public void setHeaderLabel(String headerLabel) {
        this.headerLabel = headerLabel;
        if (titleTextView != null) {
            titleTextView.setText(headerLabel);
        }
    }

    public String getHeaderLabel() {
        return headerLabel;
    }

    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    private OnFragmentInteractionListener mListener;
    private View mapContainer;
    private View addressFormContainer;
    private GoogleMap mMap;
    private Marker marker;

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public void setCurrentLatLng(LatLng currentLatLng) {
        this.currentLatLng = currentLatLng;
    }

    private LatLng currentLatLng;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param locale Parameter 1.
     * @return A new instance of fragment AddressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressDialogFragment newInstance(String locale) {
        AddressDialogFragment fragment = new AddressDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOCALE, locale);
        fragment.setArguments(args);
        fragment.isModal = true; // WHEN FRAGMENT IS CALLED AS A DIALOG SET FLAG
        return fragment;
    }

    public AddressDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.AddressDialogTheme)
                .create();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_address, null);
        onViewCreated(view, savedInstanceState);
        dialog.setView(view);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setEnabled(false);
                b.setText(getResources().getString(R.string.confirm));
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currentLatLng != null) {
                            mListener.onAddressConfirmed(currentLatLng);
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), getResources().getText(R.string.marker_required), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getText(R.string.confirm), (DialogInterface.OnClickListener) null);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return dialog;
    }

    public <T> ArrayList<T> reverse(ArrayList<T> list) {
        for (int i = 0, j = list.size() - 1; i < j; i++) {
            list.add(i, list.remove(j));
        }
        return list;
    }

    public ArrayList<String> parseLocationString(String locationName) {
        ArrayList<String> results = new ArrayList<String>();
        String[] splitted = StringUtils.split(locationName, ",");
        for (int i = 0; i < splitted.length; i++) {
            ArrayList<String> tokens = new ArrayList<String>();
            for (int n = splitted.length - i; n <= splitted.length - 1; n++) {
                tokens.add(splitted[n]);
            }
            results.add(StringUtils.join(tokens, ", "));
        }
        results.add(locationName);
        return reverse(results);
    }

    class GeocoderAsyncTask extends AsyncTask<Void, Void, GeoAddress> {

        float defaultZoom = 13f;

        @Override
        protected GeoAddress doInBackground(Void... voids) {
            String locationName = getLocationName(false);
            ArrayList<String> locationCandidates = parseLocationString(locationName);
            Geocoder geocoder = new Geocoder(getActivity());
            if (locationCandidates.size() > 0) {
                for (String locationCandidate : locationCandidates) {
                    Log.d(TAG, "searching location " + locationCandidate);
                    List<GeoAddress> location = null;
                    try {
                        location = geocoder.getFromLocationName(locationCandidate, 10);
                        if (location != null && location.size() > 0) {
                            return location.get(0);
                        }
                    } catch (Geocoder.LimitExceededException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(GeoAddress address) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(address.getLatitude(), address.getLongitude()));
            mMap.moveCamera(cameraUpdate);
            if (address.getSouthWest() != null) {
                LatLngBounds bounds = new LatLngBounds(address.getSouthWest(), address.getNorthEast());
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
            } else {
                mMap.moveCamera(CameraUpdateFactory.zoomTo(defaultZoom));
            }

        }
    }

    public void moveToPlace() {
        Toast.makeText(getActivity(), "Loading", Toast.LENGTH_SHORT).show();
        GeocoderAsyncTask geocoderAsyncTask = new GeocoderAsyncTask();
        geocoderAsyncTask.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_LOCALE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (isModal) // AVOID REQUEST FEATURE CRASH
        {
            return super.onCreateView(inflater, container, savedInstanceState);
        } else {
            return inflater.inflate(R.layout.fragment_address, container);
        }
    }

    public void setCurrentAddress(String prefilledAddress) {
        this.prefillAddress = prefilledAddress;
    }

    TimerTask task;
    Timer timer = new Timer();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView = (TextView) view.findViewById(R.id.dialogTitleMap);
        instructionsTextView = (TextView) view.findViewById(R.id.mapInstructions);

        if (headerLabel != null) {
            titleTextView.setText(headerLabel);
        }
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                instructionsTextView.setAlpha(0);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                instructionsTextView.setAlpha(1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        instructionsTextView.startAnimation(fadeInAnimation);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
                            fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    instructionsTextView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });

                            instructionsTextView.startAnimation(fadeOutAnimation);


                        }
                    });
                }
            }
        }, 5000);
        placesAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.addressField);
        placesAutoComplete.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), android.R.layout.simple_list_item_1));
        placesAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 2) {
                    if (task != null) {
                        task.cancel();
                    }
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    moveToPlace();
                                }
                            });

                        }
                    };
                    timer.schedule(task, 1000);
                }
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.addressMap);
        mMap = mapFragment.getMap();

        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);

            if (currentLatLng != null) {
                marker = mMap.addMarker(new MarkerOptions().position(currentLatLng).draggable(true));
                marker.setDraggable(true);
            }

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    currentLatLng = marker.getPosition();
                }
            });

            mMap.setOnMapClickListener(this);

            if (!TextUtils.isEmpty(prefillAddress)) {
                placesAutoComplete.setText(prefillAddress);
                moveToPlace();
            } else {
                //default to my location
                Location location = mMap.getMyLocation();

                if (location != null) {
                    LatLng myLocation = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13.0f));
                }

            }


            if (currentLatLng != null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
                mMap.moveCamera(cameraUpdate);
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
            }
        } else {
            Toast.makeText(getActivity(), "Maps is not supported on your device. Google Play Service required.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {

    }

    public boolean validateFields() {
        boolean result = true;
        return result;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (marker != null) {
            marker.remove();
        }
        Fragment f = (Fragment) getFragmentManager().findFragmentById(R.id.addressMap);
        if (f != null) {
            getFragmentManager().beginTransaction().remove(f).commit();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    private String getLocationName(boolean reduceScope) {
        return placesAutoComplete.getText().toString();
    }


    private void showMap() {
        mapContainer.setVisibility(View.VISIBLE);
        addressFormContainer.setVisibility(View.GONE);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        currentLatLng = latLng;
        Button confirmButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        confirmButton.setEnabled(true);
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        } else {
            marker.setPosition(latLng);
        }
    }

    public boolean isAddressShown() {
        return addressFormContainer.isShown();
    }

    public void showAddress() {
        mapContainer.setVisibility(View.GONE);
        addressFormContainer.setVisibility(View.VISIBLE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);

        void onAddressConfirmed(LatLng address);
    }

}
