package io.github.ustudiocompany.uframework.retry

import java.time.Duration
import kotlin.random.Random.Default.nextLong

public sealed class Jitter {
    public abstract fun get(currentDelayTime: Duration): Duration

    public object None : Jitter() {
        override fun get(currentDelayTime: Duration): Duration = Duration.ZERO
    }

    public class FixedRange(private val min: Long = 0, private val max: Long) : Jitter() {
        override fun get(currentDelayTime: Duration): Duration = Duration.ofMillis(nextLong(min, max))
    }

    public class DynamicRange(private val min: Long = 0) : Jitter() {
        override fun get(currentDelayTime: Duration): Duration =
            Duration.ofMillis(nextLong(min, currentDelayTime.toMillis() / 2))
    }
}
