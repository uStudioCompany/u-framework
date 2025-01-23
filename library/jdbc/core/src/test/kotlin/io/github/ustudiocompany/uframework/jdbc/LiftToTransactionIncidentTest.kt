package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldBeIncident
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

internal class LiftToTransactionIncidentTest : UnitTest() {

    init {

        "The  extension function `liftToTransactionIncident` of the `JDBCResult` type" - {

            "when a variable has the success value" - {
                val original: JDBCResult<String> = createResult(ORIGINAL_VALUE.asSuccess())
                val result: TransactionResult<String, Unit> = original.liftToTransactionIncident()

                "then this function should return a result with the success value" {
                    result shouldBeSuccess ORIGINAL_VALUE
                }
            }

            "when a variable has the failure value" - {
                val original: JDBCResult<String> = createResult(ERROR.asFailure())
                val result: TransactionResult<String, Unit> = original.liftToTransactionIncident()

                "then this function should return an incident" {
                    val incident = result.shouldBeIncident()
                    incident shouldBe ERROR
                }
            }
        }
    }

    private fun <ValueT> createResult(value: JDBCResult<ValueT>): JDBCResult<ValueT> = value

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private val ERROR = JDBCError("error")
    }
}
