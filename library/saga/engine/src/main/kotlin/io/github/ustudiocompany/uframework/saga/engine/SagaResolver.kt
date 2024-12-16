package io.github.ustudiocompany.uframework.saga.engine

import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.saga.core.Saga
import io.github.ustudiocompany.uframework.saga.core.SagaLabel
import io.github.ustudiocompany.uframework.saga.core.extension.toMessageName
import io.github.ustudiocompany.uframework.saga.core.extension.toMessageVersion

public class SagaResolver private constructor(
    private val resolverForCommand: Map<CommandResolverKey, Saga<*>>,
    private val resolverForSagaInstance: Map<SagaLabel, Saga<*>>
) {

    public fun resolve(name: MessageName, version: MessageVersion): Saga<*>? =
        resolverForCommand[CommandResolverKey(name = name, version = version)]

    public fun resolve(label: SagaLabel): Saga<*>? = resolverForSagaInstance[label]

    public class Builder {
        private val resolverForCommand = mutableMapOf<CommandResolverKey, Saga<*>>()
        private val resolverForSagaInstance = mutableMapOf<SagaLabel, Saga<*>>()

        public fun <DATA> register(name: String, version: String, saga: Saga<DATA>): Unit =
            register(name.toMessageName(), version.toMessageVersion(), saga)

        public fun <DATA> register(name: MessageName, version: MessageVersion, saga: Saga<DATA>) {
            addToResolverForCommand(name, version, saga)
            addToResolverForSagaInstance(saga)
        }

        public fun build(): SagaResolver = SagaResolver(resolverForCommand, resolverForSagaInstance)

        private fun addToResolverForCommand(name: MessageName, version: MessageVersion, saga: Saga<*>) {
            val key = CommandResolverKey(name, version)
            resolverForCommand[key] = saga
        }

        private fun addToResolverForSagaInstance(saga: Saga<*>) {
            resolverForSagaInstance[saga.definition.label] = saga
        }
    }

    private class CommandResolverKey(
        val name: MessageName,
        val version: MessageVersion
    ) : Comparable<CommandResolverKey> {

        override fun equals(other: Any?): Boolean =
            this === other || other is CommandResolverKey && this.name == other.name && this.version == other.version

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + version.hashCode()
            return result
        }

        override fun compareTo(other: CommandResolverKey): Int {
            val byName = name.compareTo(other.name)
            return if (byName != 0) byName else version.compareTo(other.version)
        }
    }
}
