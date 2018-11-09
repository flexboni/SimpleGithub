package com.android.gubonny.simplegithub.data

import android.arch.persistence.room.Room
import android.content.Context

// SimpleGithubDatabase 의 인스턴스를 저장 함.
private var instance: SimpleGithubDatabase? = null

// 저장소 조회 기록을 담당하는 데이터 접근 객체를 제공 함.
fun provideSearchHistroyDao(context: Context): SearchHistoryDao = provideDatabase(context).searchHistoryDao()

// SimpleGithubDatabase 룸 데이터베이스를 제공 함.
// 싱글톤 패턴을 사용하여 인스턴스를 최초 1회만 생성.
private fun provideDatabase(context: Context): SimpleGithubDatabase {
    if (null == instance) {

        // simple_github.db 데이터베이스 파일을 사용하는 룸 데이터베이스를 생성.
        instance = Room.databaseBuilder(context.applicationContext,
                SimpleGithubDatabase::class.java,
                "simple_github.db").build()
    }

    // 룸 데이터베이스 인스턴스르 반환 함.
    return instance!!
}
