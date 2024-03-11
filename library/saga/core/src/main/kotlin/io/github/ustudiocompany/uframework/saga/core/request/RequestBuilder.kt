package io.github.ustudiocompany.uframework.saga.core.request

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure

public typealias RequestBuilder<DATA> = (DATA) -> Result<Request, Failure>

public fun <T> makeRequestBuilder(block: RequestBuilder<T>): RequestBuilder<T> = block
