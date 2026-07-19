package com.gh4a.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginWebViewActivity extends Activity {

    public static final String EXTRA_OAUTH_URL = "oauth_url";
    public static final String EXTRA_RESULT_CODE = "result_code";

    private static final String OAUTH_SCHEME = "gh4a";
    private static final String OAUTH_HOST = "oauth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String oauthUrl = getIntent().getStringExtra(EXTRA_OAUTH_URL);
        if (oauthUrl == null) {
            finish();
            return;
        }

        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (OAUTH_SCHEME.equals(uri.getScheme()) && OAUTH_HOST.equals(uri.getHost())) {
                    String code = uri.getQueryParameter("code");
                    if (code != null) {
                        Intent result = new Intent();
                        result.putExtra(EXTRA_RESULT_CODE, code);
                        setResult(RESULT_OK, result);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Uri uri = Uri.parse(url);
                if (OAUTH_SCHEME.equals(uri.getScheme()) && OAUTH_HOST.equals(uri.getHost())) {
                    String code = uri.getQueryParameter("code");
                    if (code != null) {
                        Intent result = new Intent();
                        result.putExtra(EXTRA_RESULT_CODE, code);
                        setResult(RESULT_OK, result);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                }
            }
        });

        setContentView(webView);
        webView.loadUrl(oauthUrl);
    }
}
