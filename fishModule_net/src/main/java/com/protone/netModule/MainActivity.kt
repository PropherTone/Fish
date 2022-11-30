package com.protone.netModule

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.protone.common.baseType.launchDefault
import com.protone.common.baseType.launchMain
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.NetRouterPath
import com.protone.common.utils.displayUtils.imageLoader.Image
import com.protone.netModule.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import java.io.*
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

@Route(path = NetRouterPath.Main)
class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val binding by lazy { ActivityMainBinding.inflate(newLayoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.responseBody.movementMethod = ScrollingMovementMethod()
        binding.button.setOnClickListener {
            getTextWithParamByURLConnection()
        }
    }

    private fun getTextByURLConnection() {
        launchDefault {
            val url = URL("http://10.0.2.2:9102/get/text")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
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
    }

    private fun getTextBySocket() {
        launchDefault {
            val url = URL("http://10.0.2.2:9102/get/text")
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
    }

    private fun getImageByURLConnection() {
        launchDefault {
            val url = URL("http://10.0.2.2:9102/imgs/1.png")
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
    }

    private fun getImageByGlide() {
        Image.load("http://10.0.2.2:9102/imgs/1.png").with(this).into(binding.image)
    }

    private fun getTextWithParamByURLConnection() {
        launchDefault {
            //keyword 关键字
            //page 页码
            //order 0 顺序 1 逆序
            //http://10.0.2.2:9102/get/param
            val url = URL("http://10.0.2.2:9102/get/param?keyword=asd&page=1&order=0")
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
    }

    private fun uploadFileByURLConnection() {
        launchDefault {
            //keyword 关键字
            //page 页码
            //order 0 顺序 1 逆序
            //http://10.0.2.2:9102/get/param
            val url = URL("http://10.0.2.2:9102/file/upload")
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
    }
}