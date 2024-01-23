package io.github.ustudiocompany.uframework.saga.manager

import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.saga.Saga
import io.github.ustudiocompany.uframework.saga.SagaLabel
import io.github.ustudiocompany.uframework.saga.internal.toMessageName
import io.github.ustudiocompany.uframework.saga.internal.toMessageVersion

public class SagaResolver private constructor(
    private val resolverForCommand: Map<CommandKey, Saga<*>>,
    private val resolverForSagaInstance: Map<SagaLabel, Saga<*>>
) {

    public fun resolve(name: MessageName, version: MessageVersion): Saga<*>? =
        resolverForCommand[CommandKey(name = name, version = version)]

    public fun resolve(label: SagaLabel): Saga<*>? = resolverForSagaInstance[label]

    public class Builder {
        private val resolverForCommand = mutableMapOf<CommandKey, Saga<*>>()
        private val resolverForSagaInstance = mutableMapOf<SagaLabel, Saga<*>>()

        public fun <DATA> register(name: String, version: String, saga: Saga<DATA>): Unit =
            register(name.toMessageName(), version.toMessageVersion(), saga)

        public fun <DATA> register(name: MessageName, version: MessageVersion, saga: Saga<DATA>) {
            val commandKey = CommandKey(name = name, version = version)
            resolverForCommand[commandKey] = saga
            resolverForSagaInstance[saga.definition.label] = saga
        }

        public fun build(): SagaResolver = SagaResolver(resolverForCommand, resolverForSagaInstance)
    }

    private data class CommandKey(val name: MessageName, val version: MessageVersion)
}
