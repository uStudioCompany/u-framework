package io.github.ustudiocompany.uframework.rulesengine.core.rule.condition

@JvmInline
public value class Condition private constructor(public val predicates: List<Predicate>) {

    public companion object {
        public val NONE: Condition = Condition(emptyList())

        @JvmStatic
        public operator fun invoke(predicates: List<Predicate>): Condition =
            if (predicates.isEmpty()) NONE else Condition(predicates)
    }
}
