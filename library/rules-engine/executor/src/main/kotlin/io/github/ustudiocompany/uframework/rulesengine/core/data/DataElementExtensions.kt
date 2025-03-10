package io.github.ustudiocompany.uframework.rulesengine.core.data

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.executor.error.DataErrors

internal fun DataElement.search(path: Path): ResultK<DataElement?, DataErrors.Search> =
    path.searchIn(this).mapFailure { DataErrors.Search(it) }

internal fun DataElement.toPlainString() = when (this) {
    is DataElement.Text -> this.get
    is DataElement.Decimal -> this.get.toPlainString()
    is DataElement.Bool -> this.get.toString()
    else -> error("Invalid DataElement type: $this")
}
