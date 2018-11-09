package com.android.gubonny.simplegithub.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.android.gubonny.simplegithub.api.model.GithubRepo

@Database(entities = arrayOf(GithubRepo::class), version = 1)
abstract class SimpleGithubDatabase : RoomDatabase() {

    // 데이터베이스와 연결할 데이터 접근 객체를 정의 함.
    abstract fun searchHistoryDao(): SearchHistoryDao
}