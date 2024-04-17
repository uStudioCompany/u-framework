package io.github.ustudiocompany.uframework.utils

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.success
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.TypeFailure
import io.github.ustudiocompany.uframework.failure.TypeOf
import io.github.ustudiocompany.uframework.failure.typeOf

public abstract class EnumElementProvider<T>(private val info: EnumInfo<T>) where T : Enum<T>,
                                                                                  T : EnumElementProvider.Key {

    public fun orNull(value: String): T? = info.values.find { value.equals(it.key, ignoreCase = true) }

    public fun of(value: String): Result<T, Errors.UnexpectedValue<T>> =
        orNull(value)
            ?.success()
            ?: Errors.UnexpectedValue(type = info.type, value = value, expected = info.values).failure()

    public interface Key {
        public val key: String
    }

    public class EnumInfo<T>(public val type: TypeOf<T>, public val values: Array<T>) where T : Enum<T>,
                                                                                            T : Key

    public sealed class Errors<T> : TypeFailure<T>
        where T : Enum<T>,
              T : Key {

        public class UnexpectedValue<T>(override val type: TypeOf<T>, value: String, expected: Array<T>) : Errors<T>()
            where T : Enum<T>,
                  T : Key {
            override val number: String = "1"
            override val description: String =
                "Unexpected value. Actual value: `$value`, expected values: ${expected.joinToString { it.key }}"

            override val details: Failure.Details = Failure.Details.of(
                ACTUAL_VALUE_DETAIL_KEY to value,
                EXPECTED_VALUES_DETAIL_KEY to expected.joinToString { it.key }
            )

            private companion object {
                private const val ACTUAL_VALUE_DETAIL_KEY = "actual-value"
                private const val EXPECTED_VALUES_DETAIL_KEY = "expected-values"
            }
        }
    }

    public companion object {

        public inline fun <reified T> info(): EnumInfo<T> where T : Enum<T>,
                                                                T : Key =
            EnumInfo(type = typeOf(), values = enumValues())
    }
}
