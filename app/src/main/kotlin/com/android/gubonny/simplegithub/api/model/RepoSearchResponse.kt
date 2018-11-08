package com.android.gubonny.simplegithub.api.model

import com.google.gson.annotations.SerializedName

/**
 * GithubApi 에 정의된 API 의 응답을 표현하기 위한 클래스.
 */
class RepoSearchResponse(@field:SerializedName("total_count")
                         val totalCount: Int, // GithubRepo 형태의 리스트를 표현함.
                         val items: List<GithubRepo>)
