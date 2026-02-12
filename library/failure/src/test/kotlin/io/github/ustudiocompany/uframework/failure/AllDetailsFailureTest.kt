package io.github.ustudiocompany.uframework.failure

import io.github.ustudiocompany.uframework.failure.Failure.Cause
import io.github.ustudiocompany.uframework.failure.Failure.Details
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.EXCEPTION_CAUSE
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.EXCEPTION_MESSAGE
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.EXCEPTION_STACKTRACE
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainOnly

internal class AllDetailsFailureTest : UnitTest() {

    init {

        "Tee `allDetails` function" - {
            withData(
                nameFn = { (failure, _) -> failure.toString() },
                listOf(
                    failureRoot(code = CODE_1) to
                        Details.NONE,
                    failureRootException(details = Details.of(KEY_1 to VALUE_1)) to
                        Details.of(
                            KEY_1 to VALUE_1,
                            EXCEPTION_CAUSE to VALUE_3,
                            EXCEPTION_MESSAGE to VALUE_4,
                            EXCEPTION_STACKTRACE to VALUE_5
                        ),
                    failureChild(code = CODE_2, cause = failureRoot(code = CODE_1)) to
                        Details.NONE,
                    failureChild(
                        code = CODE_2,
                        cause = failureRoot(CODE_1),
                        details = Details.of(KEY_1 to VALUE_1)
                    ) to
                        Details.of(KEY_1 to VALUE_1),
                    failureChild(
                        code = CODE_2,
                        cause = failureRoot(
                            code = CODE_1,
                            details = Details.of(KEY_1 to VALUE_1)
                        )
                    ) to
                        Details.of(KEY_1 to VALUE_1),
                    failureChild(
                        code = CODE_2,
                        cause = failureRoot(
                            code = CODE_1,
                            details = Details.of(KEY_1 to VALUE_1)
                        ),
                        details = Details.of(KEY_2 to VALUE_2)
                    ) to
                        Details.of(KEY_1 to VALUE_1, KEY_2 to VALUE_2)
                )
            ) { (failure, expected) ->
                failure.allDetails() shouldContainOnly expected
            }
        }
    }

    private fun failureRoot(code: String, details: Details = Details.NONE): Failure =
        Root(code = code, details = details)

    private fun failureRootException(code: String = CODE_1, details: Details = Details.NONE): Failure =
        RootException(code = code, details = details)

    private fun failureChild(code: String, cause: Failure, details: Details = Details.NONE): Failure =
        Child(code = code, cause = Cause.Failure(cause), details = details)

    private companion object {
        private const val CODE_1 = "CODE-1"
        private const val CODE_2 = "CODE-2"

        private const val KEY_1 = "key-1"
        private const val KEY_2 = "key-2"
        private const val VALUE_1 = "value-1"
        private const val VALUE_2 = "value-2"
        private const val VALUE_3 = "null"
        private const val VALUE_4 = "value-4"
        private val testException = Exception(VALUE_4)
        private val VALUE_5 = testException.stackTraceToString()
    }

    private data class Root(
        override val code: String,
        override val details: Details = Details.NONE,
    ) : Failure

    private data class RootException(
        override val code: String,
        override val details: Details = Details.NONE,
        override val cause: Cause = Cause.Exception(testException)
    ) : Failure

    private data class Child(
        override val code: String,
        override val cause: Cause,
        override val details: Details = Details.NONE
    ) : Failure
}
