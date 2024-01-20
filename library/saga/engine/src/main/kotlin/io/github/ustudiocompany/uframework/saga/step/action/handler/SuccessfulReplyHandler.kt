package io.github.ustudiocompany.uframework.saga.step.action.handler

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.getOrForward
import io.github.airflux.functional.mapError
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.MetaData
import io.github.ustudiocompany.uframework.saga.error.SagaExecutorErrors
import io.github.ustudiocompany.uframework.saga.message.ReplyMessage
import io.github.ustudiocompany.uframework.saga.response.ResponseBodyDeserializer

public fun interface SuccessfulReplyHandler<DATA> {

    public fun handle(data: DATA, reply: ReplyMessage.Success): Result<DATA, Failure>

    public companion object {

        public operator fun <DATA, BODY> invoke(
            deserializer: ResponseBodyDeserializer<BODY>,
            handler: (data: DATA, metadata: MetaData, body: BODY) -> Result<DATA, Failure>
        ): SuccessfulReplyHandler<DATA> = SuccessfulReplyHandler { data, reply ->
            val serializedBody = reply.body
                ?: return@SuccessfulReplyHandler SagaExecutorErrors.ReplyBodyMissing().error()
            val body: BODY = deserializer(serializedBody)
                .getOrForward { (error) ->
                    return@SuccessfulReplyHandler SagaExecutorErrors.ReplyBodyDeserialization(error).error()
                }
            handler(data, reply.metadata, body)
                .mapError { error -> SagaExecutorErrors.ReplyHandle(error) }
        }

        public operator fun <DATA> invoke(
            handler: (data: DATA, metadata: MetaData) -> Result<DATA, Failure>
        ): SuccessfulReplyHandler<DATA> = SuccessfulReplyHandler { data, reply ->
            handler(data, reply.metadata)
        }
    }
}
