package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.rulesengine.executor.error.UriBuilderError
import java.net.URI

internal fun UriTemplate.build(): ResultK<URI, UriBuilderError> =
    try {
        URI(this.get).asSuccess()
    } catch (expected: Exception) {
        UriBuilderError.InvalidUriTemplate(this.get, expected).asFailure()
    }
