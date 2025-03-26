package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.Args
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.UriTemplate

public data class CallStep(
    public override val condition: Condition?,
    public val uri: UriTemplate,
    public val args: Args,
    public val result: Step.Result
) : Step
