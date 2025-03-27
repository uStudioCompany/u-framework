package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ComputeNullableReferenceValueTest : UnitTest() {

    init {
        "when value is reference type" - {

            "when the source is not in the context" - {
                val context = Context.empty()

                "then the compute function should return a failure" {
                    val value = Value.Reference(
                        source = SOURCE,
                        path = path(result = null)
                    )
                    val result = value.computeOrNull(context)
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                }
            }

            "when the source is in the context" - {
                val context = Context(mapOf(SOURCE to DATA))

                "when the data does not contain values by path" - {

                    "then the compute function should return a failure" {
                        val value = Value.Reference(
                            source = SOURCE,
                            path = path(result = null)
                        )
                        val result = value.computeOrNull(context)
                        result.shouldBeSuccess()
                        result.value.shouldBeNull()
                    }
                }

                "when the data contains values by path" - {

                    "then the compute function should return a value" {
                        val value = Value.Reference(
                            source = SOURCE,
                            path = path(result = TEXT_VALUE)
                        )
                        val result = value.computeOrNull(context)
                        result shouldBeSuccess DataElement.Text(VALUE)
                    }
                }
            }
        }
    }

    companion object {
        private const val SOURCE_NAME = "input.body"
        private val SOURCE = Source(SOURCE_NAME)

        private const val KEY = "id"
        private const val VALUE = "value"
        private val TEXT_VALUE = DataElement.Text(VALUE)
        private val DATA = DataElement.Struct(
            mutableMapOf(KEY to TEXT_VALUE)
        )

        private const val PATH_VALUE = "$.id"
        private fun path(result: DataElement?) =
            object : Path {
                override val text: String = PATH_VALUE

                override fun searchIn(data: DataElement): ResultK<DataElement?, Path.SearchErrors> =
                    result.asSuccess()
            }
    }
}
