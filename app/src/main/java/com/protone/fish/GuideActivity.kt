package com.protone.fish

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.NetRouterPath
import com.protone.fish.databinding.ActivityGuideBinding
import com.protone.fish.databinding.GuideItemBinding

class GuideActivity : AppCompatActivity() {

    private val binding by lazy { ActivityGuideBinding.inflate(newLayoutInflater) }

    //first：显示名称  second：ARouter路由地址
    private val guideList by lazy { mutableListOf<Pair<String, String>>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        guideList.apply {
            //添加模块入口Activity索引
            add(Pair("Module Net", NetRouterPath.Main))
        }

        binding.enterList.apply {
            layoutManager = GridLayoutManager(this@GuideActivity, 2)
            setHasFixedSize(true)
            adapter = object : RecyclerView.Adapter<GuideViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
                    return GuideViewHolder(GuideItemBinding.inflate(newLayoutInflater))
                }

                override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
                    holder.binding.guideButton.apply {
                        text = guideList[position].first
                        setOnClickListener {
                            ARouter.getInstance().build(guideList[position].second).navigation()
                        }
                    }
                }

                override fun getItemCount(): Int = guideList.size

            }
        }

    }

    class GuideViewHolder(val binding: GuideItemBinding) : RecyclerView.ViewHolder(binding.root)
}