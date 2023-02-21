package com.protone.layout.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.alibaba.android.arouter.facade.annotation.Route
import com.protone.common.baseType.launchDefault
import com.protone.common.context.MApplication
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.LayoutRouterPath
import com.protone.common.utils.TAG
import com.protone.fishmodule_layout.databinding.ActivityBlurBinding
import com.protone.layout.view.blur.RenderScriptBlur
import com.protone.layout.view.blurView.DefaultBlurController
import com.protone.layout.view.blurView.DefaultBlurEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay

@Route(path = LayoutRouterPath.Blur)
class BlurActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val binding by lazy { ActivityBlurBinding.inflate(newLayoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            blur.post {
                val animator = ObjectAnimator.ofFloat(
                    view,
                    "translationY",
                    0f,
                    blur.measuredHeight.toFloat() - view.measuredHeight
                )
                animator.repeatMode = ValueAnimator.REVERSE
                animator.repeatCount = ValueAnimator.INFINITE
                animator.duration = 2000L
                animator.start()
            }
            root.viewTreeObserver.addOnPreDrawListener {
                blur.renderFrame()
                true
            }
            blur.initBlurTool(
                DefaultBlurController(
                    root as ViewGroup,
                    DefaultBlurEngine()
                )
            )
//            blur.setupWith(root as ViewGroup, RenderScriptBlur(MApplication.app))
//                .setBlurRadius(24f)
        }

    }
}