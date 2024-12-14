package io.github.ustudiocompany.uframework.saga.core.request

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure

public typealias RequestBodySerializer<BODY> = (BODY) -> ResultK<String?, Failure>
