package com.protone.netModule

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.protone.common.baseType.launchDefault
import com.protone.common.baseType.launchMain
import com.protone.common.component.ModelTestListHelper
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.NetRouterPath
import com.protone.common.utils.displayUtils.imageLoader.Image
import com.protone.netModule.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.*
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import java.util.concurrent.TimeUnit

@Route(path = NetRouterPath.Main)
class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val binding by lazy { ActivityMainBinding.inflate(newLayoutInflater) }

    companion object {
        const val BASE_URL = "http://10.0.2.2:9102"
    }

    private val retrofitClient by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    data class Comment(val articleId: Int, val commentContent: String)

    class BaseResponse<T>(
        var success: Boolean = false,
        var data: T,
        var code: Int = -1,
        var message: String = ""
    )

    interface Api {
        @GET("/get/text")
        suspend fun getText(): BaseResponse<String>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.responseBody.movementMethod = ScrollingMovementMethod()

        ModelTestListHelper<suspend () -> Unit>()
            .add("getTextByURLConnection", getTextByURLConnection())
            .add("getTextBySocket", getTextBySocket())
            .add("getImageByURLConnection", getImageByURLConnection())
            .add("getImageByGlide", getImageByGlide())
            .add("getTextWithParamByURLConnection", getTextWithParamByURLConnection())
            .add("uploadFileByURLConnection", uploadFileByURLConnection())
            .add("getTextByOkHttp", getTextByOkHttp())
            .add("postCommentByOkHttp", postCommentByOkHttp())
            .add("postFileByOkHttp", postFileByOkHttp())
            .add("downloadFileByOkHttp", downloadFileByOkHttp())
            .add("getTextByRetrofit", getTextByRetrofit())
            .init(
                binding.requestList,
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false), 0
            ) {
                launchDefault {
                    runCatching {
                        it.invoke()
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }
    }

    private fun getTextByURLConnection(): suspend () -> Unit = {
        val url = URL("$BASE_URL/get/text")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 2000
        connection.connect()
        val responseCode = connection.responseCode
        val stream = connection.content as InputStream
        val bufferedReader = BufferedReader(InputStreamReader(stream))
        val content = bufferedReader.readText()
        launchMain {
            binding.code.text = responseCode.toString()
            binding.responseBody.text = content
        }
    }

    private fun getTextBySocket(): suspend () -> Unit = {
        val url = URL("$BASE_URL/get/text")
        val socket = Socket()
        socket.connect(InetSocketAddress(url.host, url.port))
        val stream = socket.getInputStream()
        val bufferedReader = BufferedReader(InputStreamReader(stream))
        val content = bufferedReader.readText()
        launchMain {
//            binding.code.text = responseCode.toString()
            binding.responseBody.text = content
        }
    }

    private fun getImageByURLConnection(): suspend () -> Unit = {
        val url = URL("$BASE_URL/imgs/1.png")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"
        urlConnection.connect()
        val code = urlConnection.responseCode
        val inputStream = urlConnection.content as InputStream

        //解析Bitmap
        val outputStream = ByteArrayOutputStream(8 * 1024)
        val byteArray = ByteArray(8 * 1024)
        var byte = inputStream.read(byteArray)
        while (byte > 0) {
            outputStream.write(byteArray, 0, byte)
            byte = inputStream.read(byteArray)
        }
        val toByteArray = outputStream.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(toByteArray, 0, toByteArray.size)
//            val bitmap = BitmapFactory.decodeStream(inputStream)

        launchMain {
            binding.code.text = code.toString()
            binding.image.setImageBitmap(bitmap)
        }
    }

    private fun getImageByGlide(): suspend () -> Unit = {
        launchMain {
            Image.load("$BASE_URL/imgs/1.png").with(this@MainActivity).into(binding.image)
        }
    }

    private fun getTextWithParamByURLConnection(): suspend () -> Unit = {
        //keyword 关键字
        //page 页码
        //order 0 顺序 1 逆序
        //http://10.0.2.2:9102/get/param
        val url = URL("$BASE_URL/get/param?keyword=asd&page=1&order=0")
        val httpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.requestMethod = "GET"
        httpURLConnection.connect()
        val responseCode = httpURLConnection.responseCode
        val inputStream = httpURLConnection.content as InputStream
        val reader = BufferedReader(InputStreamReader(inputStream))
        val content = reader.readText()
        launchMain {
            binding.code.text = responseCode.toString()
            binding.responseBody.text = content
        }
    }

    private fun uploadFileByURLConnection(): suspend () -> Unit = {
        //keyword 关键字
        //page 页码
        //order 0 顺序 1 逆序
        //http://10.0.2.2:9102/get/param
        val url = URL("$BASE_URL/file/upload")
        val httpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.requestMethod = "GET"
        httpURLConnection.connect()
        val responseCode = httpURLConnection.responseCode
        val inputStream = httpURLConnection.content as InputStream
        val reader = BufferedReader(InputStreamReader(inputStream))
        val content = reader.readText()
        launchMain {
            binding.code.text = responseCode.toString()
            binding.responseBody.text = content
        }
    }

    private fun getTextByOkHttp(): suspend () -> Unit = {
        val client = OkHttpClient.Builder()
            .connectTimeout(3000L, TimeUnit.MILLISECONDS)
            .build()
        val request = Request.Builder()
            .get()
            .url("$BASE_URL/get/text")
            .build()
        val newCall = client.newCall(request)
        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val code = response.code.toString()
                val body = response.body?.string()
                launchMain {
                    binding.code.text = code
                    binding.responseBody.text = body
                }
            }

        })
    }

    private fun postCommentByOkHttp(): suspend () -> Unit = {
//        /post/comment
        OkHttpClient.Builder()
            .connectTimeout(3000L, TimeUnit.MILLISECONDS)
            .build().newCall(
                Request.Builder().post(
                    Gson().toJson(Comment(123, "2333"))
                        .toRequestBody("application/json".toMediaType())
                ).url("$BASE_URL/post/comment").build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val code = response.code.toString()
                    val body = response.body?.string()
                    launchMain {
                        binding.code.text = code
                        binding.responseBody.text = body
                    }
                }

            })
    }

    private fun postFileByOkHttp(): suspend () -> Unit = {
//       /file/upload
        OkHttpClient.Builder()
            .connectTimeout(3000L, TimeUnit.MILLISECONDS)
            .build().newCall(
                Request.Builder().post(
                    //一个文件就调用一次addFormDataPart
                    MultipartBody.Builder().addFormDataPart(
                        "file",
                        "IMG_20220611_140412.jpg",
                        File(
                            Environment.getExternalStorageDirectory().path +
                                    "/${Environment.DIRECTORY_DCIM}/Camera/IMG_20220611_140412.jpg"
                        )
                            .asRequestBody("image/png".toMediaType())
                    ).build()
                ).url("$BASE_URL/file/upload").build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val code = response.code.toString()
                    val body = response.body?.string()
                    launchMain {
                        binding.code.text = code
                        binding.responseBody.text = body
                    }
                }

            })
    }

    private fun downloadFileByOkHttp(): suspend () -> Unit = {
        OkHttpClient.Builder()
            .connectTimeout(3000L, TimeUnit.MILLISECONDS)
            .build().newCall(
                Request.Builder().get().url("$BASE_URL/download/0").build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val code = response.code.toString()
                    val body = response.body?.byteStream()
                    val file =
                        File("${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DOWNLOADS}/2019-12-04_171830.png")
                    if (file.exists()) {
                        file.delete()
                    }
                    file.createNewFile()
                    val fileOutputStream = FileOutputStream(file)
                    val readBytes = body?.readBytes()
                    fileOutputStream.write(readBytes)
                    launchMain {
                        binding.code.text = code
                        binding.responseBody.text = "1"
                    }
                }

            })
    }

    private fun getTextByRetrofit(): suspend () -> Unit = {
        val create = retrofitClient.create(Api::class.java)
        val response = create.getText()
        val code = response.code
        val body = response.data
        launchMain {
            binding.code.text = code.toString()
            binding.responseBody.text = body
        }
    }

}