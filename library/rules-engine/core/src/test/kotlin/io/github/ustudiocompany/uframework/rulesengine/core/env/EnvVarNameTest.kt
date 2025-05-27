package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class EnvVarNameTest : UnitTest() {

    init {

        "The `EnvVarName` type" - {

            "when comparing two values with different case" - {

                "then the values should be equal" - {
                    withData(
                        listOf(
                            ENV_VAR_NAME_LOWERCASE to ENV_VAR_NAME_LOWERCASE,
                            Source("source") to Source("source"),
                            ENV_VAR_NAME_CAPITALIZE to ENV_VAR_NAME_LOWERCASE,
                            ENV_VAR_NAME_UPPERCASE to ENV_VAR_NAME_LOWERCASE,
                        )
                    ) { (left: Any, right: Any) ->
                        (left == right) shouldBe true
                        (right == left) shouldBe true
                        left.hashCode() shouldBe right.hashCode()
                    }
                }
            }

            "when comparing two values with different type" - {

                "then the values should not be equal" - {

                    withData(
                        listOf(
                            ENV_VAR_NAME_LOWERCASE to "source",
                            ENV_VAR_NAME_LOWERCASE to 1,
                            ENV_VAR_NAME_LOWERCASE to null
                        )
                    ) { (left: Any, right: Any?) ->
                        (left == right) shouldBe false
                    }
                }
            }

            "the `toString` function" - {

                "should return the source value" {
                    ENV_VAR_NAME_LOWERCASE.toString() shouldBe LOWERCASE_VALUE
                    ENV_VAR_NAME_UPPERCASE.toString() shouldBe UPPERCASE_VALUE
                    ENV_VAR_NAME_CAPITALIZE.toString() shouldBe CAPITALIZE_VALUE
                }
            }
        }
    }

    private companion object {
        private const val LOWERCASE_VALUE = "envvar"
        private const val UPPERCASE_VALUE = "ENVVAR"
        private const val CAPITALIZE_VALUE = "EnvVar"

        private val ENV_VAR_NAME_LOWERCASE = Source(LOWERCASE_VALUE)
        private val ENV_VAR_NAME_UPPERCASE = Source(UPPERCASE_VALUE)
        private val ENV_VAR_NAME_CAPITALIZE = Source(CAPITALIZE_VALUE)
    }
}
