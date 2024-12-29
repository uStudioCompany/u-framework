package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.executor.error.UriBuilderError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

internal class UriBuilderTest : UnitTest() {

    init {

        "The URI builder" - {

            "when some value of placeholders is not provided" - {
                val result = VALID_URI_TEMPLATE.build(emptyMap())

                "then the building should be a failure" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<UriBuilderError.ParamMissing>()
                }
            }

            "when all value of placeholders are provided" - {

                "when the uri template is valid" - {
                    val result = VALID_URI_TEMPLATE.build(ARGS)

                    "then the building should be a success" {
                        result.shouldBeSuccess()
                    }
                }

                "when the uri template is invalid" - {
                    val result = INVALID_URI_TEMPLATE.build(ARGS)

                    "then the building should be a failure" {
                        result.shouldBeFailure()
                        val error = result.cause.shouldBeInstanceOf<UriBuilderError.InvalidUriTemplate>()
                        println(error.description)
                    }
                }
            }
        }
    }

    private companion object {
        private const val ID_PLACEHOLDER = "id"
        private const val ID_VALUE = "1"

        private const val LANG_PLACEHOLDER = "lang"
        private const val LANG_VALUE = "en"

        private val VALID_URI_TEMPLATE = UriTemplate("http://example.com/users/{id}?lang={lang}")
        private val INVALID_URI_TEMPLATE = UriTemplate("http[:]example.com/users/{id}?lang={lang}")

        private val ARGS = mapOf(
            ID_PLACEHOLDER to ID_VALUE,
            LANG_PLACEHOLDER to LANG_VALUE
        )
    }
}
