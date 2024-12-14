package io.github.ustudiocompany.uframework.saga.core.request

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure

public typealias RequestBuilder<DATA> = (DATA) -> ResultK<Request, Failure>

public fun <T> makeRequestBuilder(block: RequestBuilder<T>): RequestBuilder<T> = block
