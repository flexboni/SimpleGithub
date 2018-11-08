package com.android.gubonny.simplegithub.ui.search

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar

import com.android.gubonny.simplegithub.R
import com.android.gubonny.simplegithub.api.model.GithubRepo
import com.android.gubonny.simplegithub.api.provideGithubApi
import com.android.gubonny.simplegithub.extensions.plusAssign
import com.android.gubonny.simplegithub.ui.repo.RepositoryActivity

import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.startActivity

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListenerNew {

    internal lateinit var progress: ProgressBar


    internal val adapater by lazy {
        // apply() 함수를 사용하여 객체 생성과 함수 호출을 한번에 수행 함.
        SearchAdapter().apply { setItemClickListener(this@SearchActivity) }
    }

    internal val api by lazy {
        provideGithubApi(this)
    }

    internal lateinit var menuSearch: MenuItem

    internal lateinit var searchView: SearchView

    //    internal var searchCall: Call<RepoSearchResponse>? = null
    // 여러 disposable 객체를 관리할 수 있는 CompositeDisposable 객체를 초기화 함.
    // searchCall 대신 사용.
    internal val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        adapater.setItemClickListener(this)

        // with() 함수를 사용하여 rvActivitySearchList 범위 내에서 작업을 수행 함.
        with(rvActivitySearchList) {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapater
        }
    }

    override fun onStop() {
        super.onStop()
//        // 액티비티가 화면에서 사라지는 시점에
//        // API 호출 객체가 생성되어 있다면
//        // API 요청을 취소 함.
//        searchCall?.run { cancel() }

        // 관리하고 있던 디스포저블 객체를 모두 해제 함.
        // 네트워크 요청이 있다고 하면 자동 취소 됨.
        disposables.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_search, menu)
        menuSearch = menu.findItem(R.id.menu_activity_search_query)

        // SearchView 로 캐스팅.
