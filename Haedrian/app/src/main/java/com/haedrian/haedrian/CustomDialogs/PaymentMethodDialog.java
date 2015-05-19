package com.haedrian.haedrian.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.haedrian.haedrian.Adapters.PaymentMethodListAdapter;
import com.haedrian.haedrian.R;

import java.util.ArrayList;

/**
 * Created by Logan on 5/18/2015.
 */
public class PaymentMethodDialog extends Dialog {

    private ListView paymentMethodList;
    private Context context;
    private ArrayList<String> paymentMethods;

    public PaymentMethodDialog(Context context, ArrayList<String> paymentMethods) {
        super(context);

        this.context = context;
        this.paymentMethods = paymentMethods;
    }


    protected PaymentMethodDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.payment_method_dialog);

        paymentMethodList = (ListView) findViewById(R.id.payment_method_list);


        PaymentMethodListAdapter adapter = new PaymentMethodListAdapter(context, R.layout.row_payment_method, paymentMethods);

        paymentMethodList.setAdapter(adapter);

    }

    public ListView getListView() {
        return this.paymentMethodList;
    }
}
