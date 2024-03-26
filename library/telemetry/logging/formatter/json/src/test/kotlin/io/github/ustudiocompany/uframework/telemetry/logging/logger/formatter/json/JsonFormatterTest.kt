package io.github.ustudiocompany.uframework.telemetry.logging.logger.formatter.json

import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext
import io.kotest.matchers.shouldBe

internal class JsonFormatterTest : UnitTest() {
    companion object {
        private const val MESSAGE = "Unknown error."
        private const val FIRST_ELEMENT_KEY = "key-1"
        private const val FIRST_ELEMENT_VALUE = "value-1"
        private const val SECOND_ELEMENT_KEY = "key-2"
        private const val SECOND_ELEMENT_VALUE = "value-2"
    }

    init {
        "The JsonFormatter" - {

            "when the `diagnosticContext` is empty" - {
                val diagnosticContext = DiagnosticContext.Empty

                "then the result should not contain the `details` attribute" {
                    val result = JsonFormatter.format(MESSAGE, diagnosticContext, exception = null)
                    result shouldBe """{"message": "$MESSAGE"}"""
                }

                "when passed some exception" - {
                    val exception = RuntimeException("Some error.")

                    "then the result should contain the `details` attribute" {
                        val expected = """{"message": "$MESSAGE", "exception": {"message": "Some error."}}"""

                        val result = JsonFormatter.format(MESSAGE, diagnosticContext, exception = exception)

                        result shouldBe expected
                    }
                }
            }

            "when the `diagnosticContext` is not empty" - {

                "when the `diagnosticContext` contains only one element" - {
                    val diagnosticContext = DiagnosticContext.Empty + (FIRST_ELEMENT_KEY to FIRST_ELEMENT_VALUE)

                    "then the result should contain the `details` attribute with only one element" {
                        val result = JsonFormatter.format(MESSAGE, diagnosticContext, exception = null)
                        result shouldBe """{"message": "$MESSAGE", "details": {"$FIRST_ELEMENT_KEY": "$FIRST_ELEMENT_VALUE"}}"""
                    }
                }

                "when the `diagnosticContext` contains some elements" - {
                    val diagnosticContext =
                        DiagnosticContext.Empty + (FIRST_ELEMENT_KEY to FIRST_ELEMENT_VALUE) + (SECOND_ELEMENT_KEY to SECOND_ELEMENT_VALUE)

                    "then the result should contain the `details` attribute with all passed elements" {
                        val result = JsonFormatter.format(MESSAGE, diagnosticContext, exception = null)
                        result shouldBe """{"message": "$MESSAGE", "details": {"$SECOND_ELEMENT_KEY": "$SECOND_ELEMENT_VALUE", "$FIRST_ELEMENT_KEY": "$FIRST_ELEMENT_VALUE"}}"""
                    }
                }

                "when the `diagnosticContext` contains some element that is of type collection" - {

                    "when the value contains only one item" - {
                        val diagnosticContext =
                            DiagnosticContext.Empty + (FIRST_ELEMENT_KEY to listOf(FIRST_ELEMENT_VALUE))

                        "then the result should contain the `details` attribute with a value of the attribute as an array" {
                            val result = JsonFormatter.format(MESSAGE, diagnosticContext, exception = null)
                            result shouldBe """{"message": "$MESSAGE", "details": {"$FIRST_ELEMENT_KEY": ["$FIRST_ELEMENT_VALUE"]}}"""
                        }
                    }

                    "when the value contains some items" - {
                        val diagnosticContext =
                            DiagnosticContext.Empty +
                                (FIRST_ELEMENT_KEY to listOf(FIRST_ELEMENT_VALUE, SECOND_ELEMENT_VALUE))

                        "then the result should contain the `details` attribute with a value of the attribute as an array" {
                            val result = JsonFormatter.format(MESSAGE, diagnosticContext, exception = null)
                            result shouldBe """{"message": "$MESSAGE", "details": {"$FIRST_ELEMENT_KEY": ["$FIRST_ELEMENT_VALUE", "$SECOND_ELEMENT_VALUE"]}}"""
                        }
                    }
                }
            }
        }
    }
}
