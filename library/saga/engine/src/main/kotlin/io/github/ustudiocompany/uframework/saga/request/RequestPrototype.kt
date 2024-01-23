package io.github.ustudiocompany.uframework.saga.request

import io.github.airflux.functional.Result
import io.github.airflux.functional.map
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.saga.MetaData
import io.github.ustudiocompany.uframework.saga.internal.toChannelName
import io.github.ustudiocompany.uframework.saga.internal.toMessageName
import io.github.ustudiocompany.uframework.saga.internal.toMessageVersion

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

        public fun createInstance(body: BODY): Result<Request, Failure> =
            createInstance(metadata = MetaData.Empty, body = body)

        public fun createInstance(metadata: MetaData, body: BODY): Result<Request, Failure> =
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

        public fun createInstance(metadata: MetaData = MetaData.Empty): Result<Request, Failure> =
            Request(
                channel = channel,
                name = name,
                version = version,
                metadata = metadata,
                body = null
            ).success()
    }
}
