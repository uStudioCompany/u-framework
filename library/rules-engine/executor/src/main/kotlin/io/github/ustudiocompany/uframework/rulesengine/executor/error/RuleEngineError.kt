package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.allDetails
import io.github.ustudiocompany.uframework.failure.fullCode
import io.github.ustudiocompany.uframework.failure.fullDescription

public sealed class RuleEngineError : Failure {

    override fun toString(): String = buildString {
        append("code: `")
        append(fullCode())
        append("`, description: `")
        append(fullDescription())

        val failureCause = cause
        if (failureCause is Failure.Cause.Exception) {
            append("`, cause: `")
            append(failureCause.get.message)
            append("`")
        }

        append("`, details: `")
        append(allDetails())
        append("`.")
    }
}
