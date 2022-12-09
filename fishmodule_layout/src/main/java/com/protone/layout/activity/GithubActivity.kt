package com.protone.layout.activity

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.protone.common.baseType.launchDefault
import com.protone.common.component.Holder
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.LayoutRouterPath
import com.protone.common.utils.TAG
import com.protone.common.utils.json.toEntity
import com.protone.fishmodule_layout.R
import com.protone.fishmodule_layout.databinding.ActivityGithubBinding
import com.protone.fishmodule_layout.databinding.FootLoaderItemLayoutBinding
import com.protone.layout.adapter.GithubPagingAdapter
import com.protone.layout.entity.GithubHot
import com.protone.layout.paging.PagingDataAPI
import com.protone.layout.viewModel.GithubViewModel
import kotlinx.coroutines.*


@Route(path = LayoutRouterPath.Github)
class GithubActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val binding: ActivityGithubBinding by lazy {
        ActivityGithubBinding.inflate(newLayoutInflater)
    }

    private val itemAdapter by lazy {
        GithubPagingAdapter()
    }

    private val footAdapter by lazy {
        object : LoadStateAdapter<Holder<FootLoaderItemLayoutBinding>>() {
            override fun onBindViewHolder(
                holder: Holder<FootLoaderItemLayoutBinding>,
                loadState: LoadState
            ) {
                holder.binding.loaderStates.setImageResource(
                    when (loadState) {
                        is LoadState.Loading -> R.drawable.ic_baseline_cloud_download_24
                        is LoadState.NotLoading -> R.drawable.ic_baseline_check_24
                        is LoadState.Error -> R.drawable.ic_baseline_error_outline_24
                    }
                )
            }

            override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
                return loadState is LoadState.Loading
                        || loadState is LoadState.Error
                        || (loadState is LoadState.NotLoading && loadState.endOfPaginationReached)
            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                loadState: LoadState
            ): Holder<FootLoaderItemLayoutBinding> {
                return Holder(FootLoaderItemLayoutBinding.inflate(newLayoutInflater, parent, false))
            }

        }
    }

    private val viewModel: GithubViewModel by viewModels()

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        launch {
//            launchDefault {
//                PagingDataAPI().api.searchRepositories(page = 1, perPage = 50)
//                    .execute()
//                    .body()
//                    ?.bytes()?.let {
//                        String(it).toEntity(GithubHot::class.java).items
//                    }
//            }
            binding.list.apply {
                layoutManager = LinearLayoutManager(this@GithubActivity)
                adapter = itemAdapter
                itemAdapter.withLoadStateFooter(footAdapter)
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    val paint = Paint().apply {
                        color = Color.RED
                    }

                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.bottom = 24
                    }

                    override fun onDraw(
                        c: Canvas,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        parent.children.forEach {
                            c.drawRect(
                                0f,
                                it.bottom.toFloat(),
                                parent.width.toFloat(),
                                it.bottom + 24f,
                                paint
                            )
                        }
                    }
                })
                launchDefault {
                    viewModel.getPagingData().collect {
                        itemAdapter.submitData(it)
                    }
                }
                itemAdapter.addLoadStateListener {
                    Log.d(TAG, "onCreate: $it")
                }
            }
        }

    }
}