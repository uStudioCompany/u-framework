package io.github.ustudiocompany.uframework.saga

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.message.CommandMessage

public typealias SagaDataInitializer<DATA> = (CommandMessage) -> Result<DATA, Failure>
