package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Comparator
import io.github.ustudiocompany.uframework.rulesengine.core.rule.DataScheme
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.header.Headers
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Conditional
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Predicates
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.UriTemplate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.UriTemplateParams

public sealed interface Step : Conditional {

    public data class ErrorCode(val get: String)

    public data class Call(
        public override val predicate: Predicates?,
        public val uri: UriTemplate,
        public val params: UriTemplateParams,
        public val headers: Headers,
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
            REPLACE,
            MERGE
        }
    }
}
