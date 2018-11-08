package com.android.gubonny.simplegithub.api

import android.content.Context

import com.android.gubonny.simplegithub.data.AuthTokenProvider

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 다른 클래스에서 정의한 REST API 를 실제로 호출할 수 있게 함.
 *
 *
 * <Retrofit 을 통해 REST API 를 호출하기 위해 필요한 요소>
 * 호스트 서버 주소
 * 네트워크 통신에 사용할 클라이언트 구현
 * REST API 응답을 변환할 컨버터
 * REST API 가 정의된 인터페이스
</Retrofit> *
 *
 *
 *
 *
 * <참고>
 * * Retrofit
 * - 홈페이지 : http://square.github.io/retrofit/
 * - 컨버터 : https://github.com/square/retrofit/wiki/Converters
</참고> *
 *
 * * OkHttp
 * - 홈페이지 : http://square.github.io/okhttp/
 * - Interceptors : https://github.com/square/okhttp/wiki/Interceptors
 *
 */

// 싱글톤으로 변환
// 싱글콘 클래스를 제거하고 패키지 단위 함수로 다시 선언 함.
//object GithubApiProvider {

// Auth API 사용을 위한 호스트 서버 주소
// 액세스 토큰 획득을 위한 객체를 생성
// 함수 내부에서 변수나 값을 선언하거나 연산을 수행하는 부분 없이,
// 생성된 객체를 반환하는 코드로만 구성됨.
// -> 단일 표현식(single expression) 형태로 표시함.
fun provideAuthApi(): AuthApi = Retrofit.Builder()
        .baseUrl("https://github.com/")
        .client(provideOkHttpClient(provideLoggingInterceptor(), null))
        // 받은 응답을 옵서버블 형태로 변환 함.
        // 비동기 방식으로 API 호출.
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        // GsonConverterFactory 사용해 JSON 형태 REST API 응답을 객체 형태로 변환
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApi::class.java)

// Github API 사용을 위한 호스트 서버 주소
// 저장소 정보에 접근하기 위한 객체를 생성
// 함수 내부에서 변수나 값을 선언하거나 연산을 수행하는 부분 없이,
// 생성된 객체를 반환하는 코드로만 구성됨.
// -> 단일 표현식(single expression) 형태로 표시함.
fun provideGithubApi(context: Context): GithubApi = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(provideOkHttpClient(provideLoggingInterceptor(),
                provideAuthInterceptor(provideAuthTokenProvider(context))))
        // 받은 응답을 옵서버블 형태로 변환 함.
        // 비동기 방식으로 API 호출.
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        // GsonConverterFactory 사용해 JSON 형태 REST API 응답을 객체 형태로 변환
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GithubApi::class.java)

// 네트워크 통신용 클라이언트
// 통신에 사용할 클라이언트 객체 생성
// apply()나 run()과 같은 범위 지정 함수를 사용하면
// 함수 내부의 변수선언을 완전히 제거할 수 있으며,
// 이로인해 단일 표현식 형태로 표현 가능 함.
private fun provideOkHttpClient(
        interceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor?): OkHttpClient = OkHttpClient.Builder()

        // run() 함수로 OkHttpClient.Builder 변수 선언을 제거함.
        .run {
            if (null != authInterceptor) {
                // 매 요청의 헤더에 액세스 토큰 정보를 추가함.
                addInterceptor(authInterceptor)
            }
// 이 클라이언트를 통해 오고 가는 네트워크 요청/응답을 로그로 표시하도록 함.
            addInterceptor(interceptor)
            build()
        }


// 네트워크 요청/응답을 로그에 표시하는 Interceptor 객체 생성
private fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
        // apply() 함수로 인스턴스 생성과 프로퍼티 값 변경을 동시에 수행 함.
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

// 액세스 토큰을 헤더에 추가하는 Interceptor 객체 생성
private fun provideAuthInterceptor(provider: AuthTokenProvider): AuthInterceptor {
    val token = provider.token ?: throw IllegalStateException("authToken cannot be null.")
    return AuthInterceptor(token)
}

private fun provideAuthTokenProvider(context: Context): AuthTokenProvider = AuthTokenProvider(context.applicationContext)

internal class AuthInterceptor(private val token: String) : Interceptor {

    // with() 함수와 run() 함수로 추가 변수 선언을 제거 함.
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val newRequest = request().newBuilder().run {
            addHeader("Authorization", "token $token")
            build()
        }
        proceed(newRequest)
    }
}
//}
