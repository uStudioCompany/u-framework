package io.github.ustudiocompany.uframework.failure

import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class ExceptionOrNullFailureTest : UnitTest() {

    init {

        "Tee `exceptionOrNull` function" - {
            withData(
                nameFn = { (failure, _) -> failure.toString() },
                listOf(
                    failure(code = CODE_1) to null,
                    failure(code = CODE_1, EXCEPTION) to EXCEPTION,
                    failure(CODE_2, failure(CODE_1)) to null,
                    failure(CODE_2, failure(CODE_1, EXCEPTION)) to EXCEPTION,
                    failure(CODE_3, failure(CODE_2, failure(CODE_1))) to null,
                    failure(CODE_3, failure(CODE_2, failure(CODE_1, EXCEPTION))) to EXCEPTION,
                )
            ) { (failure, expected) ->
                failure.exceptionOrNull() shouldBe expected
            }
        }
    }

    private fun failure(code: String, exception: Throwable? = null): Failure {
        val cause = if (exception != null) Cause.Exception(exception) else Cause.None
        return Root(code = code, cause = cause)
    }

    private fun failure(code: String, cause: Failure): Failure = Child(code = code, cause = Cause.Failure(cause))

    private companion object {
        private val EXCEPTION = Exception("EXCEPTION")
        private const val CODE_1 = "CODE-1"
        private const val CODE_2 = "CODE-2"
        private const val CODE_3 = "CODE-3"
    }

    private data class Root(override val code: String, override val cause: Cause) : Failure
    private data class Child(override val code: String, override val cause: Cause) : Failure
}
