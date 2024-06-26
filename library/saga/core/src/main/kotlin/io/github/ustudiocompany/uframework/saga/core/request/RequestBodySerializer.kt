package io.github.ustudiocompany.uframework.saga.core.request

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.failure.Failure

public typealias RequestBodySerializer<BODY> = (BODY) -> Result<String?, Failure>
