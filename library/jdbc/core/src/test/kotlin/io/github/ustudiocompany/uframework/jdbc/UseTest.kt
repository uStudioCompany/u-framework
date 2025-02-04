package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.BiFailureResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldContainExceptionInstance
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class UseTest : UnitTest() {

    init {

        "The  extension function `use` of the `JDBCResult` type" - {

            "when a variable has an resource" - {
                val resource: JDBCResult<DummyResource> = createResult(DummyResource().asSuccess())
                val result: BiFailureResultK<String, Unit, JDBCError> = resource.use {
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
                val result: BiFailureResultK<String, Unit, JDBCError> = resource.use {
                    it.doSomething().asSuccess()
                }

                "then this function should return an exception" {
                    val exception = result.shouldContainExceptionInstance()
                    exception shouldBe ERROR
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
