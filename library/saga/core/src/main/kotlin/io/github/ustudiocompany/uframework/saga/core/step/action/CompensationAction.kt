package io.github.ustudiocompany.uframework.saga.core.step.action

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.core.MetaData
import io.github.ustudiocompany.uframework.saga.core.request.RequestBuilder
import io.github.ustudiocompany.uframework.saga.core.response.ResponseBodyDeserializer
import io.github.ustudiocompany.uframework.saga.core.step.action.handler.SuccessfulReplyHandler

public class CompensationAction<DATA> private constructor(
    public val requestBuilder: RequestBuilder<DATA>,
    public val successfulReplyHandler: SuccessfulReplyHandler<DATA>?
) {

    public class Builder<DATA> internal constructor(private val requestBuilder: RequestBuilder<DATA>) {
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
