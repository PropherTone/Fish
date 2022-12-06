package com.protone.layout.activity

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.protone.common.baseType.launchDefault
import com.protone.common.baseType.launchMain
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.LayoutRouterPath
import com.protone.common.utils.TAG
import com.protone.fishmodule_layout.databinding.ActivityGithubBinding
import com.protone.layout.GithubPagingAdapter
import com.protone.layout.viewModel.GithubViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@Route(path = LayoutRouterPath.Github)
class GithubActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val binding: ActivityGithubBinding by lazy {
        ActivityGithubBinding.inflate(newLayoutInflater)
    }

    private val viewModel: GithubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.list.apply {
            layoutManager = LinearLayoutManager(this@GithubActivity)
            adapter = GithubPagingAdapter()
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.top = 24
                }
            })
            launchDefault {
                viewModel.getPagingData().collect {
                    (adapter as GithubPagingAdapter).submitData(it)
                }
            }
            (adapter as GithubPagingAdapter).addLoadStateListener {
                Log.d(TAG, "onCreate: $it")
            }
        }
    }
}