package io.github.ustudiocompany.uframework.eventsourcing.store

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.initialize
import io.github.ustudiocompany.uframework.test.kotest.TestTags
import io.kotest.core.spec.style.FreeSpec
import liquibase.Contexts
import liquibase.resource.DirectoryResourceAccessor
import kotlin.io.path.Path

internal abstract class AbstractEventSourcingStoreTest : FreeSpec({ tags(TestTags.All, TestTags.Integration) }) {

    protected val container = PostgresContainerTest().apply {
        initialize(
            context = Contexts("test"),
            resourceAccessor = DirectoryResourceAccessor(Path("library/eventsourcing/store/migrations"))
        )
    }

    protected val mapper = ObjectMapper()
}
