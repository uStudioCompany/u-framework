package io.github.ustudiocompany.uframework.retry

import io.github.airflux.commons.types.result.Result

public inline fun <T, F> retry(
    scope: RetryScope,
    predicate: (F) -> Boolean,
    block: () -> Result<T, F>
): Result<T, F> {
    while (true) {
        when (val result = block()) {
            is Result.Success -> return result
            is Result.Failure -> if (scope.next() && predicate(result.cause))
                Thread.sleep(scope.delay.toMillis())
            else
                return result
        }
    }
}
