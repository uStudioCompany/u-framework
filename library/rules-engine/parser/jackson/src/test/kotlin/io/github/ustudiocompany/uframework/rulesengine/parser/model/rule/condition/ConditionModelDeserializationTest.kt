package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.condition

import com.fasterxml.jackson.core.type.TypeReference
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.parser.JacksonDeserializer
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.FactModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ConditionModelDeserializationTest : UnitTest() {

    init {

        "The ConditionModel type" - {
            val json = """
                | [
                |   {
                |     "target": {
                |       "kind": "reference",
                |       "source": "$SOURCE",
                |       "path": "$PATH"
                |     },
                |     "operator": "$OPERATOR",
                |     "value": {
                |       "kind": "fact",
                |       "fact": "$FACT"
                |     }
                |   }
                | ]
            """.trimMargin()

            val result = deserializer.deserialize(
                input = json,
                type = object : TypeReference<List<PredicateModel>>() {}
            )

            "then should be return valid type" {
                val value = result.shouldContainSuccessInstance()
                    .shouldBeInstanceOf<List<PredicateModel>>()
                value shouldBe listOf(
                    PredicateModel(
                        target = ValueModel.Reference(source = SOURCE, path = PATH),
                        operator = OPERATOR,
                        value = ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT)))
                    )
                )
            }
        }
    }

    private companion object {
        private const val SOURCE = "input_body"
        private const val PATH = "$.scheme"
        private const val FACT = """SCHEME-1"""
        private const val OPERATOR = "contains"

        private val deserializer = JacksonDeserializer()
    }
}
