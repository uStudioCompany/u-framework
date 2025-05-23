package io.github.ustudiocompany.uframework.rulesengine.feel.functions.common

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import org.camunda.feel.syntaxtree.Val
import org.camunda.feel.syntaxtree.ValFatalError
import kotlin.reflect.KClass

internal fun <T : Val> Val.asType(name: String, type: KClass<T>): ResultK<T, ValFatalError> =
    if (type.isInstance(this))
        @Suppress("UNCHECKED_CAST")
        (this as T).asSuccess()
    else {
        val expectedType = type.java.simpleName
        val actualType = this::class.java.simpleName
        ValFatalError("Invalid type of the parameter '$name'. Expected: $expectedType, but was: $actualType")
            .asFailure()
    }
