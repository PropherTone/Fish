package com.protone.layout.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.protone.common.baseType.bufferCollect
import com.protone.common.baseType.launchDefault
import com.protone.common.baseType.withMainContext
import com.protone.common.component.ModelTestListHelper
import com.protone.common.context.checkNeededPermission
import com.protone.common.context.newLayoutInflater
import com.protone.common.context.requestContentPermission
import com.protone.common.routerPath.LayoutRouterPath
import com.protone.common.utils.TAG
import com.protone.fishmodule_layout.databinding.ActivityGuideLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow

class LayoutGuideActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val binding by lazy { ActivityGuideLayoutBinding.inflate(newLayoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        val intent = Intent(Intent.ACTION_PICK, null)
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            it?.let { uri ->
                Log.d(TAG, "onCreate: $uri")
                binding.image.setImageResource(uri)
            }
        }.launch(arrayOf("image/*"))
        checkNeededPermission({ requestContentPermission() }, {})
        //添加模块入口Activity索引
        //first：显示名称  second：ARouter路由地址
        ModelTestListHelper<String>().add("LoadingList", LayoutRouterPath.LoadingList)
            .add("Pagination", LayoutRouterPath.Github)
            .add("Blur", LayoutRouterPath.Blur)
            .add("Reveal", LayoutRouterPath.Reveal)
            .add("List", LayoutRouterPath.List)
            .init(binding.list, GridLayoutManager(this, 2), 12) {
                ARouter.getInstance().build(it).navigation()
            }
    }
}