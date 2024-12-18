package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.data.path.Path

public sealed interface Value {

    public data class Literal(
        public val fact: DataElement
    ) : Value

    public data class Reference(
        public val source: Source,
        public val path: Path
    ) : Value

    public data class Expression(
        val expression: DataElement
    ) : Value
}
