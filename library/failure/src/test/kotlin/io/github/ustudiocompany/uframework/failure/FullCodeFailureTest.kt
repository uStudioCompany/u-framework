package io.github.ustudiocompany.uframework.failure

import io.github.ustudiocompany.uframework.failure.Failure.Cause
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class FullCodeFailureTest : UnitTest() {

    init {

        "Tee `fullCode` function" - {
            withData(
                nameFn = { (failure, _) -> failure.toString() },
                listOf(
                    failure(CODE_1) to CODE_1,
                    failure(CODE_2, Root(CODE_1)) to "$CODE_2-$CODE_1",
                    failure(CODE_3, failure(CODE_2, Root(CODE_1))) to "$CODE_3-$CODE_2-$CODE_1"
                )
            ) { (failure, expected) ->
                failure.fullCode(delimiter = "-") shouldBe expected
            }
        }
    }

    private fun failure(code: String): Failure = Root(code)
    private fun failure(code: String, cause: Failure): Failure = Child(code = code, cause = Cause.Failure(cause))

    private companion object {
        private const val CODE_1 = "CODE-1"
        private const val CODE_2 = "CODE-2"
        private const val CODE_3 = "CODE-3"
    }

    private data class Root(override val code: String) : Failure
    private data class Child(override val code: String, override val cause: Cause) : Failure
}
