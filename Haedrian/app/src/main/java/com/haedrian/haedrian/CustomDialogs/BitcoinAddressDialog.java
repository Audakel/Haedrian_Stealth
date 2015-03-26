package com.haedrian.haedrian.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.haedrian.haedrian.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Logan on 3/14/2015.
 */
public class BitcoinAddressDialog extends Dialog {

    private ImageView bitcoinQr;
    private Context context;
    private String address;
    private TextView bitcoinAdress;
    private Bitmap qrCode;

    public BitcoinAddressDialog(Context context, String address, Bitmap qrCode) {
        super(context);
        this.context = context;
        this.address = address;
        this.qrCode = qrCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bitcoin_address_dialog);

        bitcoinQr = (ImageView) findViewById(R.id.dialog_image);
        bitcoinAdress = (TextView) findViewById(R.id.bitcoin_address_dialog);

        bitcoinAdress.setText(address);

        bitcoinQr.setImageBitmap(qrCode);

//        Picasso.with(context)
//                .load(R.drawable.qrcode)
//                .fit()
//                .centerInside()
//                .into(bitcoinQr);
    }
}
