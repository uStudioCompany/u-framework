package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.header.Headers
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Predicates
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.UriTemplate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.UriTemplateParams

public data class CallStep(
    public override val predicate: Predicates?,
    public val uri: UriTemplate,
    public val params: UriTemplateParams,
    public val headers: Headers,
    public val result: Step.Result
) : Step
