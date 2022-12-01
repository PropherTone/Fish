package com.protone.common.baseType

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun CoroutineScope.launchDefault(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(Dispatchers.Default, start, block)

fun CoroutineScope.launchIO(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(Dispatchers.IO, start, block)

fun CoroutineScope.launchMain(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(Dispatchers.Main, start, block)

suspend fun <T> withMainContext(block: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Main, block)

suspend fun <T> withIOContext(block: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.IO, block)

suspend fun <T> withDefaultContext(block: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Default, block)

@Suppress("ObjectLiteralToLambda")
suspend inline fun <T> Flow<T>.bufferCollect(crossinline action: suspend (value: T) -> Unit) {
    buffer().collect(object : FlowCollector<T> {
        override suspend fun emit(value: T) = action(value)
    })
}