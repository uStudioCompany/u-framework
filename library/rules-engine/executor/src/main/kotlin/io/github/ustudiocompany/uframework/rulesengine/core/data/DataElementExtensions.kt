package io.github.ustudiocompany.uframework.rulesengine.core.data

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path

internal fun DataElement.search(path: Path): ResultK<DataElement?, DataSearchError> =
    path.searchIn(this).mapFailure { failure -> DataSearchError(path = path, cause = failure) }

public class DataSearchError(path: Path, cause: Path.SearchError) : BasicRulesEngineError {
    override val code: String = PREFIX + "1"
    override val description: String = "Error searching for data by path '${path.text}'."
    override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    override val details: Failure.Details = Failure.Details.of(
        DETAILS_KEY_PATH to path.text
    )

    private companion object {
        private const val PREFIX = "DATA-SEARCH-"
        private const val DETAILS_KEY_PATH = "json-path"
    }
}
