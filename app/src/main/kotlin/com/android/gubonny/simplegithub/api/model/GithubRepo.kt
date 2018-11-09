package com.android.gubonny.simplegithub.api.model

import android.accounts.AuthenticatorDescription
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.intellij.lang.annotations.Language

// GithubRepo 엔티티의 데이터가 저장될 테이블 이름을 repositories 로 지정
@Entity(tableName = "repositories")
class GithubRepo(
//        val name: String,
//        @field:SerializedName("full_name")
//        val fullName: String,
//        val owner: GithubOwner,
//        // null 값을 허용할 수 잇는 타입으로 선언 ('?')
//        val description: String?,
//        val language: String?,
//        @field:SerializedName("updated_at")
//        val updatedAt: String,
//        @field:SerializedName("stargazers_count")
//        val stars: Int)

        val name: String,

        @SerializedName("full_name")

        // fullName 프로퍼티를 주요 키로 사용하며,
        // 테이블 내 필드 이름은 full_name 으로 지정 함.
        @PrimaryKey @ColumnInfo(name = "full_name")
        val fullName: String,

        // GithubOwner 내 필드를 테이블에 함께 저장 함.
        @Embedded
        val owner: GithubOwner,

        val description: String?,
        val language: String?,
        @SerializedName("updated_at")

        // updatedAt 프로퍼티의 테이블 내 필드 이름을 updated_at 으로 지정 함.
        @ColumnInfo(name = "updated_at")
        val updatedAt: String,

        @SerializedName("stargazers_count")
        val stars: Int
)
