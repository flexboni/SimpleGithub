package com.android.gubonny.simplegithub.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast

import com.android.gubonny.simplegithub.BuildConfig
import com.android.gubonny.simplegithub.R
import com.android.gubonny.simplegithub.api.AuthApi
import com.android.gubonny.simplegithub.api.model.GithubAccessToken
import com.android.gubonny.simplegithub.api.GithubApiProvider
import com.android.gubonny.simplegithub.data.AuthTokenProvider
import com.android.gubonny.simplegithub.ui.main.MainActivity

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 코틀린 안드로이드 익스텐션에서 activity_sign_in 레이아웃을 사용
//import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {
    internal lateinit var btnStart: Button

    internal lateinit var progress: ProgressBar

    internal lateinit var api: AuthApi

    internal lateinit var authTokenProvider: AuthTokenProvider

    internal lateinit var accessTokenCall: Call<GithubAccessToken>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // 인스턴스 선언 없이 뷰 ID 를 사용하여 인스턴스에 접근함.
        // View.OnClickListener 의 본체를 람다 표현식으로 작성함
        btnStart.setOnClickListener {
            // 사용자 인증을 처리하는 URL 을 구성합니다.
            // 형식 : https://github.com/login/oauth/
            //        authorize?client_id={애플리케이션의 Client ID}
            val authUri = Uri.Builder()
                    .scheme("https")
                    .authority("github.com")
                    .appendPath("login")
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                    .build()

            // 크롬 커스텀 탭으로 웹 페이지를 표시합니다.
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this@SignInActivity, authUri)
        }

        api = GithubApiProvider.provideAuthApi()
        authTokenProvider = AuthTokenProvider(this)

        // 저장된 액세스 토큰이 있다면 메인 액티비티로 이동합니다.
        if (null != authTokenProvider.token) {
            launchMainActivity()
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        showProgress()

        // 엘비스 연산자를 사용하여 널 값 검사
        // intent.data 가 null 이면 IllegalArgumentException 예외를 발생
        val uri = intent.data ?: throw IllegalArgumentException("No data exists")

        // 엘비스 연산자를 사용하여 널 값 검사
        // uri.getQueryParameter("code") 가 null 이면
        // IllegalStateException 예외를 발생
        val code = uri.getQueryParameter("code") ?: throw IllegalStateException("No code exists")

        getAccessToken(code)
    }

    private fun getAccessToken(code: String) {
        showProgress()

        // 액세스 토큰을 요청하는 REST API
        accessTokenCall = api.getAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)

        // 비동기 방식으로 액세스 토큰을 요청합니다.
        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스 생성
        accessTokenCall.enqueue(object : Callback<GithubAccessToken> {
            override fun onResponse(call: Call<GithubAccessToken>, response: Response<GithubAccessToken>) {
                hideProgress()

                val token = response.body()
                if (response.isSuccessful && null != token) {

                    // 발급받은 액세스 토큰을 저장합니다.
                    authTokenProvider.updateToken(token.accessToken)

                    // 메인 액티비티로 이동합니다.
                    launchMainActivity()

                } else {
                    showError(IllegalStateException(
                            "Not successful: " + response.message()))
                }
            }

            override fun onFailure(call: Call<GithubAccessToken>, t: Throwable) {
                hideProgress()
                showError(t)
            }
        })
    }

    private fun showProgress() {
        // 인스턴스 선언 없이 뷰 ID 를 사용하여 인스턴스에 접근함.
        btnStart.visibility = View.GONE
        progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        // 인스턴스 선언 없이 뷰 ID 를 사용하여 인스턴스에 접근함.
        btnStart.visibility = View.VISIBLE
        progress.visibility = View.GONE
    }

    private fun showError(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
    }

    private fun launchMainActivity() {
        startActivity(Intent(
                this@SignInActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
