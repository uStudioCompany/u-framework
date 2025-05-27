package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarName
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression

public sealed interface Value {

    public data class Literal(
        public val fact: JsonElement
    ) : Value

    public data class Reference(
        public val source: Source,
        public val path: Path
    ) : Value

    public data class Expression(
        public val expression: FeelExpression
    ) : Value

    public data class EnvVars(
        public val name: EnvVarName
    ) : Value
}
