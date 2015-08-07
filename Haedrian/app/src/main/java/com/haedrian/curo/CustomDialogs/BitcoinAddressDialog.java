package com.haedrian.curo.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haedrian.curo.R;

/**
 * Created by Logan on 3/14/2015.
 */
public class BitcoinAddressDialog extends Dialog {

    private Context context;
    private String address;
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
        setContentView(R.layout.dialog_bitcoin_address);

        ImageView bitcoinQr = (ImageView) findViewById(R.id.dialog_image);
        TextView bitcoinAddress = (TextView) findViewById(R.id.wallet_address);
        ImageButton copyButton = (ImageButton) findViewById(R.id.copy_button);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(context.getResources().getString(R.string.dummy_address));
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("bitcoinAddress", address);
                    clipboard.setPrimaryClip(clip);
                }

                Toast.makeText(context, address + " was copied to the clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        bitcoinAddress.setText(address);

        bitcoinQr.setImageBitmap(qrCode);

    }
}
