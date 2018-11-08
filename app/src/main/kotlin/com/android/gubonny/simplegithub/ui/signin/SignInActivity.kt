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
import com.android.gubonny.simplegithub.api.provideAuthApi
//import com.android.gubonny.simplegithub.api.GithubApiProvider
import com.android.gubonny.simplegithub.data.AuthTokenProvider
import com.android.gubonny.simplegithub.extensions.plusAssign
import com.android.gubonny.simplegithub.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 코틀린 안드로이드 익스텐션에서 activity_sign_in 레이아웃을 사용
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import org.jetbrains.anko.newTask


class SignInActivity : AppCompatActivity() {
    internal lateinit var btnStart: Button

    internal lateinit var progress: ProgressBar

    internal lateinit var api: AuthApi

    internal lateinit var authTokenProvider: AuthTokenProvider

    //    //    internal lateinit var accessTokenCall: Call<GithubAccessToken>
//    // lateinit 을 선언한 경우 컴파일 시점에서
//    // 해당 객체가 null 값인지 확인 할 수 없어
//    // 정말 필요할 때만 사용하는 것이 좋고,
//    // 지금 경우에는 명시적으로 null 선언을 해주는게 좋다.
//    internal var accessTokenCall: Call<GithubAccessToken>? = null
    // 여러 disposable 객체를 관리할 수 있는 CompositeDisposable 객체를 초기화 함.
    // accessTokenCall 대신 사용.
    internal val disposables = CompositeDisposable()


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

//        api = GithubApiProvider.provideAuthApi()
        // 패키지 단위 함수를 호출.
        api = provideAuthApi()
        authTokenProvider = AuthTokenProvider(this)

        // 저장된 액세스 토큰이 있다면 메인 액티비티로 이동합니다.
        if (null != authTokenProvider.token) {
            launchMainActivity()
        }

    }

    override fun onStop() {
        super.onStop()

//        // 액티비티가 화면에서 사라지는 시점에
//        // API 호출 객체가 생성되어 있다면 API 요청을 취소 함
//        accessTokenCall?.run { cancel() }

        // 관리하고 있던 디스포저블 객체를 모두 해제 함.
        // 네트워크 요청이 있다고 하면 자동 취소 됨.
        disposables.clear()
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
//        showProgress()
//
//        // 액세스 토큰을 요청하는 REST API
//        // 이 줄이 실행될 때 accessTokenCall에 반환 값이 저장 됨.
//        accessTokenCall = api.getAccessToken(
//                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)
//
//        // 비동기 방식으로 액세스 토큰을 요청합니다.
//        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스 생성
//        // 앞에서 API 호출에 필요한 객체를 받았으므로,
//        // 이 시점에서 accessTokenCall 객체의 값은 null 이 아니므로
//        // null 값 보증(!!) 을 사용하여 이 객체를 사용 함.
//        accessTokenCall!!.enqueue(object : Callback<GithubAccessToken> {
//            override fun onResponse(call: Call<GithubAccessToken>, response: Response<GithubAccessToken>) {
//                hideProgress()
//
//                val token = response.body()
//                if (response.isSuccessful && null != token) {
//
//                    // 발급받은 액세스 토큰을 저장합니다.
//                    authTokenProvider.updateToken(token.accessToken)
//
//                    // 메인 액티비티로 이동합니다.
//                    launchMainActivity()
//
//                } else {
//                    showError(IllegalStateException(
//                            "Not successful: " + response.message()))
//                }
//            }
//
//            override fun onFailure(call: Call<GithubAccessToken>, t: Throwable) {
//                hideProgress()
//                showError(t)
//            }
//        })

        // REST API 를 통해 액세스 토큰을 요청 함.
//        disposables.add(api.getAccessToken(
        // '+=' 연산자로 disposable CompositeDisposable 에 추가.
        disposables += api.getAccessToken(
                BuildConfig.GITHUB_CLIENT_ID,
                BuildConfig.GITHUB_CLIENT_SECRET,
                code)

                // REST API 를 통해 받은 응답에서 액세스 토큰만 추출 함.
                .map { it.accessToken }

                // 이 이후에 수행되는 코드는 모두 메인 스레드에서 실행 함.
                // RxAndroid 에서 제공하는 스케줄러인
                // AndroidSchedulers.mainThread() 를 사용 함.
                .observeOn(AndroidSchedulers.mainThread())

                // 구독할 때 수행할 작업을 구현 함.
                .doOnSubscribe { showProgress() }

                // 스트림이 종료될 때 수행할 작업을 구현 함.
                .doOnTerminate { hideProgress() }

                // 옵서버블을 구독 함.
                .subscribe({ token ->
                    // API 를 통해 액세스 토큰을 정상적으로 받았을 때
                    // 처리할 작업을 구현 함.
                    // 작업 중 오류가 발생하면 이 블록은 호출되지 않음.
                    authTokenProvider.updateToken(token)
                    launchMainActivity()
                }) {
                    // 에러 블록
                    // 네트워크 오류나 데이터 처리 오류 등
                    // 작업이 정상적으로 완료되지 않았을 때 호출 됨.
                    showError(it)
                }
//        )
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
//        Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()

        // 긴 시간 동안 표시되는 anko 토스트 메시지 출력
        longToast(throwable.message ?: "No Message available")
    }

    private fun launchMainActivity() {
//        startActivity(Intent(
//                this@SignInActivity, MainActivity::class.java)
//                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

        // intentFor 사용해 전환할 화면 실행과 함께 flag 값 지정.
        startActivity(intentFor<MainActivity>().clearTask().newTask())
    }
}
