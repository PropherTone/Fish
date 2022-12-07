package com.protone.layout.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.protone.common.baseType.withDefaultContext
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.LayoutRouterPath
import com.protone.common.utils.RouterPath
import com.protone.common.utils.json.toEntity
import com.protone.fishmodule_layout.R
import com.protone.fishmodule_layout.databinding.ActivityListBinding
import com.protone.fishmodule_layout.databinding.GithubHotItemLayoutBinding
import com.protone.fishmodule_layout.databinding.ListItemLayoutBinding
import com.protone.layout.adapter.GithubPagingAdapter
import com.protone.layout.entity.GithubHot
import com.protone.layout.entity.Item
import com.protone.layout.paging.PagingDataAPI
import kotlinx.coroutines.runBlocking

@Route(path = LayoutRouterPath.List)
class ListActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityListBinding.inflate(newLayoutInflater)
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
                holder.binding.apply {
                    list[position].let {
                        name.text = it.name
                        description.text = it.description
                        star.text = it.stargazers_count.toString()
                    }
                }
            }

            override fun getItemCount(): Int = list.size

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.list.apply {
            layoutManager = LinearLayoutManager(this@ListActivity)
            adapter = listAdapter
        }
    }
}