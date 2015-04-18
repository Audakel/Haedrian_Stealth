package lenddo.com.lenddoconnect;

import android.app.Application;

import com.lenddo.sdk.core.Credentials;
import com.lenddo.sdk.core.LenddoClient;
import com.lenddo.sdk.core.formbuilder.LenddoConfig;

public class SimpleLoan extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LenddoConfig.setTestMode(true);
    }
}
