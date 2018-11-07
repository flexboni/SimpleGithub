package com.android.gubonny.simplegithub.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.gubonny.simplegithub.data.AuthTokenProvider;
import com.android.gubonny.simplegithub.ui.search.SearchActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 다른 클래스에서 정의한 REST API 를 실제로 호출할 수 있게 함.
 * <p>
 * <Retrofit 을 통해 REST API 를 호출하기 위해 필요한 요소>
 * 호스트 서버 주소
 * 네트워크 통신에 사용할 클라이언트 구현
 * REST API 응답을 변환할 컨버터
 * REST API 가 정의된 인터페이스
 * </Retrofit>
 * <p>
 * <p>
 * <참고>
 * * Retrofit
 * - 홈페이지 : http://square.github.io/retrofit/
 * - 컨버터 : https://github.com/square/retrofit/wiki/Converters
 * <p>
 * * OkHttp
 * - 홈페이지 : http://square.github.io/okhttp/
 * - Interceptors : https://github.com/square/okhttp/wiki/Interceptors
 * </참고>
 */
public class GithubApiProvider {

    // Auth API 사용을 위한 호스트 서버 주소
    // 액세스 토큰 획득을 위한 객체를 생성
    public static AuthApi provideAuthApi() {

        return new Retrofit.Builder()
                .baseUrl("https://github.com/")
                .client(provideOkHttpClient(provideLoggingInterceptor(), null))
                // GsonConverterFactory 사용해 JSON 형태 REST API 응답을 객체 형태로 변환
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthApi.class);
    }

    // Github API 사용을 위한 호스트 서버 주소
    // 저장소 정보에 접근하기 위한 객체를 생성
    public static GithubApi provideGithubApi(@NonNull Context context) {

        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(provideOkHttpClient(provideLoggingInterceptor(),
                        provideAuthInterceptor(provideAuthTokenProvider(context))))
                // GsonConverterFactory 사용해 JSON 형태 REST API 응답을 객체 형태로 변환
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubApi.class);
    }

    // 네트워크 통신용 클라이언트
    // 통신에 사용할 클라이언트 객체 생성
    private static OkHttpClient provideOkHttpClient(
            @NonNull HttpLoggingInterceptor interceptor,
            @Nullable AuthInterceptor authInterceptor) {

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        if (null != authInterceptor) {
            // 매 요청의 헤더에 액세스 토큰 정보를 추가함.
            b.addInterceptor(authInterceptor);
        }
        // 이 클라이언트를 통해 오고 가는 네트워크 요청/응답을 로그로 표시하도록 함.
        b.addInterceptor(interceptor);

        return b.build();
    }

    // 네트워크 요청/응답을 로그에 표시하는 Interceptor 객체 생성
    private static HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    // 액세스 토큰을 헤더에 추가하는 Interceptor 객체 생성
    private static AuthInterceptor provideAuthInterceptor(@NonNull AuthTokenProvider provider) {
        String token = provider.getToken();
        if (null == token) {
            throw new IllegalStateException("authToken cannot be null.");
        }
        return new AuthInterceptor(token);
    }

    private static AuthTokenProvider provideAuthTokenProvider(@NonNull Context context) {
        return new AuthTokenProvider(context.getApplicationContext());
    }

    static class AuthInterceptor implements Interceptor {

        private final String token;

        AuthInterceptor(String token) {
            this.token = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            // 요청의 헤더에 액세스 토믄 정보를 추가함.
            Request.Builder b = original.newBuilder()
                    .addHeader("Authorization", "token " + token);

            Request request = b.build();
            return chain.proceed(request);
        }
    }
}
