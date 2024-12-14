package io.github.ustudiocompany.uframework.saga.core

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.core.message.CommandMessage

public typealias SagaDataInitializer<DATA> = (CommandMessage) -> ResultK<DATA, Failure>
