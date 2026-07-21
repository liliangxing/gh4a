package com.gh4a.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gh4a.BuildConfig;

public class LoginWebViewActivity extends Activity {
    private static final String OAUTH_URL = "https://github.com/login/oauth/authorize";
    private static final String CALLBACK_SCHEME = "gh4a";
    private static final String SCOPES = "user,repo,gist,read:org,notifications";

    public static final String EXTRA_AUTH_CODE = "auth_code";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebView = new WebView(this);
        setContentView(mWebView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (interceptCallback(url)) {
                    view.stopLoading();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (interceptCallback(url)) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (interceptCallback(url)) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        Uri uri = Uri.parse(OAUTH_URL)
                .buildUpon()
                .appendQueryParameter("client_id", BuildConfig.CLIENT_ID)
                .appendQueryParameter("scope", SCOPES)
                .appendQueryParameter("redirect_uri", CALLBACK_SCHEME + "://oauth")
                .build();
        mWebView.loadUrl(uri.toString());
    }

    private boolean interceptCallback(String url) {
        Uri parsed = Uri.parse(url);
        if (CALLBACK_SCHEME.equals(parsed.getScheme())) {
            String code = parsed.getQueryParameter("code");
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_AUTH_CODE, code);
            setResult(RESULT_OK, resultIntent);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
