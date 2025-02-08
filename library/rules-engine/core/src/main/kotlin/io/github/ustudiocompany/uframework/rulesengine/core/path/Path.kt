package io.github.ustudiocompany.uframework.rulesengine.core.path

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.path.PathEngine

public interface Path {
    public fun search(data: DataElement): ResultK<DataElement?, PathEngine.Errors>
}
