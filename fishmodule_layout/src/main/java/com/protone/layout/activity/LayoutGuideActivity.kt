package com.protone.layout.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.protone.common.component.ModelTestListHelper
import com.protone.common.context.checkNeededPermission
import com.protone.common.context.newLayoutInflater
import com.protone.common.context.requestContentPermission
import com.protone.common.routerPath.LayoutRouterPath
import com.protone.fishmodule_layout.databinding.ActivityGuideLayoutBinding

class LayoutGuideActivity : AppCompatActivity() {

    private val binding by lazy { ActivityGuideLayoutBinding.inflate(newLayoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkNeededPermission({ requestContentPermission() }, {})
        //添加模块入口Activity索引
        //first：显示名称  second：ARouter路由地址
        ModelTestListHelper<String>().add("LoadingList", LayoutRouterPath.List)
            .add("Pagination", LayoutRouterPath.Github)
            .add("Blur", LayoutRouterPath.Blur)
            .add("Reveal", LayoutRouterPath.Reveal)
            .init(binding.list, GridLayoutManager(this, 2), 12) {
                ARouter.getInstance().build(it).navigation()
            }
    }
}