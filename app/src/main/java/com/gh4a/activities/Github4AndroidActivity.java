package com.gh4a.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.gh4a.utils.ActivityResultHelpers;
import com.google.android.material.appbar.AppBarLayout;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.gh4a.BaseActivity;
import com.gh4a.BuildConfig;
import com.gh4a.Gh4Application;
import com.gh4a.R;
import com.gh4a.ServiceFactory;
import com.gh4a.fragment.LoginModeChooserFragment;
import com.gh4a.utils.ApiHelpers;
import com.gh4a.utils.RxUtils;
import com.meisolsson.githubsdk.core.ServiceGenerator;
import com.meisolsson.githubsdk.model.User;
import com.meisolsson.githubsdk.model.request.RequestToken;
import com.meisolsson.githubsdk.service.OAuthService;
import com.meisolsson.githubsdk.service.users.UserService;

import io.reactivex.Single;

/**
 * The Github4Android activity.
 */
public class Github4AndroidActivity extends BaseActivity implements
        View.OnClickListener, LoginModeChooserFragment.ParentCallback {
    public static final int REQUEST_LOGIN_WEBVIEW = 1001;

    private static final Uri CALLBACK_URI = Uri.parse("gh4a://oauth");
    private static final String PARAM_CODE = "code";

    private View mContent;
    private View mProgress;

    private final ActivityResultLauncher<Void> mSettingsLauncher = registerForActivityResult(
            new ActivityResultHelpers.StartSettingsContract(),
            themeChange -> {
                if (themeChange) {
                    Intent intent = new Intent(getIntent());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Gh4Application app = Gh4Application.get();
        if (app.isAuthorized()) {
            if (!handleIntent(getIntent())) {
                goToToplevelActivity();
            }
            finish();
        } else {
            setContentView(R.layout.main);

            AppBarLayout abl = findViewById(R.id.header);
            abl.setEnabled(false);

            FrameLayout contentContainer = (FrameLayout) findViewById(R.id.content).getParent();
            contentContainer.setForeground(null);

            findViewById(R.id.login_button).setOnClickListener(this);
            mContent = findViewById(R.id.welcome_container);
            mProgress = findViewById(R.id.login_progress_container);

            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (!handleIntent(intent)) {
            super.onNewIntent(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN_WEBVIEW) {
            if (resultCode == RESULT_OK && data != null) {
                String code = data.getStringExtra(LoginWebViewActivity.EXTRA_AUTH_CODE);
                if (code != null) {
                    handleAuthCode(code, this);
                    return;
                }
            }
            onLoginCanceled();
        }
    }

    private boolean handleIntent(Intent intent) {
        Uri data = intent.getData();
        if (data != null
                && data.getScheme().equals(CALLBACK_URI.getScheme())
                && data.getHost().equals(CALLBACK_URI.getHost())) {
            final String code = data.getQueryParameter(PARAM_CODE);
            if (code == null) {
                onLoginCanceled();
                return true;
            }
            handleAuthCode(code, this);
            return true;
        }

        return false;
    }

    public static void handleAuthCode(String code, LoginModeChooserFragment.ParentCallback callback) {
        OAuthService service = ServiceGenerator.createAuthService();
        RequestToken request = RequestToken.builder()
                .clientId(BuildConfig.CLIENT_ID)
                .clientSecret(BuildConfig.CLIENT_SECRET)
                .code(code)
                .build();

        service.getToken(request)
                .map(ApiHelpers::throwOnFailure)
                .flatMap(token -> {
                    UserService userService = ServiceFactory.get(UserService.class, true,
                            null, token.accessToken(), null);
                    Single<User> userSingle = userService.getUser()
                            .map(ApiHelpers::throwOnFailure);
                    return Single.zip(Single.just(token), userSingle,
                            (t, user) -> Pair.create(t.accessToken(), user));
                })
                .compose(RxUtils::doInBackground)
                .subscribe(pair -> callback.onLoginFinished(pair.first, pair.second),
                        callback::onLoginFailed);
    }

    @Override
    protected int getLeftNavigationDrawerMenuResource() {
        return R.menu.home_nav_drawer;
    }

    @IdRes
    protected int getInitialLeftDrawerSelection(Menu menu) {
        menu.setGroupCheckable(R.id.navigation, false, false);
        menu.setGroupCheckable(R.id.explore, false, false);
        menu.setGroupVisible(R.id.my_items, false);
        return super.getInitialLeftDrawerSelection(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        super.onNavigationItemSelected(item);
        switch (item.getItemId()) {
            case R.id.settings:
                mSettingsLauncher.launch(null);
                return true;
            case R.id.search:
                startActivity(SearchActivity.makeIntent(this));
                return true;
            case R.id.bookmarks:
                startActivity(new Intent(this, BookmarkListActivity.class));
                return true;
            case R.id.pub_timeline:
                startActivity(new Intent(this, TimelineActivity.class));
                return true;
            case R.id.blog:
                startActivity(new Intent(this, BlogListActivity.class));
                return true;
            case R.id.trend:
                startActivity(new Intent(this, TrendingActivity.class));
                return true;
        }
        return false;
    }

    @Override
    protected boolean canSwipeToRefresh() {
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button) {
            LoginModeChooserFragment.newInstance().show(getSupportFragmentManager(), "login");
            setProgressShown(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (mProgress.getVisibility() == View.VISIBLE) {
            setProgressShown(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLoginStartOauth() {
        launchOauthLogin(this);
    }

    @Override
    public void onLoginFinished(String token, User user) {
        Gh4Application.get().addAccount(user, token);
        goToToplevelActivity();
        finish();
    }

    @Override
    public void onLoginFailed(Throwable error) {
        handleLoadFailure(error);
        setProgressShown(false);
    }

    @Override
    public void onLoginCanceled() {
        setProgressShown(false);
    }

    private void setProgressShown(boolean show) {
        mContent.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public static void launchOauthLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginWebViewActivity.class);
        activity.startActivityForResult(intent, REQUEST_LOGIN_WEBVIEW);
    }
}
