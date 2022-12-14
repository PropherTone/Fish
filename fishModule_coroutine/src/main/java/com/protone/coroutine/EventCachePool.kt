package com.protone.coroutine

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.reflect.KClass

class EventCachePool<Event : Any>(
    private val lifecycleOwner: LifecycleOwner? = null,
    private val replay: Boolean = false,
    private val duration: Long
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    companion object {
        @JvmStatic
        fun <Event : Any> get(
            lifecycleOwner: LifecycleOwner? = null,
            replay: Boolean = false,
            duration: Long
        ) = EventCachePool<Event>(lifecycleOwner, replay, duration)
    }

    private val eventMap by lazy {
        mutableMapOf<KClass<out Event>, MutableList<Event>>()
    }

    private val job = launch(start = CoroutineStart.LAZY) {
        flow.collect {
            if (lifecycleOwner?.lifecycle?.currentState == Lifecycle.State.DESTROYED) {
                clear()
                return@collect
            }
            delay(duration)
            dispatch()
        }
    }

    private val flow = MutableSharedFlow<Event>(
        replay = if (replay) 1 else 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    suspend fun holdEvent(event: Event) {
        eventMap[event::class].also {
            if (it == null) eventMap[event::class] = mutableListOf()
            eventMap[event::class]?.add(event)
        }
        flow.emit(event)
    }

    private suspend fun dispatch() {
        block?.let {
            eventMap.forEach { (_, v) ->
                if (v.isNotEmpty()) {
                    it.invoke(v)
                    v.clear()
                }
            }
        }
    }

    private var block: (suspend (List<Event>) -> Unit)? = null
    fun handleEvent(block: suspend (List<Event>) -> Unit): EventCachePool<Event> {
        job.start()
        this.block = block
        return this
    }

    fun clear() {
        block = null
        job.cancel()
        eventMap.clear()
        cancel()
    }

}