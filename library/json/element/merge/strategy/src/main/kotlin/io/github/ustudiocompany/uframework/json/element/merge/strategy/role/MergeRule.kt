package io.github.ustudiocompany.uframework.json.element.merge.strategy.role

public sealed interface MergeRule {
    public object WholeListMerge : MergeRule
    public class MergeByAttributes(public val attributes: List<String>) : MergeRule
}
