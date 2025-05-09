package io.github.ustudiocompany.uframework.json.element.merge.strategy

import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.engine.merge.strategy.role.MergeRule

public typealias MergeStrategy = Map<AttributePath, MergeRule>
