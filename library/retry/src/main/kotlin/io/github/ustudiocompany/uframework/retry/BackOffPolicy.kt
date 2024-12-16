package io.github.ustudiocompany.uframework.retry

import java.time.Duration

public fun backOffPolicy(min: Duration, max: Duration, step: TimeStep): BackOffPolicy =
    BackOffPolicy(min = min, max = max, step = step)

public class BackOffPolicy internal constructor(
    public val min: Duration,
    private val max: Duration,
    private val step: TimeStep
) {

    public fun computeNextDelay(current: Duration): Duration {
        val newDelay = step.computeNext(current)
        return if (newDelay <= max) newDelay else max
    }

    public companion object {
        private val MIN_DELAY_DEFAULT: Duration = Duration.ofMillis(100)
        private val MAX_DELAY_DEFAULT: Duration = Duration.ofMinutes(1)
        private val STEP_DEFAULT: TimeStep = TimeStep.factor(1.5)

        public fun default(): BackOffPolicy =
            BackOffPolicy(min = MIN_DELAY_DEFAULT, max = MAX_DELAY_DEFAULT, step = STEP_DEFAULT)
    }
}
