package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.rulesengine.core.data.path.Path

public sealed class DataError(override val message: String, override val cause: Throwable?) : RuleEngineError {
    public class PathMissing(path: Path, cause: Throwable? = null) :
        DataError("The path: `$path` is missing.", cause)

    public class PathParsing(message: String, cause: Throwable?) : DataError(message, cause)
}
