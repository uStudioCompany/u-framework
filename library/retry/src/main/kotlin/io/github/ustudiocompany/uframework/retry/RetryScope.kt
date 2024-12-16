package io.github.ustudiocompany.uframework.retry

import java.time.Duration

public class RetryScope(private val backOffPolicy: BackOffPolicy, private val attemptsPolicy: AttemptsPolicy) {
    public var delay: Duration = backOffPolicy.min
        private set

    public constructor(attemptsPolicy: AttemptsPolicy) : this(BackOffPolicy.default(), attemptsPolicy)

    public fun next(): Boolean {
        delay = backOffPolicy.computeNextDelay(delay)
        return attemptsPolicy.next()
    }

    public companion object {
        private const val COUNT_DEFAULT = 3

        public fun default(): RetryScope = RetryScope(
            backOffPolicy = BackOffPolicy.default(),
            attemptsPolicy = AttemptsPolicy.limited(COUNT_DEFAULT)
        )
    }
}
