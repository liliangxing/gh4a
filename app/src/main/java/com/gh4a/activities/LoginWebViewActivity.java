package com.gh4a.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gh4a.BuildConfig;

public class LoginWebViewActivity extends Activity {
    private static final String OAUTH_URL = "https://github.com/login/oauth/authorize";
    private static final String CALLBACK_URI = "gh4a://oauth";
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
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                if ("gh4a".equals(url.getScheme()) && "oauth".equals(url.getHost())) {
                    String code = url.getQueryParameter("code");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_AUTH_CODE, code);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        Uri uri = Uri.parse(OAUTH_URL)
                .buildUpon()
                .appendQueryParameter("client_id", BuildConfig.CLIENT_ID)
                .appendQueryParameter("scope", SCOPES)
                .appendQueryParameter("redirect_uri", CALLBACK_URI)
                .build();
        mWebView.loadUrl(uri.toString());
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
