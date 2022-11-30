package com.protone.projectDesign.arouter

import android.content.Context
import android.util.Log
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.protone.common.utils.TAG

//ARouter拦截器，priority越高优先级越高，不同的拦截器使用相同的priority会报错
@Interceptor(priority = 1)
class RouterInterceptor : IInterceptor {
    override fun init(context: Context?) {
        Log.d(TAG, "init: ")
    }

    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
        Log.d(TAG, "process: ${postcard?.extras}")
        callback?.onContinue(postcard)
    }
}