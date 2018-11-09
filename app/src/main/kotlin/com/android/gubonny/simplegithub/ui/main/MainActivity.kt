package com.android.gubonny.simplegithub.ui.main

//import android.content.Intent
//import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
//import android.view.View

import com.android.gubonny.simplegithub.R
import com.android.gubonny.simplegithub.api.model.GithubRepo
import com.android.gubonny.simplegithub.data.provideSearchHistroyDao
import com.android.gubonny.simplegithub.rx.AutoClearedDisposable
import com.android.gubonny.simplegithub.ui.search.SearchActivity
import com.android.gubonny.simplegithub.ui.search.SearchAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), SearchAdapter.ItemClickListenerNew {

    override fun onItemClick(repository: GithubRepo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    internal lateinit var btnSearch: FloatingActionButton

    // 어댑터 프로퍼티를 추가 함.
    internal val adapter by lazy {
        SearchAdapter().apply { setItemClickListener(this@MainActivity) }
    }

    // 최근 조회한 저장소를 담당하는 데이터 접근 객체 프로퍼티를 추가 함.
    internal val searchHistoryDao by lazy { provideSearchHistroyDao(this) }

    // 디스포저블을 관리하는 프로퍼티를 추가.
    internal val disposables = AutoClearedDisposable(this)

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnActivityMainSearch.setOnClickListener {
            // 저장소 검색 액티비티를 호출합니다.
//            startActivity(Intent(this@MainActivity, SearchActivity::class.java))

            // 호출할 액티비티만 명시 함.
            startActivity<SearchActivity>()
        }
    }
}
