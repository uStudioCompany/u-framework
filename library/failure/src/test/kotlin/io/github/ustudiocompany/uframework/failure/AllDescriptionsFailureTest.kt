package io.github.ustudiocompany.uframework.failure

import io.github.ustudiocompany.uframework.failure.Failure.Cause
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly

internal class AllDescriptionsFailureTest : UnitTest() {

    init {

        "Tee `allDescriptions` function" - {
            withData(
                nameFn = { (failure, _) -> failure.toString() },
                listOf(
                    failure(code = CODE_1, description = DES_1) to listOf(DES_1),
                    failure(
                        code = CODE_2,
                        description = DES_2,
                        cause = failure(
                            code = CODE_1,
                            description = DES_1
                        ),
                    ) to listOf(DES_2, DES_1),
                    failure(
                        code = CODE_3,
                        description = DES_3,
                        cause = failure(
                            code = CODE_2,
                            description = DES_2,
                            cause = failure(
                                code = CODE_1,
                                description = DES_1
                            ),
                        )
                    ) to listOf(DES_3, DES_2, DES_1),
                )
            ) { (failure, expected) ->
                failure.allDescriptions() shouldContainExactly expected
            }
        }
    }

    private fun failure(code: String, description: String): Failure = Root(code = code, description = description)
    private fun failure(code: String, description: String, cause: Failure): Failure =
        Child(code = code, cause = Cause.Failure(cause), description = description)

    private companion object {
        private const val CODE_1 = "CODE-1"
        private const val CODE_2 = "CODE-2"
        private const val CODE_3 = "CODE-2"

        private const val DES_1 = "description-1"
        private const val DES_2 = "description-2"
        private const val DES_3 = "description-3"
    }

    private data class Root(
        override val code: String,
        override val description: String
    ) : Failure

    private data class Child(
        override val code: String,
        override val cause: Cause,
        override val description: String
    ) : Failure
}
