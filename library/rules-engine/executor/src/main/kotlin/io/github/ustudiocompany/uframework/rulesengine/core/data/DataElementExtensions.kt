package io.github.ustudiocompany.uframework.rulesengine.core.data

import com.jayway.jsonpath.Configuration
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.path.Path
import io.github.ustudiocompany.uframework.rulesengine.executor.error.DataError

internal fun DataElement.search(path: Path, configuration: Configuration): ResultK<DataElement, DataError> = try {
    path.get.read<DataElement>(this, configuration).asSuccess()
} catch (expected: Exception) {
    DataError.PathMissing(path, expected).asFailure()
}
