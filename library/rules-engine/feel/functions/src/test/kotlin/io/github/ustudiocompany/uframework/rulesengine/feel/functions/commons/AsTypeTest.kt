package io.github.ustudiocompany.uframework.rulesengine.feel.functions.commons

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.feel.functions.common.asType
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import org.camunda.feel.syntaxtree.Val
import org.camunda.feel.syntaxtree.ValBoolean
import org.camunda.feel.syntaxtree.ValFatalError
import org.camunda.feel.syntaxtree.ValString

@OptIn(AirfluxTypesExperimental::class)
internal class AsTypeTest : UnitTest() {

    init {

        "The `asType` function" - {

            "when the type matches the expected type" - {
                val actual: Val = ValString(STRING_VALUE)
                val expected = ValString::class

                val result = actual.asType(PARAM_NAME, expected)

                "then should return the value of the expected type" {
                    result shouldBeSuccess ValString(STRING_VALUE)
                }
            }

            "when the type does not match the expected type" - {
                val actual: Val = ValBoolean(true)
                val expected = ValString::class

                val result = actual.asType(PARAM_NAME, expected)

                "then should return the error" {
                    result shouldBeFailure ValFatalError(
                        "Invalid type of the parameter '$PARAM_NAME'. Expected: ValString, but was: ValBoolean"
                    )
                }
            }
        }
    }

    private companion object {
        private const val STRING_VALUE = "Hello"
        private const val PARAM_NAME = "param"
    }
}
