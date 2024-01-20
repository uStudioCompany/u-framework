package io.github.ustudiocompany.uframework.messaging.listener

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

@JvmInline
internal value class Shutdown(private val job: Job) {
    val isShutdown: Boolean
        get() = !job.isActive
}

internal inline fun CoroutineScope.withShutdown(block: Shutdown.() -> Unit): Unit = with(Shutdown(job), block)

internal val CoroutineScope.job: Job
    get() = coroutineContext[Job]!!
