package com.android.gubonny.simplegithub.ui.signin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.gubonny.simplegithub.BuildConfig;
import com.android.gubonny.simplegithub.R;
import com.android.gubonny.simplegithub.api.AuthApi;
import com.android.gubonny.simplegithub.api.model.GithubAccessToken;
import com.android.gubonny.simplegithub.api.GithubApiProvider;
import com.android.gubonny.simplegithub.data.AuthTokenProvider;
import com.android.gubonny.simplegithub.ui.main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    Button btnStart;

    ProgressBar progress;

    AuthApi api;

    AuthTokenProvider authTokenProvider;

    Call<GithubAccessToken> accessTokenCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnStart = findViewById(R.id.btnActivitySignInStart);
        progress = findViewById(R.id.pbActivitySignIn);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 사용자 인증을 처리하는 URL 을 구성합니다.
                // 형식 : https://github.com/login/oauth/
                //        authorize?client_id={애플리케이션의 Client ID}
                Uri authUri = new Uri.Builder()
                        .scheme("https")
                        .authority("github.com")
                        .appendPath("login")
                        .appendPath("oauth")
                        .appendPath("authorize")
                        .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                        .build();

                // 크롬 커스텀 탭으로 웹 페이지를 표시합니다.
                CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                intent.launchUrl(SignInActivity.this, authUri);
            }
        });

        api = GithubApiProvider.provideAuthApi();
        authTokenProvider = new AuthTokenProvider(this);

        // 저장된 액세스 토큰이 있다면 메인 액티비티로 이동합니다.
        if (null != authTokenProvider.getToken()) {
            launchMainActivity();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        showProgress();

        Uri uri = intent.getData();
        if (null == uri) {
            throw new IllegalArgumentException("No data exists");
        }

        String code = uri.getQueryParameter("code");
        if (null == code) {
            throw new IllegalArgumentException("No code exists");
        }

        getAccessToken(code);
    }

    private void getAccessToken(@NonNull String code) {
        showProgress();

        // 액세스 토큰을 요청하는 REST API
        accessTokenCall = api.getAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code);

        // 비동기 방식으로 액세스 토큰을 요청합니다.
        accessTokenCall.enqueue(new Callback<GithubAccessToken>() {
            @Override
            public void onResponse(Call<GithubAccessToken> call, Response<GithubAccessToken> response) {
                hideProgress();

                GithubAccessToken token = response.body();
                if (response.isSuccessful() && null != token) {

                    // 발급받은 액세스 토큰을 저장합니다.
                    authTokenProvider.updateToken(token.accessToken);

                    // 메인 액티비티로 이동합니다.
                    launchMainActivity();

                } else {
                    showError(new IllegalStateException(
                            "Not successful: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<GithubAccessToken> call, Throwable t) {
                hideProgress();
                showError(t);
            }
        });
    }

    private void showProgress() {
        btnStart.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        btnStart.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void showError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void launchMainActivity() {
        startActivity(new Intent(
                SignInActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
