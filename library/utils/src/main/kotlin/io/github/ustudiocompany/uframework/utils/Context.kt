package io.github.ustudiocompany.uframework.utils

context(T)
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> fromContext(): T = this@T

public inline fun <T, R> withContext(receiver: T, block: T.() -> R): R = with(receiver, block)
