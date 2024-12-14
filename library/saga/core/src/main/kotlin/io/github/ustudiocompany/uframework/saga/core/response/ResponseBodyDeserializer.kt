package io.github.ustudiocompany.uframework.saga.core.response

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure

public typealias ResponseBodyDeserializer<BODY> = (String) -> ResultK<BODY, Failure>
