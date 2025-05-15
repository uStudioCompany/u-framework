package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class SourceTest : UnitTest() {

    init {

        "The `Source` type" - {

            "when comparing two values with different case" - {

                "then the values should be equal" - {
                    withData(
                        listOf(
                            SOURCE_LOWERCASE to SOURCE_LOWERCASE,
                            Source("source") to Source("source"),
                            SOURCE_CAPITALIZE to SOURCE_LOWERCASE,
                            SOURCE_UPPERCASE to SOURCE_LOWERCASE,
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
                            SOURCE_LOWERCASE to "source",
                            SOURCE_LOWERCASE to 1,
                            SOURCE_LOWERCASE to null
                        )
                    ) { (left: Any, right: Any?) ->
                        (left == right) shouldBe false
                    }
                }
            }

            "the `toString` function" - {

                "should return the source value" {
                    SOURCE_LOWERCASE.toString() shouldBe LOWERCASE_SOURCE_VALUE
                    SOURCE_UPPERCASE.toString() shouldBe UPPERCASE_SOURCE_VALUE
                    SOURCE_CAPITALIZE.toString() shouldBe CAPITALIZE_SOURCE_VALUE
                }
            }
        }
    }

    private companion object {
        private const val LOWERCASE_SOURCE_VALUE = "source"
        private const val UPPERCASE_SOURCE_VALUE = "SOURCE"
        private const val CAPITALIZE_SOURCE_VALUE = "Source"

        private val SOURCE_LOWERCASE = Source(LOWERCASE_SOURCE_VALUE)
        private val SOURCE_UPPERCASE = Source(UPPERCASE_SOURCE_VALUE)
        private val SOURCE_CAPITALIZE = Source(CAPITALIZE_SOURCE_VALUE)
    }
}
