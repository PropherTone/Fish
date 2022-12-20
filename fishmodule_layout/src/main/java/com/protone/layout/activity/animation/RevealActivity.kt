package com.protone.layout.activity.animation

import android.os.Bundle
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.LayoutRouterPath
import com.protone.fishmodule_layout.databinding.ActivityRevealBinding
import kotlin.math.hypot

@Route(path = LayoutRouterPath.Reveal)
class RevealActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRevealBinding.inflate(newLayoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            button.setOnClickListener {
                // get the center for the clipping circle
                val cx = view.width / 2
                val cy = view.height / 2

                // get the final radius for the clipping circle
                val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
                val reveal =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
                view.isVisible = true
                reveal.start()
            }
            button2.setOnClickListener {
                // get the center for the clipping circle
                val cx = view.width / 2
                val cy = view.height / 2

                // get the final radius for the clipping circle
                val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
                val reveal =
                    ViewAnimationUtils.createCircularReveal(view, 0, 0, finalRadius, 0f)
                reveal.doOnEnd {
                    view.isVisible = false
                }
                reveal.start()
            }
        }
    }
}