package io.github.ustudiocompany.uframework.engine.merge

public sealed interface MergeRule {
    public object WholeListMerge : MergeRule
    public class MergeByAttributes(public val attributes: List<String>) : MergeRule
}
