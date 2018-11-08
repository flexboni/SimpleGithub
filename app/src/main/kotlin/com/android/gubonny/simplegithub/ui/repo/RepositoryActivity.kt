package com.android.gubonny.simplegithub.ui.repo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.android.gubonny.simplegithub.R
//import com.android.gubonny.simplegithub.api.GithubApi
//import com.android.gubonny.simplegithub.api.GithubApiProvider
import com.android.gubonny.simplegithub.api.model.GithubRepo
import com.android.gubonny.simplegithub.api.provideGithubApi
import com.android.gubonny.simplegithub.ui.GlideApp
import kotlinx.android.synthetic.main.activity_repository.*

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryActivity : AppCompatActivity() {

    // 정적 필드로 정의되어 있던 항목은 동반 객체 내부에 정의함.
    // 클래스 내 동반 객체의 정의부를 가장 위로 옮겨 줌.
    companion object {

        // const 키워드 추가.
        const val KEY_USER_LOGIN = "user_login"

        // const 키워드 추가.
        const val KEY_REPO_NAME = "repo_name"
    }

    internal lateinit var llContent: LinearLayout

    internal lateinit var ivProfile: ImageView

    internal lateinit var tvName: TextView

    internal lateinit var tvStars: TextView

    internal lateinit var tvDescription: TextView

    internal lateinit var tvLanguage: TextView

    internal lateinit var tvLastUpdate: TextView

    internal lateinit var pbProgress: ProgressBar

    internal lateinit var tvMessage: TextView

    // lazy 로 전향 함.
    internal val api by lazy { provideGithubApi(this) }

    // null 값을 허용하도록 한 후, 초기값을 명시적으로 null 지정 함.
    internal var repoCall: Call<GithubRepo>? = null

    // REST API 응답에 포함된 날짜 및 시간 표시 형식입니다.
    // 객체 한번 생성하고 나면
    // 이후 변경할 일이 없어 val 로 변경.
    internal val dateFormatInResoponse = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault()
    )

    // 화면에서 사용자에게 보여줄 날짜 및 시간 표시 형식입니다.
    // 객체 한번 생성하고 나면
    // 이후 변경할 일이 없어 val 로 변경.
    internal val dateFormatToShow = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

//        api = GithubApiProvider.provideGithubApi(this)

        // 액티비티 호출 시 전달받은 사용자 이름과 저장소 이름을 추출합니다.
        // 엘비스 연산자를 사용하여 null 값을 검사함.
        // KEY_USER_LOGIN 이름으로 문자열 값 포함되어 있지 않다면
        // IllegalArgumentException 예외를 발생시킴.
        val login = intent.getStringExtra(KEY_USER_LOGIN)
                ?: throw IllegalArgumentException("No login info exists in extras")

        // 엘비스 연산자를 사용하여 null 값을 검사함.
        // KEY_REPO_NAME 이름으로 문자열 값 포함되어 있지 않다면
        // IllegalArgumentException 예외를 발생시킴.
        val repo = intent.getStringExtra(KEY_REPO_NAME)
                ?: throw IllegalArgumentException("No repo info exists in extras")

        showRepositoryInfo(login, repo)

    }

    override fun onStop() {
        super.onStop()
        // 액티비티가 화면에서 사라지는 시점에서
        // API 호출 객체가 생성되어 있다면
        // API 요청을 취소 함.
        repoCall?.run { cancel() }
    }

    private fun showRepositoryInfo(login: String, repoName: String) {
        showProgress()

        repoCall = api.getRepository(login, repoName)

        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스 생성
        // 앞에서 API 호출에 필요한 객체를 받았으므로
        // null 이 아님을 보증해줘야 함.(!!)
        repoCall!!.enqueue(object : Callback<GithubRepo> {
            override fun onResponse(call: Call<GithubRepo>, response: Response<GithubRepo>) {
                hideProgress(true)

                val repo = response.body()
                if (response.isSuccessful && null != repo) {
                    // 저장소 소유자의 프로필 사진을 표시합니다.
                    GlideApp.with(this@RepositoryActivity)
                            .load(repo.owner.avatarUrl)
                            .into(ivProfile)

                    // 저장소 정보를 표시합니다.
                    tvName.text = repo.fullName
                    tvStars.text = resources
                            .getQuantityString(R.plurals.star, repo.stars, repo.stars)

                    if (null == repo.description) {
                        tvDescription.setText(R.string.no_description_provided)

                    } else {
                        tvDescription.setText(R.string.description)
                    }

                    if (null == repo.language) {
                        tvLanguage.setText(R.string.no_language_specified)

                    } else {
                        tvLanguage.setText(R.string.language)
                    }

                    try {
                        // 응답에 포함된 마지막 업데이트 시각을 Date 형식으로 변환합니다.
                        val lastUpdate = dateFormatInResoponse.parse(repo.updatedAt)

                        // 마지막 업데이트 시각을 yyyy-MM-dd HH:mm:ss 형태로 표시합니다.
                        tvLastUpdate.text = dateFormatToShow.format(lastUpdate)

                    } catch (ex: ParseException) {
                        tvLastUpdate.text = getString(R.string.unknown)
                    }

                } else {
                    showError("Not successful: " + response.message())
                }
            }

            override fun onFailure(call: Call<GithubRepo>, t: Throwable) {
                hideProgress(false)
                showError(t.message)
            }
        })
    }

    private fun showProgress() {
        llContent.visibility = View.GONE
        pbProgress.visibility = View.VISIBLE
    }

    private fun hideProgress(isSucceed: Boolean) {
        llContent.visibility = if (isSucceed) View.VISIBLE else View.GONE
        pbProgress.visibility = View.GONE
    }

    private fun showError(message: String?) {
        // message 가 null 값인 경우 "Unexpected error." 메시지를 표시함.
//        tvMessage.text = message
//        pbProgress.visibility = View.VISIBLE

        // with() 함수 사용하여
        // tvActivityRepositoryMessage 범위 내에서 작업을 수행 함.
        with(tvActivityRepositoryMessage) {
            text = message
            visibility = View.VISIBLE
        }
    }
}
