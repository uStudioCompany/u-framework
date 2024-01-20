package io.github.ustudiocompany.uframework.failure

public inline fun <reified T> typeOf(): TypeOf<T> = TypeOf(T::class.java)

@JvmInline
public value class TypeOf<T>(private val get: Class<T>) {
    public val name: String
        get() = get.simpleName
}
