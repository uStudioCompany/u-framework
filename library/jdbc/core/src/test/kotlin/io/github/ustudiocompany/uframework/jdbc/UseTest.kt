package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldBeException
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

internal class UseTest : UnitTest() {

    init {

        "The  extension function `use` of the `JDBCResult` type" - {

            "when a variable has an resource" - {
                val resource: JDBCResult<DummyResource> = createResult(DummyResource().asSuccess())
                val result: TransactionResult<String, Unit> = resource.use {
                    it.doSomething().asSuccess()
                }

                "then this function should return some result" {
                    result shouldBeSuccess ORIGINAL_VALUE
                }

                "then this function should close the resource" {
                    resource.shouldBeSuccess()
                    resource.value.isClosed shouldBe true
                }
            }

            "when a variable has the failure value" - {
                val resource: JDBCResult<DummyResource> = createResult(ERROR.asFailure())
                val result: TransactionResult<String, Unit> = resource.use {
                    it.doSomething().asSuccess()
                }

                "then this function should return an exception" {
                    val exceptionValue = result.shouldBeException()
                    exceptionValue shouldBe ERROR
                }
            }
        }
    }

    private fun <ValueT : AutoCloseable> createResult(value: JDBCResult<ValueT>): JDBCResult<ValueT> = value

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private val ERROR = JDBCError("error")
    }

    private class DummyResource : AutoCloseable {
        var isClosed: Boolean = false
            private set

        override fun close() {
            isClosed = true
        }

        fun doSomething(): String = ORIGINAL_VALUE
    }
}
