package io.github.ustudiocompany.uframework.rulesengine.core.rule

public sealed interface Step {
    public val predicate: Predicates?

    public data class Call(
        public override val predicate: Predicates?,
        public val uri: String,
        public val args: List<Arg>,
        public val result: Result
    ) : Step {

        public data class Arg(
            public val name: String,
            public val type: ArgType,
            public val value: Value
        )
    }

    public data class Requirement(
        public override val predicate: Predicates?,
        public val target: Value,
        public val compareWith: Value,
        public val comparator: Comparator,
        public val errorCode: String
    ) : Step

    public data class Action(
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
