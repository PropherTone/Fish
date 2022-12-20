package com.protone.common

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.drawToBitmap
import com.alibaba.android.arouter.launcher.ARouter
import com.protone.common.baseType.DPI
import com.protone.common.context.MApplication
import com.protone.common.utils.SCrashHandler
import com.protone.common.utils.TAG
import com.protone.common.utils.displayUtils.Blur
import com.protone.common.utils.todayDate
import java.io.File

class MainApplication : Application() {

    companion object {
        @JvmStatic
        var ALIVE = "Terminate"
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Log.d(TAG, "attachBaseContext: ")
        ALIVE = "Alive"
        Blur.init(this)
        MApplication.init(this)
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(MApplication.app)
//        registerActivityLifecycleCallbacks(object :ActivityLifecycleCallbacks{
//            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
//                val view = activity.window.decorView
//                view.setLayerType(View.LAYER_TYPE_HARDWARE,Paint().apply {
//                    colorFilter = ColorMatrixColorFilter(ColorMatrix().also {
//                        it.setSaturation(0f)
//                    })
//                })
//                view.invalidate()
//            }
//
//            override fun onActivityStarted(activity: Activity) {
//            }
//
//            override fun onActivityResumed(activity: Activity) {
//            }
//
//            override fun onActivityPaused(activity: Activity) {
//            }
//
//            override fun onActivityStopped(activity: Activity) {
//            }
//
//            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
//            }
//
//            override fun onActivityDestroyed(activity: Activity) {
//            }
//
//        })
        DPI = MApplication.app.resources.displayMetrics.densityDpi
        val file = File("${base?.externalCacheDir?.path}/CrashLog")
        val result = if (!file.exists()) {
            file.mkdirs()
        } else true
        val todayDate = todayDate("yyyy_MM_dd_HH_mm_ss")
        SCrashHandler.path =
            if (result) "${base?.externalCacheDir?.path}/CrashLog/s_crash_log_${todayDate}.txt"
            else "${base?.externalCacheDir?.path}/s_crash_log_${todayDate}.txt"
    }

    override fun onTerminate() {
        super.onTerminate()
        ALIVE = "Terminate"
        Log.d(TAG, "onTerminate: ")
        ARouter.getInstance().destroy()
    }

}