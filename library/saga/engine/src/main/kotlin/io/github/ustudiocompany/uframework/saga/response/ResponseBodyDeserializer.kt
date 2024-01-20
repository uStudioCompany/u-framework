package io.github.ustudiocompany.uframework.saga.response

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure

public typealias ResponseBodyDeserializer<BODY> = (String) -> Result<BODY, Failure>
