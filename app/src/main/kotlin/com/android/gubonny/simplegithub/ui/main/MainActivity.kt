package com.android.gubonny.simplegithub.ui.main

import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View

import com.android.gubonny.simplegithub.R
import com.android.gubonny.simplegithub.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {

    internal lateinit var btnSearch: FloatingActionButton

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSearch.setOnClickListener {
            // 저장소 검색 액티비티를 호출합니다.
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }

    }
}
