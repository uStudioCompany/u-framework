package io.github.ustudiocompany.uframework.saga.core.step.action.handler

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.getOrForward
import io.github.airflux.functional.mapError
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.core.MetaData
import io.github.ustudiocompany.uframework.saga.core.message.ReplyMessage
import io.github.ustudiocompany.uframework.saga.core.response.ResponseBodyDeserializer

public fun interface SuccessfulReplyHandler<DATA> {

    public fun handle(data: DATA, reply: ReplyMessage.Success): Result<DATA, ReplyHandlerErrors>

    public companion object {

        public operator fun <DATA, BODY> invoke(
            deserializer: ResponseBodyDeserializer<BODY>,
            handler: (data: DATA, metadata: MetaData, body: BODY) -> Result<DATA, Failure>
        ): SuccessfulReplyHandler<DATA> = SuccessfulReplyHandler { data, reply ->
            val serializedBody = reply.body
                ?: return@SuccessfulReplyHandler ReplyHandlerErrors.ReplyBodyMissing().error()
            val body: BODY = deserializer(serializedBody)
                .getOrForward { (error) ->
                    return@SuccessfulReplyHandler ReplyHandlerErrors.ReplyBodyDeserialization(error).error()
                }
            handler(data, reply.metadata, body)
                .mapError { error -> ReplyHandlerErrors.ReplyHandle(error) }
        }

        public operator fun <DATA> invoke(
            handler: (data: DATA, metadata: MetaData) -> Result<DATA, Failure>
        ): SuccessfulReplyHandler<DATA> = SuccessfulReplyHandler { data, reply ->
            handler(data, reply.metadata)
                .mapError { error -> ReplyHandlerErrors.ReplyHandle(error) }
        }
    }
}
