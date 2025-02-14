package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.matcher.shouldBeError
import io.github.airflux.commons.types.fail.matcher.shouldBeException
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.row.extractor.DataExtractor
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class ResultRowMapperTest : UnitTest() {

    init {
        "The function `resultRowMapper`" - {

            "when the function of getting user id returns the success value" - {
                val mapper: ResultRowMapper<User, User.Error, JDBCError> = resultRowMapper { index, row ->
                    val (id) = getIdAsSuccess()
                    User.create(id).mapFailure { error -> Fail.Error(error) }
                }

                "then the mapper should return the user with the id" {
                    val result = mapper.invoke(1, DummyResultRow())
                    result.shouldBeSuccess()
                    result.value.id shouldBe USER_ID
                }
            }

            "when the function of getting user id returns the error" - {
                val mapper: ResultRowMapper<User, User.Error, JDBCError> = resultRowMapper { index, row ->
                    val (id) = getIdAsError()
                    User.create(id).mapFailure { error -> Fail.Error(error) }
                }

                "then the mapper should return the error as exception" {
                    val result = mapper.invoke(1, DummyResultRow())
                    result.shouldBeFailure()
                    val failure = result.cause
                    failure.shouldBeException()
                    val exceptionValue = failure.value
                    exceptionValue shouldBe ERROR
                }
            }

            "when the error occurs while creating a domain entity" - {
                val mapper: ResultRowMapper<User, User.Error, JDBCError> = resultRowMapper { index, row ->
                    User.create("").mapFailure { error -> Fail.Error(error) }
                }

                "then the mapper should return the error as business error" {
                    val result = mapper.invoke(1, DummyResultRow())
                    result.shouldBeFailure()
                    val failure = result.cause
                    failure.shouldBeError()
                    val error = failure.value
                    error shouldBe User.Error
                }
            }
        }
    }

    private class DummyResultRow : ResultRow {
        override fun <ValueT> extract(
            index: Int,
            expectedColumnTypes: ResultRow.ColumnTypes,
            block: DataExtractor<ValueT>
        ): JDBCResult<ValueT> {
            error("Not implemented")
        }
    }

    private class User private constructor(val id: String) {
        companion object {
            fun create(id: String): ResultK<User, Error> =
                if (id.isNotBlank())
                    User(id).asSuccess()
                else
                    Error.asFailure()
        }

        object Error
    }

    private companion object {

        private const val USER_ID = "user-id"
        private val ERROR = JDBCError("error")

        private fun getIdAsSuccess(): JDBCResult<String> = USER_ID.asSuccess()
        private fun getIdAsError(): JDBCResult<String> = ERROR.asFailure()
    }
}
