package io.github.ustudiocompany.uframework.saga.core.request

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.map
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.saga.core.MetaData
import io.github.ustudiocompany.uframework.saga.core.extension.toChannelName
import io.github.ustudiocompany.uframework.saga.core.extension.toMessageName
import io.github.ustudiocompany.uframework.saga.core.extension.toMessageVersion

public fun <BODY : Any> requestPrototype(
    channel: String,
    name: String,
    version: String,
    serializer: RequestBodySerializer<BODY>
): RequestPrototype.WithBody<BODY> =
    RequestPrototype.WithBody(channel = channel, name = name, version = version, serializer = serializer)

public fun requestPrototype(
    channel: String,
    name: String,
    version: String,
): RequestPrototype.WithoutBody = RequestPrototype.WithoutBody(channel = channel, name = name, version = version)

public sealed class RequestPrototype(
    channel: String,
    name: String,
    version: String
) {
    protected val channel: ChannelName = channel.toChannelName()
    protected val name: MessageName = name.toMessageName()
    protected val version: MessageVersion = version.toMessageVersion()

    public class WithBody<BODY : Any> internal constructor(
        channel: String,
        name: String,
        version: String,
        private val serializer: RequestBodySerializer<BODY>
    ) : RequestPrototype(channel, name, version) {

        public fun createInstance(body: BODY): ResultK<Request, Failure> =
            createInstance(metadata = MetaData.EMPTY, body = body)

        public fun createInstance(metadata: MetaData, body: BODY): ResultK<Request, Failure> =
            serializer(body)
                .map { serializedBody ->
                    Request(
                        channel = channel,
                        name = name,
                        version = version,
                        metadata = metadata,
                        body = serializedBody
                    )
                }
    }

    public class WithoutBody internal constructor(
        channel: String,
        name: String,
        version: String,
    ) : RequestPrototype(channel, name, version) {

        public fun createInstance(metadata: MetaData = MetaData.EMPTY): ResultK<Request, Failure> =
            Request(
                channel = channel,
                name = name,
                version = version,
                metadata = metadata,
                body = null
            ).asSuccess()
    }
}
