package io.github.ustudiocompany.uframework.engine.merge.strategy.role

public sealed interface MergeRule {
    public object WholeListMerge : MergeRule
    public class MergeByAttributes(public val attributes: List<String>) : MergeRule
}
