package com.android.gubonny.simplegithub.ui.search

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.android.gubonny.simplegithub.R
import com.android.gubonny.simplegithub.api.model.GithubRepo
import com.android.gubonny.simplegithub.ui.GlideApp
import kotlinx.android.synthetic.main.item_repository.view.*


import java.util.ArrayList

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.RepositoryHolder>() {

    //    private var items: MutableList<GithubRepo> = ArrayList()
    // 명확한 함수를 사용해 초기화 하는게 좋음.
    private var items: MutableList<GithubRepo> = mutableListOf()

    private val placeholder = ColorDrawable(Color.GRAY)

    private var listener: ItemClickListenerNew? = null

    // 항상 RepositoryHolder 객체만 반환하므로 단일 표현식으로 표현 함.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryHolder = RepositoryHolder(parent)


    override fun onBindViewHolder(holder: RepositoryHolder, position: Int) {
//        // items.get(position) 대신 배열 인덱스 접근 연산자를 사용함.
//        val repo = items[position]
//
//        // with() 함수를 사용하여 holder.itemView 를 여러 번 호출하지 않도록 함.
//        with(holder.itemView) {
//            GlideApp.with(holder.itemView.context)
//                    .load(repo.owner.avatarUrl)
//                    .placeholder(placeholder)
//                    // 뷰 ID를 사용하여 뷰 인스턴스에 접근.
//                    .into(ivItemRepositoryProfile)
//
//            // 뷰 ID를 사용하여 뷰 인스턴스에 접근.
//            tvItemRepositoryName.text = repo.fullName
//            tvItemRepositoryLanguage.text = if (TextUtils.isEmpty(repo.language))
//                holder.itemView.context.getText(R.string.no_language_specified)
//            else
//                repo.language
//
//            // View.OnClickListener 의 본체를 람다 표현식으로 작성함.
//            holder.itemView.setOnClickListener {
//                if (null != listener) {
//                    listener!!.onItemClick(repo)
//                }
//            }
//        }

        // 뷰에 데이터를 반영하기 위해 사용될 뿐,
        // 다른 곳에서는 사용하지 않는다.
        // 즉, let() 함수를 사용해 값이 사용되는 범위 명시적으로 한정.
        items[position].let { repo ->
            // with() 함수를 사용하여 holder.itemView 를 여러 번 호출하지 않도록 함.
            with(holder.itemView) {
                GlideApp.with(holder.itemView.context)
                        .load(repo.owner.avatarUrl)
                        .placeholder(placeholder)
                        // 뷰 ID를 사용하여 뷰 인스턴스에 접근.
                        .into(ivItemRepositoryProfile)

                // 뷰 ID를 사용하여 뷰 인스턴스에 접근.
                tvItemRepositoryName.text = repo.fullName
                tvItemRepositoryLanguage.text = if (TextUtils.isEmpty(repo.language))
                    holder.itemView.context.getText(R.string.no_language_specified)
                else
                    repo.language

                // View.OnClickListener 의 본체를 람다 표현식으로 작성함.
                setOnClickListener { listener!!.onItemClick(repo) }
            }
        }
    }

    // 항상 리스트 크기만 반환하므로 단일 표현식으로 표현 함.
    override fun getItemCount(): Int = items.size

    fun setItems(items: List<GithubRepo>) {
        // 인자로 받은 리스트의 형태를 어댑터 내부에서 사용하는
        // 리스트 형태(내부 자료 변경이 가능한 형태)로 변환해 주어야함.
        this.items = items.toMutableList()
    }

    fun clearItems() {
        this.items.clear()
    }

    interface ItemClickListener

    // 'internal' 키워드를 제거하여 가시성을 'public' 으로 변경.
    class RepositoryHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_repository, parent, false))
//    {
//        // init 블록에서 프로퍼티의 값을 선언해 주고 있으므로
//        // 여기에서 값을 할당하지 않아도 컴파일 에러가 발생하지 않음.
//        var ivProfile: ImageView
//
//        var tvName: TextView
//
//        var tvLanguage: TextView
//
//        init {
//            ivProfile = itemView.findViewById(R.id.ivItemRepositoryProfile)
//            tvName = itemView.findViewById(R.id.tvItemRepositoryName)
//            tvLanguage = itemView.findViewById(R.id.tvItemRepositoryLanguage)
//        }
//    }

    fun setItemClickListener(listener: ItemClickListenerNew?) {
        this.listener = listener
    }

    interface ItemClickListenerNew {

        fun onItemClick(repository: GithubRepo)
    }
}
