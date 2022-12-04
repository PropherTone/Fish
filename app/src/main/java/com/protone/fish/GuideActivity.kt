package com.protone.fish

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.protone.common.component.ModelTestListHelper
import com.protone.common.context.checkNeededPermission
import com.protone.common.context.newLayoutInflater
import com.protone.common.context.requestContentPermission
import com.protone.common.routerPath.CoroutineRouterPath
import com.protone.common.routerPath.NetRouterPath
import com.protone.common.routerPath.ProjectDesignRouterPath
import com.protone.fish.databinding.ActivityGuideBinding

class GuideActivity : AppCompatActivity() {

    private val binding by lazy { ActivityGuideBinding.inflate(newLayoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkNeededPermission({
            requestContentPermission()
        }, {
        })
        //添加模块入口Activity索引
        //first：显示名称  second：ARouter路由地址
        ModelTestListHelper<String>().add("Module Net", NetRouterPath.Main)
            .add("MVVM", ProjectDesignRouterPath.MVVM)
            .add("ARouterTest", ProjectDesignRouterPath.DestinationA)
            .add("CoroutineTest", CoroutineRouterPath.Coroutine)
            .add("Login", NetRouterPath.Login)
            .init(binding.enterList, GridLayoutManager(this, 2), 12) {
                ARouter.getInstance().build(it).navigation()
            }

    }
}