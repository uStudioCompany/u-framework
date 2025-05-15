package io.github.ustudiocompany.uframework.json.element.merge.strategy.role

public sealed interface MergeRule {
    public data object WholeListMerge : MergeRule
    public data class MergeByAttributes(public val attributes: List<String>) : MergeRule
}
