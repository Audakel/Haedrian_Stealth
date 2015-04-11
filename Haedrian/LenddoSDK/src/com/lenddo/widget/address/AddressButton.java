package com.lenddo.widget.address;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.lenddo.widget.address.models.Address;

import com.lenddo.sdk.R;

/**
 * Created by joseph on 11/4/14.
 */
public class AddressButton extends TextView implements View.OnClickListener, AddressDialogFragment.OnFragmentInteractionListener {

    private static final String TAG = AddressButton.class.getName();
    private OnAddressConfirmedListener listener;
    private AddressDialogFragment addressDialogFragment;
    LatLng currentAddress;
    private OnPrefillListener prefillListener;
    private String headerLabel = getResources().getString(com.lenddo.sdk.R.string.verify_your_address);

    public String getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(String currentLocale) {
        this.currentLocale = currentLocale;
    }

    String currentLocale = AddressDialogFragment.PH_LOCALE;

    public LatLng getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(LatLng currentAddress) {
        this.currentAddress = currentAddress;
    }

    public AddressButton(Context context) {
        super(context);
        setupView(null);
    }

    public AddressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView(attrs);
    }

    public AddressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView(attrs);
    }

    private void setupView(AttributeSet attrs) {
        if (!isInEditMode()) {
            // Create and show the dialog.
            addressDialogFragment = AddressDialogFragment.newInstance(currentLocale);

            setOnClickListener(this);
            if (attrs!=null) {
                TypedArray a = getContext().obtainStyledAttributes(
                        attrs,
                        R.styleable.AddressButton);
                headerLabel = a.getString(R.styleable.AddressButton_headerLabel);
                if (headerLabel!=null) {
                    addressDialogFragment.setHeaderLabel(headerLabel);
                }
                Log.d(TAG,"header_label passed " + headerLabel);
                a.recycle();
            } else {
                Log.d(TAG,"no header label passed");
            }
        }

    }

    @Override
    public void onClick(View view) {
        Activity activity = (Activity)getContext();
        FragmentTransaction ft =  activity.getFragmentManager().beginTransaction();


        Fragment prev = activity.getFragmentManager().findFragmentByTag("addressDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        addressDialogFragment.setmListener(this);
        addressDialogFragment.show(ft, "addressDialog");

        if (prefillListener!=null) {
            Address prefilledAddress = prefillListener.getAddress();
            addressDialogFragment.setCurrentLatLng(currentAddress);
            addressDialogFragment.setCurrentAddress(prefilledAddress.toString());
        }

    }

    public void setPrefillListener(OnPrefillListener listener) {
        this.prefillListener = listener;

    }
    public void setOnAddressConfirmedListener(OnAddressConfirmedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAddressConfirmed(LatLng address) {
        this.currentAddress = address;
        setText(getContext().getString(R.string.address_set));
        setError(null);
        if (listener!=null) {
            listener.onAddressConfirmed(address);
        } else {
            Log.w(TAG, "OnAddressConfirmedListener was not set");
        }
    }
}
