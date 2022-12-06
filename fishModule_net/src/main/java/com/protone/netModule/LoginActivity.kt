package com.protone.netModule

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.protone.common.baseType.launchDefault
import com.protone.common.baseType.withMainContext
import com.protone.common.component.ModelTestListHelper
import com.protone.common.context.newLayoutInflater
import com.protone.common.entity.BaseResponse
import com.protone.common.routerPath.NetRouterPath
import com.protone.common.utils.TAG
import com.protone.netModule.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.internal.and
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and

@Route(path = NetRouterPath.Login)
class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(newLayoutInflater)
    }

    companion object {
        private const val BASE_URL = "https://api.sunofbeaches.com"
        private const val L_C_I = "l_c_i"
        private const val TOKEN = "sob_token"
    }

    var lci = ""
    var token = ""

    interface API {
        @Streaming
        @GET("/uc/ut/captcha")
        suspend fun getCaptcha(@Query("code") code: Int): ResponseBody

        @POST("/uc/user/login/{captcha}")
        suspend fun login(
            @Path("captcha") captcha: String,
            @Body user: User
        ): BaseResponse<String>
    }

    data class User(val phoneNum: String, val password: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ModelTestListHelper<() -> Unit>()
            .add("retrofitGetCaptcha", retrofitGetCaptcha())
            .add("retrofitLogin", retrofitLogin())
            .init(binding.list, LinearLayoutManager(this), 12) {
                it.invoke()
            }
    }

    private fun retrofitGetCaptcha(): () -> Unit = {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor {
                        it.proceed(it.request()).let response@{ response ->
                            response.headers.forEach { header ->
                                if (header.first == L_C_I && header.second.isNotEmpty()) {
                                    lci = header.second
                                    Log.d(TAG, "retrofitLogin: $lci")
                                    return@response response
                                }
                            }
                            response
                        }
                    }.build()
            ).build().let {
                launchDefault {
                    val api = it.create(API::class.java)
                    val captcha = api.getCaptcha((0..100).random())
                    val decodeStream = BitmapFactory.decodeStream(captcha.byteStream())
                    withMainContext {
                        binding.image.setImageBitmap(decodeStream)

                    }
                }
            }
    }

    private fun retrofitLogin(): () -> Unit = {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor {
                        val builder = it.request().newBuilder()
                        if (lci.isNotEmpty()) {
                            builder.addHeader(L_C_I, lci)
                            Log.d(TAG, "retrofitLogin: addHeader $lci")
                        }
                        it.proceed(builder.build()).let response@{ response ->
                            response.headers.forEach { header ->
                                if (header.first == TOKEN && header.second.isNotEmpty()) {
                                    token = header.second
                                    Log.d(TAG, "retrofitLogin: $token")
                                    return@response response
                                }
                            }
                            response
                        }
                    }
                    .addInterceptor(HttpLoggingInterceptor {}.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            ).addConverterFactory(GsonConverterFactory.create())
            .build().let {
                launchDefault {
                    val api = it.create(API::class.java)
                    api.login(
                        binding.captcha.text.toString(),
                        User(
                            "15805020294",
                            byteToHexString(
                                MessageDigest.getInstance("MD5")
                                    .digest("dsxnd".toByteArray())
                            )
                        )
                    ).let { login ->
                        Log.d(TAG, "retrofitLogin: ${login.code}")
                        Log.d(TAG, "retrofitLogin: ${login.message}")
                    }
                }
            }
    }

    fun byteToHexString(b: ByteArray): String {
        val hexString = StringBuffer()
        for (i in b.indices) {
            var hex = Integer.toHexString(b[i] and 0xFF)
            if (hex.length == 1) {
                hex = "0$hex"
            }
            hexString.append(hex.uppercase(Locale.getDefault()))
        }
        return hexString.toString()
    }

}