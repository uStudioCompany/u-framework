package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class DataSchemeBuilderTest : UnitTest() {

    init {

        "The extension function 'build' tor the `DataScheme` type" - {

            "when the data schema describes a structure" - {

                "when the structure is empty" - {
                    val dataScheme = DataScheme.Struct(properties = emptyList())

                    "then the builder should return a data" {
                        val result = dataScheme.build(CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe DataElement.Struct(properties = mutableMapOf())
                    }
                }

                "when the structure contains a single element" - {
                    val dataScheme = DataScheme.Struct(
                        properties = listOf(
                            DataScheme.Property.Element(
                                name = DATA_KEY_1,
                                value = Value.Literal(fact = DataElement.Text(DATA_VALUE_1))
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataScheme.build(CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe DataElement.Struct(
                            properties = mutableMapOf(
                                DATA_KEY_1 to DataElement.Text(DATA_VALUE_1)
                            )
                        )
                    }
                }

                "when the structure contains an array" - {
                    val dataScheme = DataScheme.Struct(
                        properties = listOf(
                            DataScheme.Property.Array(
                                name = DATA_KEY_1,
                                items = mutableListOf(
                                    DataScheme.Item.Element(
                                        Value.Literal(fact = DataElement.Text(ARRAY_ITEM_1))
                                    ),
                                    DataScheme.Item.Element(
                                        Value.Literal(fact = DataElement.Text(ARRAY_ITEM_2))
                                    )
                                )
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataScheme.build(CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe DataElement.Struct(
                            properties = mutableMapOf(
                                DATA_KEY_1 to DataElement.Array(
                                    mutableListOf(
                                        DataElement.Text(ARRAY_ITEM_1),
                                        DataElement.Text(ARRAY_ITEM_2)
                                    )
                                )
                            )
                        )
                    }
                }

                "when the structure contains a nested structure" - {
                    val dataScheme = DataScheme.Struct(
                        properties = listOf(
                            DataScheme.Property.Struct(
                                name = DATA_KEY_1,
                                properties = mutableListOf(
                                    DataScheme.Property.Element(
                                        name = DATA_KEY_2,
                                        value = Value.Literal(fact = DataElement.Text(DATA_VALUE_2))
                                    )
                                )
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataScheme.build(CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe DataElement.Struct(
                            properties = mutableMapOf(
                                DATA_KEY_1 to DataElement.Struct(
                                    properties = mutableMapOf(
                                        DATA_KEY_2 to DataElement.Text(DATA_VALUE_2)
                                    )
                                )
                            )
                        )
                    }
                }
            }

            "when the data schema describes an array" - {

                "when the array is empty" - {
                    val dataScheme = DataScheme.Array(items = emptyList())

                    "then the builder should return a data" {
                        val result = dataScheme.build(CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe DataElement.Array(mutableListOf())
                    }
                }

                "when the array contains an elements of a primitive type" - {
                    val dataScheme = DataScheme.Array(
                        items = mutableListOf(
                            DataScheme.Item.Element(
                                Value.Literal(fact = DataElement.Text(ARRAY_ITEM_1))
                            ),
                            DataScheme.Item.Element(
                                Value.Literal(fact = DataElement.Text(ARRAY_ITEM_2))
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataScheme.build(CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe DataElement.Array(
                            mutableListOf(
                                DataElement.Text(ARRAY_ITEM_1),
                                DataElement.Text(ARRAY_ITEM_2)
                            )
                        )
                    }
                }

                "when the array contains a structure" - {
                    val dataScheme = DataScheme.Array(
                        items = mutableListOf(
                            DataScheme.Item.Struct(
                                properties = mutableListOf(
                                    DataScheme.Property.Element(
                                        name = DATA_KEY_1,
                                        value = Value.Literal(fact = DataElement.Text(DATA_VALUE_1))
                                    )
                                )
                            ),
                            DataScheme.Item.Struct(
                                properties = mutableListOf(
                                    DataScheme.Property.Element(
                                        name = DATA_KEY_2,
                                        value = Value.Literal(fact = DataElement.Text(DATA_VALUE_2))
                                    )
                                )
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataScheme.build(CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe DataElement.Array(
                            mutableListOf(
                                DataElement.Struct(
                                    properties = mutableMapOf(
                                        DATA_KEY_1 to DataElement.Text(DATA_VALUE_1)
                                    )
                                ),
                                DataElement.Struct(
                                    properties = mutableMapOf(
                                        DATA_KEY_2 to DataElement.Text(DATA_VALUE_2)
                                    )
                                )
                            )
                        )
                    }
                }

                "when the array contains an array" - {
                    val dataScheme = DataScheme.Array(
                        items = mutableListOf(
                            DataScheme.Item.Array(
                                items = mutableListOf(
                                    DataScheme.Item.Element(
                                        value = Value.Literal(fact = DataElement.Text(ARRAY_ITEM_1))
                                    ),
                                    DataScheme.Item.Element(
                                        value = Value.Literal(fact = DataElement.Text(ARRAY_ITEM_2))
                                    )
                                )
                            ),
                            DataScheme.Item.Array(
                                items = mutableListOf(
                                    DataScheme.Item.Element(
                                        value = Value.Literal(fact = DataElement.Text(ARRAY_ITEM_3))
                                    ),
                                    DataScheme.Item.Element(
                                        value = Value.Literal(fact = DataElement.Text(ARRAY_ITEM_4))
                                    )
                                )
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataScheme.build(CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe DataElement.Array(
                            mutableListOf(
                                DataElement.Array(
                                    items = mutableListOf(
                                        DataElement.Text(ARRAY_ITEM_1),
                                        DataElement.Text(ARRAY_ITEM_2)
                                    )
                                ),
                                DataElement.Array(
                                    items = mutableListOf(
                                        DataElement.Text(ARRAY_ITEM_3),
                                        DataElement.Text(ARRAY_ITEM_4)
                                    )
                                )
                            )
                        )
                    }
                }
            }

            "when occurs an error" - {
                val dataScheme = DataScheme.Struct(
                    properties = listOf(
                        DataScheme.Property.Element(
                            name = DATA_KEY_1,
                            value = Value.Expression(EXPRESSION)
                        )
                    )
                )

                "then the builder should return a failure" {
                    val result = dataScheme.build(Context.Companion.empty())
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<DataBuildErrors>()
                }
            }
        }
    }

    private companion object {
        private val CONTEXT = Context.Companion.empty()

        private const val DATA_KEY_1 = "key-1"
        private const val DATA_VALUE_1 = "data-1"

        private const val DATA_KEY_2 = "key-2"
        private const val DATA_VALUE_2 = "data-2"

        private const val ARRAY_ITEM_1 = "item-1"
        private const val ARRAY_ITEM_2 = "item-2"
        private const val ARRAY_ITEM_3 = "item-3"
        private const val ARRAY_ITEM_4 = "item-4"

        private val EXPRESSION = object : FeelExpression {

            override val text: String
                get() = "a/0"

            override fun evaluate(
                context: Context
            ): ResultK<DataElement, FeelExpression.EvaluateError> =
                FeelExpression.EvaluateError(this).asFailure()
        }
    }
}
