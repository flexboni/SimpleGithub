package com.android.gubonny.simplegithub.ui.search

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView

import com.android.gubonny.simplegithub.R
import com.android.gubonny.simplegithub.api.GithubApi
import com.android.gubonny.simplegithub.api.GithubApiProvider
import com.android.gubonny.simplegithub.api.model.GithubRepo
import com.android.gubonny.simplegithub.api.model.RepoSearchResponse
import com.android.gubonny.simplegithub.ui.repo.RepositoryActivity

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListenerNew {

    internal lateinit var rvList: RecyclerView

    internal lateinit var progress: ProgressBar

    internal lateinit var tvMessage: TextView

    internal lateinit var adpater: SearchAdapter

    internal lateinit var api: GithubApi

    internal lateinit var menuSearch: MenuItem

    internal lateinit var searchView: SearchView

    internal lateinit var searchCall: Call<RepoSearchResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        adpater = SearchAdapter()
        adpater.setItemClickListener(this)

        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adpater

        api = GithubApiProvider.provideGithubApi(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_search, menu)
        menuSearch = menu.findItem(R.id.menu_activity_search_query)

        // SearchView 로 캐스팅
        searchView = menuSearch.actionView as SearchView
        // SearchView.OnQueryTextListener 인터페이스 구현하는
        // 익명 클래스의 인스턴스를 생성함.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                updateTitle(query)
                hideSoftKeyboard()
                collapseSearchView()
                searchRepository(query)

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        menuSearch.expandActionView()

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
        val intent = Intent(this, RepositoryActivity::class.java)
        intent.putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.owner.login)
        intent.putExtra(RepositoryActivity.KEY_REPO_NAME, repository.name)
        startActivity(intent)
    }

    private fun searchRepository(query: String) {
        clearResults()
        hideError()
        showProgress()

        searchCall = api.searchRepository(query)

        // Call 인터페이스를 구현하는 익명 클래스의 인스턴스를 생성함.
        searchCall.enqueue(object : Callback<RepoSearchResponse> {
            override fun onResponse(call: Call<RepoSearchResponse>, response: Response<RepoSearchResponse>) {
                hideProgress()

                val searchResult = response.body()
                if (response.isSuccessful && null != searchResult) {
                    adpater.setItems(searchResult.items)
                    adpater.notifyDataSetChanged()

                    if (0 == searchResult.totalCount) {
                        showError(getString(R.string.no_search_result))
                    }
                } else {
                    showError("Not successful: " + response.message())
                }
            }

            override fun onFailure(call: Call<RepoSearchResponse>, t: Throwable) {
                hideProgress()

                // showError 함수는 null 값을 허용하지 않으나
                // t.message 는 null 값을 반환할 수 있음
                showError(t.message)
            }
        })
    }

    private fun updateTitle(query: String) {
        val ab = supportActionBar
        if (null != ab) {
            ab.subtitle = query
        }
    }

    private fun hideSoftKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }

    private fun collapseSearchView() {
        menuSearch.collapseActionView()
    }

    private fun clearResults() {
        adpater.clearItems()
        adpater.notifyDataSetChanged()
    }

    private fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progress.visibility = View.GONE
    }

    private fun showError(message: String?) {
        // message 가 널 값인 경우 "Unexpected error" 메시지를 표시함
        tvMessage.text = message
        tvMessage.visibility = View.VISIBLE
    }

    private fun hideError() {
        tvMessage.text = ""
        tvMessage.visibility = View.GONE
    }
}
