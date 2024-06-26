package io.github.ustudiocompany.uframework.saga.core.response

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.failure.Failure

public typealias ResponseBodyDeserializer<BODY> = (String) -> Result<BODY, Failure>