//        searchView = menuSearch.actionView as SearchView
        // SearchView.OnQueryTextListener 인터페이스 구현하는
        // 익명 클래스의 인스턴스를 생성함.
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                updateTitle(query)
//                hideSoftKeyboard()
//                collapseSearchView()
//                searchRepository(query)
//
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                return false
//            }
//        })

        // apply() 함수를 사용하여 객체 생성과 리스너 지정을 동시에 수행 함.
        searchView = (menuSearch.actionView as SearchView).apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

                    updateTitle(query)
                    hideSoftKeyboard()
                    collapseSearchView()
                    searchRepository(query)

                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean = false

            })
        }

        // with() 함수를 사용하여 menuSearch 범위 내에서 작업을 수행 함.
        with(menuSearch) {
            setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean = true

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

                    if ("" == searchView.query) {
                        finish()
                    }

                    return true
                }
            })

            expandActionView()
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.menu_activity_search_query == item.itemId) {
            item.expandActionView()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(repository: GithubRepo) {
//        // apply() 함수를 사용하여 객체 생성과 extra 를 추가하는 작업을 동시에 수행 함.
//        val intent = Intent(this, RepositoryActivity::class.java).apply {
//            // 인텐트 부가 정보에 저장소 소유자 정보와 저장소 이름 추가.
//            putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.owner.login)
//            putExtra(RepositoryActivity.KEY_REPO_NAME, repository.name)
//    }

        // 부가정보로 전달할 항목을 함수의 인자로 바로 넣어 줌.
        startActivity<RepositoryActivity>(
                RepositoryActivity.KEY_USER_LOGIN to repository.owner.login,
                RepositoryActivity.KEY_REPO_NAME to repository.name)

//        startActivity(intent)
    }

    private fun searchRepository(query: String) {
//        clearResults()
//        hideError()
//        showProgress()
//
//        searchCall = api.searchRepository(query)
//
//        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스를 생성함.
//        // 앞에서 API 호출에 필요한 객체를 받았으므로
//        // null 이 아님을 보증해줘야 함.(!!)
//        searchCall!!.enqueue(object : Callback<RepoSearchResponse> {
//            override fun onResponse(call: Call<RepoSearchResponse>, response: Response<RepoSearchResponse>) {
//                hideProgress()
//
//                val searchResult = response.body()
//                if (response.isSuccessful && null != searchResult) {
//                    // with() 함수를 사용하여 adapter 범위 내에서 작업을 수행 함.
//                    with(adapater) {
//                        setItems(searchResult.items)
//                        notifyDataSetChanged()
//                    }
//
//                    if (0 == searchResult.totalCount) {
//                        showError(getString(R.string.no_search_result))
//                    }
//
//                } else {
//                    showError("Not successful: " + response.message())
//                }
//            }
//
//            override fun onFailure(call: Call<RepoSearchResponse>, t: Throwable) {
//                hideProgress()
//
//                // showError 함수는 null 값을 허용하지 않으나
//                // t.message 는 null 값을 반환할 수 있음
//                showError(t.message)
//            }
//        })

        // REST API 를 통해 검색 결과를 요청 함.
//        disposables.add(api.searchRepository(query)
        // '+=' 연산자로 disposable CompositeDisposable 에 추가.
        disposables += api.searchRepository(query)

                // Observable 형태로 결과를 바꿔주기 위해 flatMap 을 사용 함.
                .flatMap {
                    if (0 == it.totalCount) {
                        // 검색 결과가 없을 경우
                        // 에러를 발생시켜 에러 메시지를 표시하도록 함.
                        // (곧바로 에러 블록이 실행 됨)
                        Observable.error(IllegalStateException("No search result"))
                    } else {
                        // 검색 결과 리스트를 다음 스트림으로 전달.
                        Observable.just(it.items)
                    }
                }

                // 이 이후에 수행되는 코드는 모두 메인 스레드에서 실행 됨.
                // RxAndroid 에서 제공하는 스케줄러인
                // AndroidSchedulers.mainThread() 를 사용 함.
                .observeOn(AndroidSchedulers.mainThread())

                // 구독할 때 수행할 작업을 구현 함.
                .doOnSubscribe {
                    clearResults()
                    hideError()
                    showProgress()
                }

                // 스트림이 종료될 때 수행할 작업을 구현 함.
                .doOnTerminate { hideProgress() }

                // 옵서버블을 구독 함.
                .subscribe({ items ->

                    // API 를 통해 검색 결과를 정상적으로 받았을 때
                    // 처리할 작업을 구현 함.
                    with(adapater) {
                        setItems(items)
                        notifyDataSetChanged()
                    }
                }) {
                    // 에러블록
                    // 네트워크 오류나 데이터 처리 오류 등
                    // 작업이 정상적으로 완료되지 않았을 때 호출 됨.
                    showError(it.message)
                }
//        )
    }

    private fun updateTitle(query: String) {
//        val ab = supportActionBar
//        if (null != ab) {
//            ab.subtitle = query
//        }

        // 별도의 변수 선언 없이,
        // getSupportActionBar() 의 반환 값이 널이 아닌 경우에만 작업을 수행 함.
        supportActionBar?.run { subtitle = query }
    }

    private fun hideSoftKeyboard() {
//        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(searchView.windowToken, 0)

        // 별도의 변수 선언 없이 획득한 인스턴스의 범위 내에서 작업을 수행 함.
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).run {
            hideSoftInputFromWindow(searchView.windowToken, 0)
        }
    }

    private fun collapseSearchView() {
        menuSearch.collapseActionView()
    }

    private fun clearResults() {
//        adapater.clearItems()
//        adapater.notifyDataSetChanged()

        // with() 함수를 사용하여 adapter 범위 내에서 작업을 수행 함.
        with(adapater) {
            clearItems()
            notifyDataSetChanged()
        }
    }

    private fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progress.visibility = View.GONE
    }

    private fun showError(message: String?) {
        // message 가 널 값인 경우 "Unexpected error" 메시지를 표시함
//        tvMessage.text = message
//        tvMessage.visibility = View.VISIBLE

        // with() 함수를 사용하여 tvActivitySearchMessage 범위 내에서 작업을 수행 함.
        with(tvActivitySearchMessage) {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun hideError() {
//        tvMessage.text = ""
//        tvMessage.visibility = View.GONE

        // with() 함수를 사용하여 tvActivitySearchMessage 범위 내에서 작업을 수행 함.
        with(tvActivitySearchMessage) {
            text = ""
            visibility = View.VISIBLE
        }
    }
}
