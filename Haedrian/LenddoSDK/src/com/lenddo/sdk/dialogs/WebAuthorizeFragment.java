package com.lenddo.sdk.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.lenddo.sdk.R;
import com.lenddo.sdk.core.LenddoConstants;

import org.apache.http.util.EncodingUtils;

import java.sql.Wrapper;

/**
 * Created by joseph on 11/26/14.
 */
public class WebAuthorizeFragment extends DialogFragment {

    private static final String TAG = WebAuthorizeFragment.class.getName();
    private WebView webViewPopup;
    private AuthorizeCallbackCollector authorizeCallbackCollector;
    private String postData;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url, String postdata) {
        if (webViewPopup!=null) {
            webViewPopup.postUrl(url,
                    EncodingUtils.getBytes(postdata, "utf-8"));
        }
        this.url = url;
        this.postData =  postdata;
    }

    String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.web_authorize, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText keyboardHack = new EditText(getActivity());
        keyboardHack.setVisibility(View.GONE);

        ViewGroup wrapper = (ViewGroup)view.findViewById(R.id.wrapper);
        wrapper.addView(keyboardHack);

        webViewPopup = (WebView) view.findViewById(R.id.webView);
        final ProgressBar progress = (ProgressBar) view.findViewById(R.id.progressBar);
        webViewPopup.postUrl(url,
                EncodingUtils.getBytes(postData, "utf-8"));
        WebSettings settings = webViewPopup.getSettings();
        settings.setUserAgentString(settings.getUserAgentString() + " LenddoWrapper/1.0");
        webViewPopup.requestFocus(View.FOCUS_DOWN);
        webViewPopup.setFocusableInTouchMode(true);
        webViewPopup.setFocusable(true);
        webViewPopup.setHapticFeedbackEnabled(true);
        webViewPopup.setClickable(true);
        webViewPopup.getSettings().setUseWideViewPort(true);
        webViewPopup.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus())
                        {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
        settings.setJavaScriptEnabled(true);
        webViewPopup.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(WebAuthorizeFragment.this);
                ft.commit();
            }
        });

        webViewPopup.setWebViewClient(new WebViewClient() {
            @Override

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG,"load url = " + url);
                if (url.startsWith("lenddo://")) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.remove(WebAuthorizeFragment.this);
                    ft.commit();
                    if (authorizeCallbackCollector!=null) {
                        authorizeCallbackCollector.onCallbackInitiated(url);
                    }
                } else
                {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setProgress(100);
                progress.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress.setProgress(0);
                progress.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        authorizeCallbackCollector.onCancel();
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.LenddoDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {

                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                            Log.d(TAG,"onKey webview current url = " + webViewPopup.getUrl());
                            if (webViewPopup.canGoBack()) {
                                webViewPopup.goBack();
                            } else {
                                webViewPopup.clearCache(true);
                                webViewPopup.clearHistory();
                                WebAuthorizeFragment.this.dismiss();
                            }
                        }
                        return true;
                    }
        });
        return dialog;
    }

    public void setAuthorizeCallbackCollector(AuthorizeCallbackCollector authorizeCallbackCollector) {
        this.authorizeCallbackCollector = authorizeCallbackCollector;
    }

    public AuthorizeCallbackCollector getAuthorizeCallbackCollector() {
        return authorizeCallbackCollector;
    }

    public void onBackPressed() {
        Log.d(TAG,"onBackPressed webview current url = " + webViewPopup.getUrl());
        if (webViewPopup.canGoBack()) {
            webViewPopup.goBack();
        } else {
            webViewPopup.clearCache(true);
            webViewPopup.clearHistory();
            authorizeCallbackCollector.onCancel();
            WebAuthorizeFragment.this.dismiss();
        }
    }
}
