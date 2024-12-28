package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Args
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Comparator
import io.github.ustudiocompany.uframework.rulesengine.core.rule.DataScheme
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Conditional
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Predicates

public sealed interface Step : Conditional {

    public data class ErrorCode(val get: String)

    public data class Call(
        public override val predicate: Predicates?,
        public val uri: String,
        public val args: Args,
        public val result: Result
    ) : Step

    public data class Requirement(
        public override val predicate: Predicates?,
        public val target: Value,
        public val compareWith: Value,
        public val comparator: Comparator,
        public val errorCode: ErrorCode
    ) : Step

    public data class Data(
        public override val predicate: Predicates?,
        public val dataScheme: DataScheme,
        public val result: Result
    ) : Step

    public data class Result(val source: Source, val action: Action) {

        public enum class Action {
            PUT,
            MERGE
        }
    }
}
