package com.android.gubonny.simplegithub.ui.repo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.android.gubonny.simplegithub.R
import com.android.gubonny.simplegithub.api.GithubApi
import com.android.gubonny.simplegithub.api.GithubApiProvider
import com.android.gubonny.simplegithub.api.model.GithubRepo
import com.android.gubonny.simplegithub.ui.GlideApp

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryActivity : AppCompatActivity() {

    internal lateinit var llContent: LinearLayout

    internal lateinit var ivProfile: ImageView

    internal lateinit var tvName: TextView

    internal lateinit var tvStars: TextView

    internal lateinit var tvDescription: TextView

    internal lateinit var tvLanguage: TextView

    internal lateinit var tvLastUpdate: TextView

    internal lateinit var pbProgress: ProgressBar

    internal lateinit var tvMessage: TextView

    internal lateinit var api: GithubApi

    internal lateinit var repoCall: Call<GithubRepo>

    // REST API 응답에 포함된 날짜 및 시간 표시 형식입니다.
    internal var dateFormatInResoponse = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault()
    )

    // 화면에서 사용자에게 보여줄 날짜 및 시간 표시 형식입니다.
    internal var dateFormatToShow = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        api = GithubApiProvider.provideGithubApi(this)

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

    private fun showRepositoryInfo(login: String, repoName: String) {
        showProgress()

        repoCall = api.getRepository(login, repoName)

        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스 생성
        repoCall.enqueue(object : Callback<GithubRepo> {
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
        tvMessage.text = message
        pbProgress.visibility = View.VISIBLE
    }

    // 정적 필드로 정의되어 있던 항목은 동반 객체 내부에 정의함.
    companion object {

        val KEY_USER_LOGIN = "user_login"

        val KEY_REPO_NAME = "repo_name"
    }
}
