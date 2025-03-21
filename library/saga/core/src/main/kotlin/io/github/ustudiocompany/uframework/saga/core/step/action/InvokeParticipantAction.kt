package io.github.ustudiocompany.uframework.saga.core.step.action

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.core.MetaData
import io.github.ustudiocompany.uframework.saga.core.request.RequestBuilder
import io.github.ustudiocompany.uframework.saga.core.response.ResponseBodyDeserializer
import io.github.ustudiocompany.uframework.saga.core.step.action.handler.ErrorReplyHandler
import io.github.ustudiocompany.uframework.saga.core.step.action.handler.SuccessfulReplyHandler

public class InvokeParticipantAction<DATA> private constructor(
    public val requestBuilder: RequestBuilder<DATA>,
    public val successfulReplyHandler: SuccessfulReplyHandler<DATA>?,
    public val errorReplyHandler: ErrorReplyHandler<DATA>?
) {

    public class Builder<DATA> internal constructor(private val requestBuilder: RequestBuilder<DATA>) {
        private var successfulReplyHandler: SuccessfulReplyHandler<DATA>? = null
        private var errorReplyHandler: ErrorReplyHandler<DATA>? = null

        public fun <BODY> onReply(
            deserializer: ResponseBodyDeserializer<BODY>,
            handler: (data: DATA, metadata: MetaData, body: BODY) -> ResultK<DATA, Failure>
        ): Builder<DATA> {
            if (successfulReplyHandler != null) error("Re-definition of a successful reply handler.")
            successfulReplyHandler = SuccessfulReplyHandler(deserializer, handler)
            return this
        }

        public fun onReply(
            handler: (data: DATA, metadata: MetaData) -> ResultK<DATA, Failure>
        ): Builder<DATA> {
            if (successfulReplyHandler != null) error("Re-definition of a successful reply handler.")
            successfulReplyHandler = SuccessfulReplyHandler(handler)
            return this
        }

        public fun <BODY> onError(
            deserializer: ResponseBodyDeserializer<BODY>,
            handler: (data: DATA, metadata: MetaData, body: BODY) -> ResultK<DATA, Failure>
        ): Builder<DATA> {
            if (errorReplyHandler != null) error("Re-definition of an error reply handler.")
            errorReplyHandler = ErrorReplyHandler(deserializer, handler)
            return this
        }

        public fun onError(
            handler: (data: DATA, metadata: MetaData) -> ResultK<DATA, Failure>
        ): Builder<DATA> {
            if (errorReplyHandler != null) error("Re-definition of an error reply handler.")
            errorReplyHandler = ErrorReplyHandler(handler)
            return this
        }

        internal fun build(): InvokeParticipantAction<DATA> = InvokeParticipantAction(
            requestBuilder = requestBuilder,
            successfulReplyHandler = successfulReplyHandler,
            errorReplyHandler = errorReplyHandler
        )
    }
}
