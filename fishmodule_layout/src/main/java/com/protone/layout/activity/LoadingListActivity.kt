package com.protone.layout.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.protone.common.baseType.withDefaultContext
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.LayoutRouterPath
import com.protone.common.utils.json.toEntity
import com.protone.fishmodule_layout.databinding.ActivityLoadingListBinding
import com.protone.fishmodule_layout.databinding.GithubHotItemLayoutBinding
import com.protone.layout.adapter.GithubPagingAdapter
import com.protone.layout.entity.GithubHot
import com.protone.layout.entity.Item
import com.protone.layout.paging.PagingDataAPI
import kotlinx.coroutines.runBlocking

@Route(path = LayoutRouterPath.LoadingList)
class LoadingListActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoadingListBinding.inflate(newLayoutInflater)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private val list by lazy {
        runBlocking {
            withDefaultContext {
                PagingDataAPI().api
                    .searchRepositories(page = 1, perPage = 50)
                    .execute()
                    .body()?.let {
                        String(it.bytes()).toEntity(GithubHot::class.java).items as MutableList<Item>
                    }
            }
        } ?: mutableListOf()
    }

    private var itemCount = 100

    private val listAdapter by lazy {
        object : RecyclerView.Adapter<GithubPagingAdapter.Holder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): GithubPagingAdapter.Holder {
                return GithubPagingAdapter.Holder(
                    GithubHotItemLayoutBinding.inflate(
                        newLayoutInflater,
                        parent,
                        false
                    )
                )
            }

            override fun onBindViewHolder(holder: GithubPagingAdapter.Holder, position: Int) {
                if (position >= list.size) {
                    holder.binding.apply {
                        name.text = "$position"
                        description.text = "it.description"
                        star.text = "it.stargazers_count.toString()"
                    }
                    return
                }
                holder.binding.apply {
                    list[position].let {
                        name.text = "[$position]${it.name}"
                        description.text = it.description
                        star.text = it.stargazers_count.toString()
                    }
                }
            }

            override fun getItemCount(): Int = this@LoadingListActivity.itemCount

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.list.apply {
            layoutManager = LinearLayoutManager(this@LoadingListActivity)
            adapter = listAdapter
            postDelayed({
                list.addAll(list)
                itemCount = 200
                listAdapter.notifyItemRangeChanged(50, 80)
            }, 20000)
        }
    }
}