package io.github.ustudiocompany.uframework.retry

import io.github.airflux.commons.types.resultk.Failure
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success

public inline fun <T, F> retry(
    scope: RetryScope,
    predicate: (F) -> Boolean,
    block: () -> ResultK<T, F>
): ResultK<T, F> {
    while (true) {
        when (val result = block()) {
            is Success -> return result
            is Failure -> if (scope.next() && predicate(result.cause))
                Thread.sleep(scope.delay.toMillis())
            else
                return result
        }
    }
}
