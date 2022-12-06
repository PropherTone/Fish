package com.protone.projectDesign.arouter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.protone.common.baseType.launchMain
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.ProjectDesignRouterPath.DestinationA
import com.protone.common.utils.onResult
import com.protone.projectDesign.R
import com.protone.projectDesign.databinding.ActivityDestinationAactivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

@Route(path = DestinationA)
class DestinationAActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val binding by lazy { ActivityDestinationAactivityBinding.inflate(newLayoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.jump.setOnClickListener {
            ARouter.getInstance().build(DestinationB)
                .withTransition(R.anim.swap_in, R.anim.swap_out)
                .with(Bundle().apply {
                    putString("data", "From DestinationA's Activity")
                }).navigation(this, 1)
        }
        binding.jumpWithResult.setOnClickListener {
            launch {
                startActivityForResult(DestinationB).let {
                    binding.msg.text = it?.action
                }
            }
        }
    }

    val code = AtomicInteger(0)

    private val _activityResultMessenger by lazy { MutableSharedFlow<Intent?>() }
    val activityResultMessenger by lazy { _activityResultMessenger.asSharedFlow() }
    private suspend inline fun startActivityForResult(
        routerPath: String,
        crossinline postCard: Postcard.() -> Postcard = { this },
    ) = onResult { co ->
        ARouter.getInstance()
            .build(routerPath)
            .postCard()
            .navigation(this@DestinationAActivity, code.incrementAndGet())
        var job: Job? = null
        job = launchMain {
            activityResultMessenger.collect {
                co.resumeWith(Result.success(it))
                job?.cancel()
            }
        }
        job.start()
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == code.get()) {
            launch { if (data != null) _activityResultMessenger.emit(data) }
        }
    }
}