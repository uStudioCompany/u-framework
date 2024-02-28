package io.github.ustudiocompany.uframework.jdbc

import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.failure
import io.kotest.core.extensions.install
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Scope
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.visitor.ChangeExecListener
import liquibase.command.CommandScope
import liquibase.command.core.UpdateCommandStep
import liquibase.command.core.helpers.ChangeExecListenerCommandStep
import liquibase.command.core.helpers.DatabaseChangelogCommandStep
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import liquibase.resource.ResourceAccessor
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet
import kotlin.io.path.Path

public abstract class AbstractSQLDatabaseTest(
    private val changeLogFile: String = CHANGE_LOG_FILE,
    private val context: Contexts = Contexts(CONTEXT),
    private val resourceAccessor: ResourceAccessor = DirectoryResourceAccessor(Path(DIRECTORY_RESOURCE_ACCESSOR_PATH)),
    private val liquibaseConfig: CommandScope.() -> Unit = {}
) : FreeSpec() {

    private val postgresql = PostgreSQLContainer<Nothing>("postgres:15.4").apply {
        startupAttempts = 1
    }

    protected val dataSource: HikariDataSource = install(JdbcDatabaseContainerExtension(postgresql))

    override suspend fun beforeSpec(spec: Spec) {
        dataSource.connection.use { connection ->
            val database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(JdbcConnection(connection))

            Scope.child(
                mapOf(
                    Scope.Attr.database.name to database,
                    Scope.Attr.resourceAccessor.name to resourceAccessor
                )
            ) {
                val updateCommand = CommandScope(*UpdateCommandStep.COMMAND_NAME)
                    .apply {
                        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database)
                        addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, changeLogFile)
                        addArgumentValue(UpdateCommandStep.CONTEXTS_ARG, context.toString())
                        addArgumentValue(UpdateCommandStep.LABEL_FILTER_ARG, LabelExpression().originalString)
                        addArgumentValue(
                            ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG,
                            null as ChangeExecListener?
                        )
                        addArgumentValue(
                            DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS,
                            ChangeLogParameters(database)
                        )

                        liquibaseConfig(this)
                    }
                updateCommand.execute()
            }
        }
    }

    private companion object {
        private const val CHANGE_LOG_FILE: String = "liquibase.changelog-master.xml"
        private const val CONTEXT: String = "test"
        private const val DIRECTORY_RESOURCE_ACCESSOR_PATH: String = "./migrations"
    }

    protected fun truncateTable(name: String) {
        executeSql("TRUNCATE TABLE $name CASCADE")
    }

    protected fun executeSql(value: String) {
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(value).execute()
            }
    }

    protected fun <T> checkData(sql: String, assertions: ResultSet.(index: Int) -> T) {
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        var index = 0
                        while (result.next()) {
                            assertions(result, index++)
                        }
                    }
            }
    }

    protected fun selectData(sql: String): List<ResultSet> =
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        val list = mutableListOf<ResultSet>()
                        while (result.next()) {
                            list.add(result)
                        }
                        list
                    }
            }

    protected fun <T> shouldContain(sql: String, assertion: ResultSet.(index: Int) -> T) {
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        var index = 0
                        while (result.next()) {
                            assertion(result, index++)
                        }
                    }
            }
    }

    protected fun shouldContainExactly(sql: String, vararg assertions: ResultSet.(index: Int) -> Unit) {
        val assertionCount = assertions.size
        var currentAssertionIndex = 0
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        var index = 0
                        while (result.next()) {
                            if (currentAssertionIndex != assertionCount) {
                                val assertion = assertions[currentAssertionIndex]
                                assertion(result, index++)
                                currentAssertionIndex++
                            } else
                                failure("There are fewer assertions than rows.")
                        }
                    }
            }
    }
}
