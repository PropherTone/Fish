package com.protone.projectDesign.common

import android.util.Log
import com.protone.common.utils.TAG
import java.util.*

class ServiceProxy : IService {

    private val loader = ServiceLoader.load(IService::class.java)

    fun show() {
        loader.forEach {
            Log.d(TAG, "show: ${it.javaClass.name}")
        }
    }
}