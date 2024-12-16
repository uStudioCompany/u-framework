package io.github.ustudiocompany.uframework.saga.core.step.action.handler

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.core.MetaData
import io.github.ustudiocompany.uframework.saga.core.message.ReplyMessage
import io.github.ustudiocompany.uframework.saga.core.response.ResponseBodyDeserializer

public fun interface ErrorReplyHandler<DATA> {

    public fun handle(data: DATA, reply: ReplyMessage.Error): ResultK<DATA, ReplyHandlerErrors>

    public companion object {

        public operator fun <DATA, BODY> invoke(
            deserializer: ResponseBodyDeserializer<BODY>,
            handler: (data: DATA, metadata: MetaData, body: BODY) -> ResultK<DATA, Failure>
        ): ErrorReplyHandler<DATA> = ErrorReplyHandler { data, reply ->
            val serializedBody = reply.body
                ?: return@ErrorReplyHandler ReplyHandlerErrors.ReplyBodyMissing().asFailure()
            val body: BODY = deserializer(serializedBody)
                .getOrForward { (failure) ->
                    return@ErrorReplyHandler ReplyHandlerErrors.ReplyBodyDeserialization(failure).asFailure()
                }
            handler(data, reply.metadata, body)
                .mapFailure { error -> ReplyHandlerErrors.ReplyHandle(error) }
        }

        public operator fun <DATA> invoke(
            handler: (data: DATA, metadata: MetaData) -> ResultK<DATA, Failure>
        ): ErrorReplyHandler<DATA> = ErrorReplyHandler { data, reply ->
            handler(data, reply.metadata)
                .mapFailure { error -> ReplyHandlerErrors.ReplyHandle(error) }
        }
    }
}
