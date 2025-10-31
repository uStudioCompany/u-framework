package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsMapOf
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class DataSchemaBuilderTest : UnitTest() {

    init {

        "The extension function 'build' tor the `DataSchema` type" - {

            "when the data schema describes a structure" - {

                "when the structure is empty" - {
                    val dataSchema = DataSchema.Struct(properties = emptyList())

                    "then the builder should return a data" {
                        val result = dataSchema.build(ENV_VARS, CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe JsonElement.Struct()
                    }
                }

                "when the structure contains a single element" - {
                    val dataSchema = DataSchema.Struct(
                        properties = listOf(
                            DataSchema.Property.Element(
                                name = DATA_KEY_1,
                                value = Value.Literal(fact = JsonElement.Text(DATA_VALUE_1))
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataSchema.build(ENV_VARS, CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe JsonElement.Struct(
                            DATA_KEY_1 to JsonElement.Text(DATA_VALUE_1)
                        )
                    }
                }

                "when the structure contains an array" - {
                    val dataSchema = DataSchema.Struct(
                        properties = listOf(
                            DataSchema.Property.Array(
                                name = DATA_KEY_1,
                                items = mutableListOf(
                                    DataSchema.Item.Element(
                                        Value.Literal(fact = JsonElement.Text(ARRAY_ITEM_1))
                                    ),
                                    DataSchema.Item.Element(
                                        Value.Literal(fact = JsonElement.Text(ARRAY_ITEM_2))
                                    )
                                )
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataSchema.build(ENV_VARS, CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe JsonElement.Struct(
                            DATA_KEY_1 to JsonElement.Array(
                                JsonElement.Text(ARRAY_ITEM_1),
                                JsonElement.Text(ARRAY_ITEM_2)
                            )
                        )
                    }
                }

                "when the structure contains a nested structure" - {
                    val dataSchema = DataSchema.Struct(
                        properties = listOf(
                            DataSchema.Property.Struct(
                                name = DATA_KEY_1,
                                properties = mutableListOf(
                                    DataSchema.Property.Element(
                                        name = DATA_KEY_2,
                                        value = Value.Literal(fact = JsonElement.Text(DATA_VALUE_2))
                                    )
                                )
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataSchema.build(ENV_VARS, CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe JsonElement.Struct(
                            DATA_KEY_1 to JsonElement.Struct(
                                DATA_KEY_2 to JsonElement.Text(DATA_VALUE_2)
                            )
                        )
                    }
                }
            }

            "when the data schema describes an array" - {

                "when the array is empty" - {
                    val dataSchema = DataSchema.Array(items = emptyList())

                    "then the builder should return a data" {
                        val result = dataSchema.build(ENV_VARS, CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe JsonElement.Array()
                    }
                }

                "when the array contains an elements of a primitive type" - {
                    val dataSchema = DataSchema.Array(
                        items = mutableListOf(
                            DataSchema.Item.Element(
                                Value.Literal(fact = JsonElement.Text(ARRAY_ITEM_1))
                            ),
                            DataSchema.Item.Element(
                                Value.Literal(fact = JsonElement.Text(ARRAY_ITEM_2))
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataSchema.build(ENV_VARS, CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe JsonElement.Array(
                            JsonElement.Text(ARRAY_ITEM_1),
                            JsonElement.Text(ARRAY_ITEM_2)
                        )
                    }
                }

                "when the array contains a structure" - {
                    val dataSchema = DataSchema.Array(
                        items = mutableListOf(
                            DataSchema.Item.Struct(
                                properties = mutableListOf(
                                    DataSchema.Property.Element(
                                        name = DATA_KEY_1,
                                        value = Value.Literal(fact = JsonElement.Text(DATA_VALUE_1))
                                    )
                                )
                            ),
                            DataSchema.Item.Struct(
                                properties = mutableListOf(
                                    DataSchema.Property.Element(
                                        name = DATA_KEY_2,
                                        value = Value.Literal(fact = JsonElement.Text(DATA_VALUE_2))
                                    )
                                )
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataSchema.build(ENV_VARS, CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe JsonElement.Array(
                            JsonElement.Struct(DATA_KEY_1 to JsonElement.Text(DATA_VALUE_1)),
                            JsonElement.Struct(DATA_KEY_2 to JsonElement.Text(DATA_VALUE_2))
                        )
                    }
                }

                "when the array contains an array" - {
                    val dataSchema = DataSchema.Array(
                        items = mutableListOf(
                            DataSchema.Item.Array(
                                items = mutableListOf(
                                    DataSchema.Item.Element(
                                        value = Value.Literal(fact = JsonElement.Text(ARRAY_ITEM_1))
                                    ),
                                    DataSchema.Item.Element(
                                        value = Value.Literal(fact = JsonElement.Text(ARRAY_ITEM_2))
                                    )
                                )
                            ),
                            DataSchema.Item.Array(
                                items = mutableListOf(
                                    DataSchema.Item.Element(
                                        value = Value.Literal(fact = JsonElement.Text(ARRAY_ITEM_3))
                                    ),
                                    DataSchema.Item.Element(
                                        value = Value.Literal(fact = JsonElement.Text(ARRAY_ITEM_4))
                                    )
                                )
                            )
                        )
                    )

                    "then the builder should return a data" {
                        val result = dataSchema.build(ENV_VARS, CONTEXT)
                        result.shouldBeSuccess()
                        result.value shouldBe JsonElement.Array(
                            JsonElement.Array(
                                JsonElement.Text(ARRAY_ITEM_1),
                                JsonElement.Text(ARRAY_ITEM_2)
                            ),
                            JsonElement.Array(
                                JsonElement.Text(ARRAY_ITEM_3),
                                JsonElement.Text(ARRAY_ITEM_4)
                            )
                        )
                    }
                }
            }

            "when occurs an error" - {
                val dataSchema = DataSchema.Struct(
                    properties = listOf(
                        DataSchema.Property.Element(
                            name = DATA_KEY_1,
                            value = Value.Expression(EXPRESSION)
                        )
                    )
                )

                "then the builder should return a failure" {
                    val result = dataSchema.build(ENV_VARS, CONTEXT)
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<DataBuildErrors>()
                }
            }
        }
    }

    private companion object {
        private val ENV_VARS = envVarsMapOf()
        private val CONTEXT = Context.empty()

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
                envVars: EnvVars,
                context: Context
            ): ResultK<JsonElement, FeelExpression.EvaluateError> =
                FeelExpression.EvaluateError(this).asFailure()
        }
    }
}
