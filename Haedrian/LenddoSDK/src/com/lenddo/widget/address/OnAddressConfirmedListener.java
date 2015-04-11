package com.lenddo.widget.address;

import com.google.android.gms.maps.model.LatLng;
import com.lenddo.widget.address.models.Address;

/**
 * Created by joseph on 11/5/14.
 */
public interface OnAddressConfirmedListener {
    void onAddressConfirmed(LatLng address);
}
