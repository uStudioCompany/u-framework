package io.github.ustudiocompany.uframework.saga.request

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure

public typealias RequestBodySerializer<BODY> = (BODY) -> Result<String?, Failure>
