package com.gh4a.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 内置 WebView 加载 GitHub OAuth 授权页。
 *
 * 关键修复：WebView 对 gh4a:// 这种自定义 scheme 不会触发 shouldOverrideUrlLoading，
 * 所以我们需要在 onPageStarted 里手动检查 URL，并且对非 http/https URL 做 fallback 处理。
 */
public class LoginWebViewActivity extends Activity {

    public static final String EXTRA_OAUTH_URL = "oauth_url";
    public static final String EXTRA_RESULT_CODE = "result_code";

    private static final String OAUTH_SCHEME = "gh4a";
    private static final String OAUTH_HOST = "oauth";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String oauthUrl = getIntent().getStringExtra(EXTRA_OAUTH_URL);
        if (oauthUrl == null) {
            finish();
            return;
        }

        mWebView = new WebView(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        // 允许 JS 执行重定向
        mWebView.getSettings().setSupportMultipleWindows(false);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme();
                // 拦截 gh4a://oauth 回调
                if (OAUTH_SCHEME.equals(scheme) && OAUTH_HOST.equals(uri.getHost())) {
                    handleCallback(uri);
                    return true;
                }
                // 对其他非 http/https scheme（如 intent://），交给系统处理
                if (scheme != null && !scheme.equals("http") && !scheme.equals("https")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                // WebView 对自定义 scheme 可能不触发 shouldOverrideUrlLoading，
                // 但 onPageStarted 会被调用（即使 URL 是 gh4a://）
                Uri uri = Uri.parse(url);
                if (OAUTH_SCHEME.equals(uri.getScheme()) && OAUTH_HOST.equals(uri.getHost())) {
                    handleCallback(uri);
                }
            }
        });

        setContentView(mWebView);
        mWebView.loadUrl(oauthUrl);
    }

    private void handleCallback(Uri uri) {
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

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }
}
