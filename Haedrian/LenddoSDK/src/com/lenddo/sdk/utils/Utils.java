package com.lenddo.sdk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lenddo.sdk.R;
import com.lenddo.sdk.core.LenddoClient;
import com.lenddo.sdk.core.LenddoConstants;
import com.lenddo.sdk.core.LenddoEventListener;
import com.lenddo.sdk.core.formbuilder.LenddoConfig;
import com.lenddo.sdk.dialogs.AuthorizeCallbackCollector;
import com.lenddo.sdk.dialogs.WebAuthorizeFragment;
import com.lenddo.sdk.listeners.RawEventListener;
import com.lenddo.sdk.models.AuthorizationStatus;
import com.lenddo.sdk.models.FormDataCollector;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by joseph on 8/8/14.
 */
public class Utils {

    private static final String TAG = Utils.class.getName();


    public static final String getSignString(String method, String md5Body, String date, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(method);
        sb.append("\n");
        sb.append(md5Body);
        sb.append("\n");
        sb.append(date);
        sb.append("\n");
        sb.append(path);
        return sb.toString();
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final String hmacSha1(String key, String baseString) {
        try {

            byte[] dataBytes = baseString.getBytes("UTF-8");
            byte[] secretBytes = key.getBytes("UTF-8");

            SecretKeySpec signingKey = new SecretKeySpec(secretBytes, "HmacSHA1");

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] signature = mac.doFinal(dataBytes);

            return base64encode(signature);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static final String base64encode(byte[] data) {
        return Base64.encodeBytes(data);
    }

    public static final byte[] base64decode(String data) {
        try {
            return Base64.decode(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> createQueryString(HashMap<String, String> params) {
        ArrayList<String> urlparams = new ArrayList<String>();
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value != null) {
                urlparams.add(key + "=" + URLEncoder.encode(value));
                Log.v(TAG, key + " = " + value);
            }
        }
        return urlparams;
    }

    public static void showAuthorizeDialog(final Activity context, final String postdata,
                                           final RawEventListener eventListener) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View popupLayout = context.getLayoutInflater().inflate(R.layout.web_authorize, null);
                final WebView webViewPopup = (WebView) popupLayout.findViewById(R.id.webView);

                WebSettings settings = webViewPopup.getSettings();
                settings.setJavaScriptEnabled(true);

                Log.d(TAG, "postdata = " + postdata);

                FragmentTransaction transaction = context.getFragmentManager().beginTransaction();
                // For a little polish, specify a transition animation
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                // To make it fullscreen, use the 'content' root view as the container
                // for the fragment, which is always the root view for the activity
                WebAuthorizeFragment webAuthorizeFragment = new WebAuthorizeFragment();
                webAuthorizeFragment.setUrl(LenddoConfig.getAuthorizeSiteUrl(), postdata);
                webAuthorizeFragment.setAuthorizeCallbackCollector(new AuthorizeCallbackCollector() {
                    @Override
                    public void onCallbackInitiated(String url) {
                        if (url.startsWith("lenddo://success")) {
                            Uri uri = Uri.parse(url);
                            String verification = uri.getQueryParameter("verification");
                            String status = uri.getQueryParameter("status");
                            String transid = uri.getQueryParameter("transid");
                            String userid = uri.getQueryParameter("userid");
                            String psid = uri.getQueryParameter("psid");

                            AuthorizationStatus authorizationStatus = new AuthorizationStatus();
                            authorizationStatus.setVerification(verification);
                            authorizationStatus.setStatus(status);
                            authorizationStatus.setTransId(transid);
                            authorizationStatus.setVerification(transid);
                            authorizationStatus.setUserId(userid);
                            authorizationStatus.setPsid(psid);

                            eventListener.onAuthorizeComplete(url);

                        } else if (url.startsWith("lenddo://cancel")) {
                            eventListener.onAuthorizeCanceled();
                        }
                    }

                    @Override
                    public void onCancel() {
                        eventListener.onAuthorizeCanceled();
                    }
                });
//        webAuthorizeFragment.show(context.getFragmentManager(), "authorizeDialog");

                transaction.add(android.R.id.content, webAuthorizeFragment, "authorizeLenddoDialog")
                        .commit();
            }
        });


    }

    public static final int REQUEST_AUTHORIZE = 1234;

    public static void chooseAuthorize(Activity context, JSONObject postData,
                                       String partnerId, String clientId, RawEventListener listener) {
        try {
            String partnerData = postData.getString("p_d");
            String verificationData = postData.getString("v_d");
            Log.d(TAG, "partnerData = " + partnerData);
            Log.d(TAG, "verificationData = " + verificationData);
            if (Utils.isLenddoInstalled(context)) {
                Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("lenddo://authorize"));
                intent.putExtra("partnerScriptId", partnerId);
                intent.putExtra("userId", URLEncoder.encode(clientId, "utf-8"));
                intent.putExtra("formData", partnerData);
                intent.putExtra("verificationData",verificationData);
                intent.putExtra("packageName", context.getApplicationContext().getPackageName());
                ((Activity) context).startActivityForResult(intent, REQUEST_AUTHORIZE);
            } else {
                ArrayList<String> postDataArr = new ArrayList<String>();
                try {
                    Log.d(TAG, "no lenddo app detected, using webview");
                    postDataArr.add("ps_id=" + partnerId);
                    postDataArr.add("c_id=" + URLEncoder.encode(clientId, "utf-8"));
                    postDataArr.add("p_d=" + URLEncoder.encode(partnerData, "utf-8"));
                    postDataArr.add("v_d=" + URLEncoder.encode(verificationData, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String postdata = StringUtils.join(postDataArr, "&");
                Utils.showAuthorizeDialog(context, postdata, listener);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void showAuthorizeDialog(Activity context, final FormDataCollector collector,
                                           final LenddoEventListener eventListener) {
        View popupLayout = context.getLayoutInflater().inflate(R.layout.web_authorize, null);
        final WebView webViewPopup = (WebView) popupLayout.findViewById(R.id.webView);
        WebSettings settings = webViewPopup.getSettings();
        settings.setJavaScriptEnabled(true);

        ArrayList<String> postDataArr = new ArrayList<String>();
        String partnerData = collector.toJson();

        String verificationData =collector.toVerificationJson();
        Log.d(TAG, "partnerData = " + partnerData);
        Log.d(TAG, "verificationData = " + verificationData);
        try {
            postDataArr.add("ps_id=" + collector.getPartnerScriptId());
            postDataArr.add("c_id=" + URLEncoder.encode(collector.getUserId(), "utf-8"));
            postDataArr.add("p_d=" + URLEncoder.encode(partnerData, "utf-8"));
            postDataArr.add("v_d=" + URLEncoder.encode(verificationData, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String postdata = StringUtils.join(postDataArr, "&");

        Log.d(TAG, "postdata = " + postdata);

        FragmentTransaction transaction = context.getFragmentManager().beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        WebAuthorizeFragment webAuthorizeFragment = new WebAuthorizeFragment();
        webAuthorizeFragment.setUrl(LenddoConfig.getAuthorizeSiteUrl(), postdata);
        webAuthorizeFragment.setAuthorizeCallbackCollector(new AuthorizeCallbackCollector() {
            @Override
            public void onCallbackInitiated(String url) {
                Log.d(TAG, "callblack initiated " + url);
                if (url.startsWith("lenddo://success")) {
                    Uri uri = Uri.parse(url);
                    String verification = uri.getQueryParameter("verification");
                    String status = uri.getQueryParameter("status");
                    String transid = uri.getQueryParameter("transid");
                    String userid = uri.getQueryParameter("userid");
                    String psid = uri.getQueryParameter("psid");

                    AuthorizationStatus authorizationStatus = new AuthorizationStatus();
                    authorizationStatus.setVerification(verification);
                    authorizationStatus.setStatus(status);
                    authorizationStatus.setTransId(transid);
                    authorizationStatus.setVerification(transid);
                    authorizationStatus.setUserId(userid);
                    authorizationStatus.setPsid(psid);

                    collector.setAuthorizationStatus(authorizationStatus);
                    eventListener.onAuthorizeComplete(collector);

                } else if (url.startsWith("lenddo://cancel")) {
                    eventListener.onAuthorizeCanceled(collector);
                }
            }

            @Override
            public void onCancel() {
                eventListener.onAuthorizeCanceled(collector);
            }
        });
//        webAuthorizeFragment.show(context.getFragmentManager(), "authorizeDialog");

        transaction.add(android.R.id.content, webAuthorizeFragment, "authorizeLenddoDialog")
                .commit();

    }

    public static Map<String, Integer> getInstalledApps(Context context) {
        Log.d(TAG, "get installed apps");
        HashMap<String, Integer> installedApps = new HashMap<String, Integer>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            installedApps.put(packageInfo.packageName, packageInfo.versionCode);
        }
        Log.d(TAG, "done.");
        return installedApps;
    }

    public static boolean isLenddoInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.lenddo")) {
                if (packageInfo.versionCode > 12) {
                    return true;
                }
            }
        }
        return false;
    }

}
