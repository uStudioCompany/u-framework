package io.github.ustudiocompany.uframework.jdbc.test

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
import javax.sql.DataSource
import kotlin.io.path.Path

public fun DataSource.initialize(
    context: Contexts,
    changeLogFile: String = CHANGE_LOG_FILE,
    resourceAccessor: ResourceAccessor = DirectoryResourceAccessor(Path(DIRECTORY_RESOURCE_ACCESSOR_PATH)),
    liquibaseConfig: CommandScope.() -> Unit = {},
) {
    connection
        .use { connection ->
            val database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(JdbcConnection(connection))

            Scope.child(
                mapOf(
                    Scope.Attr.database.name to database,
                    Scope.Attr.resourceAccessor.name to resourceAccessor
                )
            ) {
                @Suppress("SpreadOperator")
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

private const val CHANGE_LOG_FILE: String = "liquibase.changelog-master.xml"
private const val DIRECTORY_RESOURCE_ACCESSOR_PATH: String = "./migrations"
