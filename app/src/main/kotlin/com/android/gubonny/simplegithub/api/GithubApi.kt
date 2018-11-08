package com.android.gubonny.simplegithub.api

import com.android.gubonny.simplegithub.api.model.GithubRepo
import com.android.gubonny.simplegithub.api.model.RepoSearchResponse

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Github API 저장소 검색 API 와
 * 저장소 정보 읽기 API 를 구현 함.
 */
interface GithubApi {

    @GET("search/repositories")
    fun searchRepository(@Query("q") query: String): Call<RepoSearchResponse>

    @GET("repos/{owner}/{name}")
    fun getRepository(
            @Path("owner") ownerLogin: String,
            @Path("name") repoName: String): Call<GithubRepo>
}
