package com.protone.coroutine

import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.protone.common.baseType.bufferCollect
import com.protone.common.baseType.launchDefault
import com.protone.common.component.ModelTestListHelper
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.CoroutineRouterPath
import com.protone.common.utils.TAG
import com.protone.coroutine.databinding.CoroutineActivityLayoutBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.CoroutineContext

@Route(path = CoroutineRouterPath.Coroutine)
class CoroutineActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val binding by lazy { CoroutineActivityLayoutBinding.inflate(newLayoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ModelTestListHelper<() -> Unit>()
            .add("selectTest", selectTest())
            .add("coroutineSyncStressTest", coroutineSyncStressTest())
            .add("coContextDispatchToCurrentThread", coContextDispatchToCurrentThread())
            .add("coContextTest", coContextTest())
            .add("coroutineTest", coroutineTest())
            .init(
                binding.list,
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false),
                0
            ) {
                it.invoke()
            }
    }

    private fun selectTest(): () -> Unit = {
        launch {
            //哪个先执行完就取哪个
            val filterJob = launchDefault {
                val jobA = launchDefault {
                    delay(100)
                    Log.d(TAG, "selectTest: A")
                }
                val jobB = launchDefault {
                    delay(200)
                    Log.d(TAG, "selectTest: B")
                }
                select {
                    jobB.onJoin {
                        Log.d(TAG, "selectTest: B finish")
                    }
                    jobA.onJoin {
                        Log.d(TAG, "selectTest: A finish")
                    }
                }
            }
            //也可用于轮询Channel消息
            val msgJob = launchDefault {
                val channel = Channel<String>(Channel.UNLIMITED)
                launchDefault {
                    repeat(100) {
                        channel.trySend(it.toString())
                    }
                }
                while (isActive) {
                    select {
                        channel.onReceive {
                            Log.d(TAG, "selectTest: $it")
                        }
                    }
                }
            }

            filterJob.start()
            msgJob.start()
        }
    }

    private fun coroutineSyncStressTest(): () -> Unit = {
        val times = 100000
        launchDefault {
            launchDefault {
                val synchronizedJob = launchDefault {
                    var i = 0
                    val lock = ""
                    val start = System.currentTimeMillis()
                    launchDefault co@{
                        repeat(times) {
                            synchronized(lock) {
                                if (i >= 2 * times - 10) {
                                    Log.d(
                                        TAG,
                                        "coroutineSyncStressTest A $i: ${System.currentTimeMillis() - start}"
                                    )
                                    return@co
                                }
                                i++
                            }
                        }
                    }
                    launchDefault co@{
                        repeat(times) {
                            synchronized(lock) {
                                if (i >= 2 * times - 10) {
                                    Log.d(
                                        TAG,
                                        "coroutineSyncStressTest A $i: ${System.currentTimeMillis() - start}"
                                    )
                                    return@co
                                }
                                i++
                            }
                        }
                    }
                    launchDefault co@{
                        repeat(times) {
                            synchronized(lock) {
                                if (i >= 2 * times - 10) {
                                    Log.d(
                                        TAG,
                                        "coroutineSyncStressTest A $i: ${System.currentTimeMillis() - start}"
                                    )
                                    return@co
                                }
                                i++
                            }
                        }
                    }
                    launchDefault co@{
                        repeat(times) {
                            synchronized(lock) {
                                if (i >= 2 * times - 10) {
                                    Log.d(
                                        TAG,
                                        "coroutineSyncStressTest A $i: ${System.currentTimeMillis() - start}"
                                    )
                                    return@co
                                }
                                i++
                            }
                        }
                    }
                }
                val mutexJob = launchDefault {
                    var i = 0
                    val mutex = Mutex()
                    val start = System.currentTimeMillis()
                    launchDefault co@{
                        repeat(times) {
                            mutex.withLock {
                                if (i >= 2 * times - 10) {
                                    Log.d(
                                        TAG,
                                        "coroutineSyncStressTest B $i: ${System.currentTimeMillis() - start}"
                                    )
                                    return@co
                                }
                                i++
                            }
                        }
                    }
                    launchDefault co@{
                        repeat(times) {
                            mutex.withLock {
                                if (i >= 2 * times - 10) {
                                    Log.d(
                                        TAG,
                                        "coroutineSyncStressTest B $i: ${System.currentTimeMillis() - start}"
                                    )
                                    return@co
                                }
                                i++
                            }
                        }
                    }
                    launchDefault co@{
                        repeat(times) {
                            mutex.withLock {
                                if (i >= 2 * times - 10) {
                                    Log.d(
                                        TAG,
                                        "coroutineSyncStressTest B $i: ${System.currentTimeMillis() - start}"
                                    )
                                    return@co
                                }
                                i++
                            }
                        }
                    }
                    launchDefault co@{
                        repeat(times) {
                            mutex.withLock {
                                if (i >= 2 * times - 10) {
                                    Log.d(
                                        TAG,
                                        "coroutineSyncStressTest B $i: ${System.currentTimeMillis() - start}"
                                    )
                                    return@co
                                }
                                i++
                            }
                        }
                    }
                }
                val reentrantLockJob = launchDefault {
                    var i = 0
                    val reentrantLock = ReentrantLock()
                    val start = System.currentTimeMillis()
                    launchDefault co@{
                        repeat(times) {
                            reentrantLock.lock()
                            if (i >= 2 * times - 10) {
                                Log.d(
                                    TAG,
                                    "coroutineSyncStressTest C $i: ${System.currentTimeMillis() - start}"
                                )
                                reentrantLock.unlock()
                                return@co
                            }
                            i++
                            reentrantLock.unlock()
                        }
                    }
                    launchDefault co@{
                        repeat(times) {
                            reentrantLock.lock()
                            if (i >= 2 * times - 10) {
                                Log.d(
                                    TAG,
                                    "coroutineSyncStressTest C $i: ${System.currentTimeMillis() - start}"
                                )
                                reentrantLock.unlock()
                                return@co
                            }
                            i++
                            reentrantLock.unlock()
                        }
                    }
                    launchDefault co@{
                        repeat(times) {
                            reentrantLock.lock()
                            if (i >= 2 * times - 10) {
                                Log.d(
                                    TAG,
                                    "coroutineSyncStressTest C $i: ${System.currentTimeMillis() - start}"
                                )
                                reentrantLock.unlock()
                                return@co
                            }
                            i++
                            reentrantLock.unlock()
                        }
                    }
                    launchDefault co@{
                        repeat(times) {
                            reentrantLock.lock()
                            if (i >= 2 * times - 10) {
                                Log.d(
                                    TAG,
                                    "coroutineSyncStressTest C $i: ${System.currentTimeMillis() - start}"
                                )
                                reentrantLock.unlock()
                                return@co
                            }
                            i++
                            reentrantLock.unlock()
                        }
                    }
                }
                synchronizedJob.start()
                mutexJob.start()
                reentrantLockJob.start()
            }
        }
    }

    private fun coContextDispatchToCurrentThread(): () -> Unit = {
        val handlerThread = HandlerThread("thread")
        val handler = handlerThread.run {
            start()
            Handler(this.looper)
        }
        val dispatcher = handler.asCoroutineDispatcher()
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        launch(dispatcher) {
            val currentThread = Thread.currentThread()
            Log.d(TAG, "coContextDispatchToCurrentThread: $currentThread")
        }
    }

    private fun coContextTest(): () -> Unit = {
        val job = Job()
        launch(job.plus(Dispatchers.Default)) {
            Log.d(TAG, "coContextTest: ${Thread.currentThread()}")
        }
        fun launchWithCatch(
            coroutineContext: CoroutineContext,
            coroutineStart: CoroutineStart = CoroutineStart.DEFAULT,
            onError: (CoroutineContext, Throwable) -> Unit,
            block: suspend CoroutineScope.() -> Unit
        ) {
            launch(coroutineContext + CoroutineExceptionHandler { coContext, throwable ->
                onError(coContext, throwable)
            }, coroutineStart, block = block)
        }
        launchWithCatch(Dispatchers.IO, onError = { _, throwable ->
            throwable.printStackTrace()
        }) {
            val i = 1 / 0
            Log.d(TAG, "coContextTest: $i")
        }
    }

    private fun coroutineTest(): () -> Unit = {
        val job = launch(start = CoroutineStart.LAZY) {
            delay(500)
            Log.d(TAG, "coroutineTest: delay")
        }
        job.start()
        Log.d(TAG, "coroutineTest: start")
        job.cancel()
        Log.d(TAG, "coroutineTest: cancel")
    }

}