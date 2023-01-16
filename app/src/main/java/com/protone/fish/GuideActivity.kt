package com.protone.fish

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.protone.common.baseType.withIOContext
import com.protone.common.component.ModelTestListHelper
import com.protone.common.context.checkNeededPermission
import com.protone.common.context.newLayoutInflater
import com.protone.common.context.requestContentPermission
import com.protone.common.routerPath.CoroutineRouterPath
import com.protone.common.routerPath.LayoutRouterPath
import com.protone.common.routerPath.NetRouterPath
import com.protone.common.routerPath.ProjectDesignRouterPath
import com.protone.common.utils.TAG
import com.protone.fish.databinding.ActivityGuideBinding

class GuideActivity : AppCompatActivity(){

    private val binding by lazy { ActivityGuideBinding.inflate(newLayoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val newList = mutableListOf(0, 1, 2, 3, 4, 5, 6, 7)
        val oldList = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8)

        val filter = newList.filter { !oldList.remove(it) }
        Log.d(TAG, "onCreate: filter $filter")
        Log.d(TAG, "onCreate: oldList $oldList")

        checkNeededPermission({ requestContentPermission() }, {})
        //添加模块入口Activity索引
        //first：显示名称  second：ARouter路由地址
        ModelTestListHelper<String>().add("Module Net", NetRouterPath.Main)
            .add("MVVM", ProjectDesignRouterPath.MVVM)
            .add("ARouterTest", ProjectDesignRouterPath.DestinationA)
            .add("CoroutineTest", CoroutineRouterPath.Coroutine)
            .add("Login", NetRouterPath.Login)
            .add("Paging", LayoutRouterPath.Github)
            .add("List", LayoutRouterPath.LoadingList)
            .init(binding.enterList, GridLayoutManager(this, 2), 12) {
                ARouter.getInstance().build(it).navigation()
            }
    }
}