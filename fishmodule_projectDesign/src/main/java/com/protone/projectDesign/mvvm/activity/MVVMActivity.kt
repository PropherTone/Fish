package com.protone.projectDesign.mvvm.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.ProjectDesignRouterPath
import com.protone.projectDesign.BR
import com.protone.projectDesign.R
import com.protone.projectDesign.common.ServiceProxy
import com.protone.projectDesign.databinding.ActivityMvvmBinding
import com.protone.projectDesign.mvvm.viewModel.MVVMViewModel

@Route(path = ProjectDesignRouterPath.MVVM)
class MVVMActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMvvmBinding.inflate(newLayoutInflater) }
    private val model by viewModels<MVVMViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ServiceProxy().show()
        binding.setVariable(BR.model, model)
        model.buttonText.set("设置背景")
        model.listener.set {
            model.idRes.set(R.drawable.main_background)
        }
    }
}