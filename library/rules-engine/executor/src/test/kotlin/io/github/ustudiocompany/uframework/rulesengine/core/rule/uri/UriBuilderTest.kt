package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.rulesengine.executor.error.UriBuilderError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class UriBuilderTest : UnitTest() {

    init {

        "The URI builder" - {

            "when the uri template is valid" - {
                listOf(
                    "users:id"
                ).forEach {
                    val template = UriTemplate(it)
                    val result = template.build()

                    "then the building should be a success" {
                        result.shouldBeSuccess()
                    }
                }
            }

            "when the uri template is invalid" - {
                listOf(
                    "users[:]id",
                    "example.com/users/{id}"
                ).forEach {
                    val template = UriTemplate(it)
                    val result = template.build()

                    "then the building should be a failure" {
                        result.shouldContainFailureInstance()
                            .shouldBeInstanceOf<UriBuilderError.InvalidUriTemplate>()
                    }
                }
            }
        }
    }
}
