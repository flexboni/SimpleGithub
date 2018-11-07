package com.android.gubonny.simplegithub.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * GithubApi 에 정의된 API 의 응답을 표현하기 위한 클래스.
 */
public class RepoSearchResponse {

    @SerializedName("total_count")
    public final int totalCount;

    // GithubRepo 형태의 리스트를 표현함.
    public final List<GithubRepo> items;

    public RepoSearchResponse(int totalCount, List<GithubRepo> items) {
        this.totalCount = totalCount;
        this.items = items;
    }
}
