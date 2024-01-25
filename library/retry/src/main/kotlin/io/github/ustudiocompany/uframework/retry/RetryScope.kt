package io.github.ustudiocompany.uframework.retry

import java.time.Duration

public class RetryScope(private val backOffPolicy: BackOffPolicy, private val attemptsPolicy: AttemptsPolicy) {

    public constructor(attemptsPolicy: AttemptsPolicy) : this(BackOffPolicy.default(), attemptsPolicy)

    public var delay: Duration = backOffPolicy.min
        private set

    public fun next(): Boolean {
        delay = backOffPolicy.computeNextDelay(delay)
        return attemptsPolicy.next()
    }

    public companion object {

        public fun default(): RetryScope = RetryScope(
            backOffPolicy = BackOffPolicy.default(),
            attemptsPolicy = AttemptsPolicy.limited(COUNT_DEFAULT)
        )

        private const val COUNT_DEFAULT = 3
    }
}
