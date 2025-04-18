package io.github.ustudiocompany.rulesengine.parser.model.rule

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.parser.JacksonDeserializer
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.FactModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ValueModelDeserializationTest : UnitTest() {

    init {

        "The ValueModel type" - {

            "when JSON has a literal value" - {
                val json = """
                    | {
                    |   "kind": "fact",
                    |   "fact": "$FACT"
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(json, ValueModel::class.java)

                "then should be return a literal value" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<ValueModel.Literal>()

                    value shouldBe ValueModel.Literal(fact = FactModel(DataElement.Text(FACT)))
                }
            }

            "when JSON has a reference value" - {
                val json = """
                    | {
                    |   "kind": "reference",
                    |   "source": "$SOURCE",
                    |   "path": "$PATH"
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(json, ValueModel::class.java)

                "then should be return a reference value" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<ValueModel.Reference>()

                    value shouldBe ValueModel.Reference(source = SOURCE, path = PATH)
                }
            }

            "when JSON has an expression value" - {
                val json = """
                    | {
                    |   "kind": "expression",
                    |   "expression": "$EXPRESSION"
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(json, ValueModel::class.java)

                "then should be return an expression value" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<ValueModel.Expression>()

                    value shouldBe ValueModel.Expression(expression = EXPRESSION)
                }
            }
        }
    }

    private companion object {
        private const val SOURCE = "input_body"
        private const val PATH = "$.scheme"
        private const val FACT = "UA"
        private const val EXPRESSION = "1 + 2"

        private val deserializer = JacksonDeserializer()
    }
}
