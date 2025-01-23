package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldBeError
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

internal class LiftToTransactionErrorTest : UnitTest() {

    init {

        "The  extension function `liftToTransactionError` of the `ResultK` type" - {

            "when a variable has the success value" - {
                val original: ResultK<String, Errors> = createResult(ORIGINAL_VALUE.asSuccess())
                val result: TransactionResult<String, Errors> = original.liftToTransactionError()

                "then this function should return a result with the success value" {
                    result shouldBeSuccess ORIGINAL_VALUE
                }
            }

            "when a variable has the failure value" - {
                val original: ResultK<String, Errors> = createResult(Errors.Empty.asFailure())
                val result: TransactionResult<String, Errors> = original.liftToTransactionError()

                "then this function should return an error" {
                    val error = result.shouldBeError()
                    error shouldBe Errors.Empty
                }
            }
        }
    }

    private fun <ValueT, ErrorT> createResult(value: ResultK<ValueT, ErrorT>): ResultK<ValueT, ErrorT> = value

    companion object {
        private const val ORIGINAL_VALUE = "10"
    }

    private sealed interface Errors {
        data object Empty : Errors
    }
}
