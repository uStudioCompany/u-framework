package io.github.ustudiocompany.uframework.rulesengine.core.data

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.path.Path

internal fun JsonElement.toStringValue() = if (this is JsonElement.Text) this.get else this.toJson()

internal fun JsonElement.search(path: Path): ResultK<JsonElement?, Path.SearchError> = path.searchIn(this)
