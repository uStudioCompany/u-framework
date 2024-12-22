package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public sealed class ContextError(override val message: String, override val cause: Throwable?) : RuleEngineError {

    public class SourceMissing(source: Source) :
        ContextError("The source `${source.get}` is not found.", null)

    public class SourceAlreadyExists(source: Source) :
        ContextError("The source `${source.get}` is already exists.", null)
}
