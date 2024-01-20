package io.github.ustudiocompany.uframework.saga.step.action

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.MetaData
import io.github.ustudiocompany.uframework.saga.request.Request
import io.github.ustudiocompany.uframework.saga.response.ResponseBodyDeserializer
import io.github.ustudiocompany.uframework.saga.step.action.handler.SuccessfulReplyHandler

public class CompensationAction<DATA> private constructor(
    public val requestBuilder: (DATA) -> Result<Request, Failure>,
    public val successfulReplyHandler: SuccessfulReplyHandler<DATA>?
) {

    public class Builder<DATA> internal constructor(
        private val requestBuilder: (DATA) -> Result<Request, Failure>
    ) {
        private var successfulReplyHandler: SuccessfulReplyHandler<DATA>? = null

        public fun <BODY> onReply(
            deserializer: ResponseBodyDeserializer<BODY>,
            handler: (data: DATA, metadata: MetaData, body: BODY) -> Result<DATA, Failure>
        ): Builder<DATA> {
            if (successfulReplyHandler != null) error("Re-definition of a successful reply handler.")
            successfulReplyHandler = SuccessfulReplyHandler(deserializer, handler)
            return this
        }

        public fun onReply(
            handler: (data: DATA, metadata: MetaData) -> Result<DATA, Failure>
        ): Builder<DATA> {
            if (successfulReplyHandler != null) error("Re-definition of a successful reply handler.")
            successfulReplyHandler = SuccessfulReplyHandler(handler)
            return this
        }

        internal fun build(): CompensationAction<DATA> = CompensationAction(
            requestBuilder = requestBuilder,
            successfulReplyHandler = successfulReplyHandler
        )
    }
}
