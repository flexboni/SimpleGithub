package com.android.gubonny.simplegithub.api;

import com.android.gubonny.simplegithub.api.model.GithubRepo;
import com.android.gubonny.simplegithub.api.model.RepoSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Github API 저장소 검색 API 와
 * 저장소 정보 읽기 API 를 구현 함.
 */
public interface GithubApi {

    @GET("search/repositories")
    Call<RepoSearchResponse> searchRepository(@Query("q") String query);

    @GET("repos/{owner}/{name}")
    Call<GithubRepo> getRepository(
            @Path("owner") String ownerLogin,
            @Path("name") String repoName);
}
