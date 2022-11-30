package com.protone.projectDesign.arouter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.ProjectDesignRouterPath.DestinationA
import com.protone.projectDesign.R
import com.protone.projectDesign.databinding.ActivityDestinationBactivityBinding

const val DestinationB = "/Destination/B"

@Route(path = DestinationB)
class DestinationBActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDestinationBactivityBinding.inflate(newLayoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            msg.text = intent.extras?.getString("data")
            jump.setOnClickListener {
                ARouter.getInstance().build(DestinationA).navigation()
            }
        }
        setResult(123, Intent("asd"))
    }
}