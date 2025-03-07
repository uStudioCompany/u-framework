package io.github.ustudiocompany.uframework.failure

import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainOnly

internal class AllDetailsFailureTest : UnitTest() {

    init {

        "Tee `allDetails` function" - {
            withData(
                nameFn = { (failure, _) -> failure.toString() },
                listOf(
                    failure(code = CODE_1) to Details.NONE,
                    failure(
                        code = CODE_1,
                        details = Details.of(KEY_1 to VALUE_1)
                    ) to Details.of(KEY_1 to VALUE_1),
                    failure(code = CODE_2, cause = failure(code = CODE_1)) to Details.NONE,
                    failure(
                        code = CODE_2,
                        cause = failure(CODE_1),
                        details = Details.of(KEY_1 to VALUE_1)
                    ) to Details.of(KEY_1 to VALUE_1),
                    failure(
                        code = CODE_2,
                        cause = failure(
                            code = CODE_1,
                            details = Details.of(KEY_1 to VALUE_1)
                        )
                    ) to Details.of(KEY_1 to VALUE_1),
                    failure(
                        code = CODE_2,
                        cause = failure(
                            code = CODE_1,
                            details = Details.of(KEY_1 to VALUE_1)
                        ),
                        details = Details.of(KEY_2 to VALUE_2)
                    ) to Details.of(KEY_1 to VALUE_1, KEY_2 to VALUE_2)
                )
            ) { (failure, expected) ->
                failure.allDetails() shouldContainOnly expected
            }
        }
    }

    private fun failure(code: String, details: Details = Details.NONE): Failure =
        Root(code = code, details = details)

    private fun failure(code: String, cause: Failure, details: Details = Details.NONE): Failure =
        Child(code = code, cause = Failure.Cause.Failure(cause), details = details)

    private companion object {
        private const val CODE_1 = "CODE-1"
        private const val CODE_2 = "CODE-2"

        private const val KEY_1 = "key-1"
        private const val KEY_2 = "key-2"
        private const val VALUE_1 = "value-1"
        private const val VALUE_2 = "value-2"
    }

    private data class Root(
        override val code: String,
        override val details: Details = Details.NONE
    ) : Failure

    private data class Child(
        override val code: String,
        override val cause: Failure.Cause,
        override val details: Details = Details.NONE
    ) : Failure
}
