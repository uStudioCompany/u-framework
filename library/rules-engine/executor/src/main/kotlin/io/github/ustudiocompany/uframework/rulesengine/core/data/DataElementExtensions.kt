package io.github.ustudiocompany.uframework.rulesengine.core.data

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.rulesengine.core.data.path.Path
import io.github.ustudiocompany.uframework.rulesengine.executor.error.DataError

internal fun DataElement.search(path: Path): ResultK<DataElement, DataError> =
    path.search(this).mapFailure { DataError.Search(it) }
