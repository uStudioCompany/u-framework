package io.github.ustudiocompany.uframework.json.element.merge.strategy

import io.github.ustudiocompany.uframework.json.element.merge.path.AttributePath
import io.github.ustudiocompany.uframework.json.element.merge.strategy.role.MergeRule

public typealias MergeStrategy = Map<AttributePath, MergeRule>
