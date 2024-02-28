package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.functional.kotest.shouldBeError
import io.github.airflux.functional.kotest.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.AbstractSQLDatabaseTest
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.*

@Suppress("LargeClass")
internal class RowIT : AbstractSQLDatabaseTest() {

    init {

        "The Row type" - {
            truncateTable(TABLE_NAME)
            executeSql(
                """
                   | INSERT INTO $TABLE_NAME (
                   |    $BOOLEAN_COLUMN_NAME,
                   |    $STRING_COLUMN_NAME,
                   |    $INT_COLUMN_NAME,
                   |    $LONG_COLUMN_NAME,
                   |    $UUID_COLUMN_NAME)
                   | VALUES (
                   |     $BOOLEAN_COLUMN_VALUE,
                   |     '$STRING_COLUMN_VALUE'::text,
                   |     $INT_COLUMN_VALUE,
                   |     $LONG_COLUMN_VALUE,
                   |     '$UUID_COLUMN_VALUE'::uuid
                   | );
                    """.trimMargin()
            )

            "when calling the `getBoolean` method" - {

                "when finding by a column index" - {

                    "when a column index is known" - {

                        "for column is a boolean type" - {
                            val index = BOOLEAN_COLUMN_INDEX

                            "then should return the value" {
                                val result = executeQuery { getBoolean(index) }
                                result.shouldBeSuccess()
                                result.value shouldBe BOOLEAN_COLUMN_VALUE
                            }
                        }

                        "for column is a string type" - {
                            val index = STRING_COLUMN_INDEX

                            "then should return the error" {
                                val result = executeQuery { getBoolean(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a int type" - {
                            val index = INT_COLUMN_INDEX

                            "then should return the error" {
                                val result = executeQuery { getBoolean(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a long type" - {
                            val index = LONG_COLUMN_INDEX

                            "then should return the error" {
                                val result = executeQuery { getBoolean(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a UUID type" - {
                            val index = UUID_COLUMN_INDEX

                            "then should return the error" {
                                val result = executeQuery { getBoolean(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }
                    }

                    "when a column index is not known" - {
                        val index = UNKNOWN_COLUMN_INDEX

                        "then the called method should return an error" - {
                            val result = executeQuery { getBoolean(index) }

                            result.shouldBeError()
                            val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Rows.UndefinedColumn>()
                            val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                            label.get shouldBe index
                        }
                    }
                }

                "when finding by a column name" - {

                    "when a column name is known" - {

                        "for column is a boolean type" - {

                            "then should return the value" {
                                val result = executeQuery { getBoolean(BOOLEAN_COLUMN_NAME) }
                                result.shouldBeSuccess()
                                result.value shouldBe BOOLEAN_COLUMN_VALUE
                            }
                        }

                        "for column is a string type" - {
                            val columnName = STRING_COLUMN_NAME

                            "then should return the error" {
                                val result = executeQuery { getBoolean(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a int type" - {
                            val columnName = INT_COLUMN_NAME

                            "then should return the error" {
                                val result = executeQuery { getBoolean(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a long type" - {
                            val columnName = LONG_COLUMN_NAME

                            "then should return the error" {
                                val result = executeQuery { getBoolean(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a UUID type" - {
                            val columnName = UUID_COLUMN_NAME

                            "then should return the error" {
                                val result = executeQuery { getBoolean(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }
                    }

                    "when a column name is not known" - {
                        val columnName = UNKNOWN_COLUMN_NAME

                        "then the called method should return an error" - {
                            val result = executeQuery { getBoolean(columnName) }

                            result.shouldBeError()
                            val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Rows.UndefinedColumn>()
                            val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                            label.get shouldBe columnName
                        }
                    }
                }
            }

            "when calling the `getString` method" - {

                "when finding by a column index" - {

                    "when a column index is known" - {

                        "for column is a boolean type" - {
                            val index = BOOLEAN_COLUMN_INDEX

                            "then should return the value" {
                                val result = executeQuery { getString(index) }

                                result.shouldBeSuccess()
                                result.value shouldBe "t"
                            }
                        }

                        "for column is a string type" - {
                            val index = STRING_COLUMN_INDEX

                            "then should return the value" {
                                val result = executeQuery { getString(index) }

                                result.shouldBeSuccess()
                                result.value shouldBe STRING_COLUMN_VALUE
                            }
                        }

                        "for column is a int type" - {
                            val index = INT_COLUMN_INDEX

                            "then should return the int value as string" {
                                val result = executeQuery { getString(index) }

                                result.shouldBeSuccess()
                                result.value shouldBe INT_COLUMN_VALUE.toString()
                            }
                        }

                        "for column is a long type" - {
                            val index = LONG_COLUMN_INDEX

                            "then should return the int value as string" {
                                val result = executeQuery { getString(index) }

                                result.shouldBeSuccess()
                                result.value shouldBe LONG_COLUMN_VALUE.toString()
                            }
                        }

                        "for column is a UUID type" - {
                            val index = UUID_COLUMN_INDEX

                            "then should return the int value as string" {
                                val result = executeQuery { getString(index) }

                                result.shouldBeSuccess()
                                result.value shouldBe UUID_COLUMN_VALUE.toString()
                            }
                        }
                    }

                    "when a column index is not known" - {
                        val index = UNKNOWN_COLUMN_INDEX

                        "then the called method should return an error" - {
                            val result = executeQuery { getString(index) }

                            result.shouldBeError()
                            val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Rows.UndefinedColumn>()
                            val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                            label.get shouldBe index
                        }
                    }
                }

                "when finding by a column name" - {

                    "when a column name is known" - {

                        "for column is a boolean type" - {
                            val columnName = BOOLEAN_COLUMN_NAME

                            "then should return the value" {
                                val result = executeQuery { getString(columnName) }

                                result.shouldBeSuccess()
                                result.value shouldBe "t"
                            }
                        }

                        "for column is a string type" - {
                            val columnName = STRING_COLUMN_NAME

                            "then should return the value" {
                                val result = executeQuery { getString(columnName) }

                                result.shouldBeSuccess()
                                result.value shouldBe STRING_COLUMN_VALUE
                            }
                        }

                        "for column is a int type" - {
                            val columnName = INT_COLUMN_NAME

                            "then should return the int value as string" {
                                val result = executeQuery { getString(columnName) }

                                result.shouldBeSuccess()
                                result.value shouldBe INT_COLUMN_VALUE.toString()
                            }
                        }

                        "for column is a long type" - {
                            val columnName = LONG_COLUMN_NAME

                            "then should return the int value as string" {
                                val result = executeQuery { getString(columnName) }

                                result.shouldBeSuccess()
                                result.value shouldBe LONG_COLUMN_VALUE.toString()
                            }
                        }

                        "for column is a UUID type" - {
                            val columnName = UUID_COLUMN_NAME

                            "then should return the int value as string" {
                                val result = executeQuery { getString(columnName) }

                                result.shouldBeSuccess()
                                result.value shouldBe UUID_COLUMN_VALUE.toString()
                            }
                        }
                    }

                    "when a column name is not known" - {
                        val columnName = UNKNOWN_COLUMN_NAME

                        "then the called method should return an error" - {
                            val result = executeQuery { getString(columnName) }

                            result.shouldBeError()
                            val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Rows.UndefinedColumn>()
                            val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                            label.get shouldBe columnName
                        }
                    }
                }
            }

            "when calling the `getInt` method" - {

                "when finding by a column index" - {

                    "when a column index is known" - {

                        "for column is a boolean type" - {
                            val index = BOOLEAN_COLUMN_INDEX

                            "then should return the value" {
                                val result = executeQuery { getInt(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a string type" - {
                            val index = STRING_COLUMN_INDEX

                            "then should return the error" {
                                val result = executeQuery { getInt(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a int type" - {
                            val index = INT_COLUMN_INDEX

                            "then should return the error" {
                                val result = executeQuery { getInt(index) }

                                result.shouldBeSuccess()
                                result.value shouldBe INT_COLUMN_VALUE
                            }
                        }

                        "for column is a long type" - {
                            val index = LONG_COLUMN_INDEX

                            "then should return the error" {
                                val result = executeQuery { getInt(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a UUID type" - {
                            val index = UUID_COLUMN_INDEX

                            "then should return the error" {
                                val result = executeQuery { getInt(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }
                    }

                    "when a column index is not known" - {
                        val index = UNKNOWN_COLUMN_INDEX

                        "then the called method should return an error" - {
                            val result = executeQuery { getInt(index) }

                            result.shouldBeError()
                            val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Rows.UndefinedColumn>()
                            val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                            label.get shouldBe index
                        }
                    }
                }

                "when finding by a column name" - {

                    "when a column name is known" - {

                        "for column is a boolean type" - {
                            val columnName = BOOLEAN_COLUMN_NAME

                            "then should return the value" {
                                val result = executeQuery { getInt(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a string type" - {
                            val columnName = STRING_COLUMN_NAME

                            "then should return the error" {
                                val result = executeQuery { getInt(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a int type" - {

                            "then should return the error" {
                                val result = executeQuery { getInt(INT_COLUMN_NAME) }

                                result.shouldBeSuccess()
                                result.value shouldBe INT_COLUMN_VALUE
                            }
                        }

                        "for column is a long type" - {
                            val columnName = LONG_COLUMN_NAME

                            "then should return the error" {
                                val result = executeQuery { getInt(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a UUID type" - {
                            val columnName = UUID_COLUMN_NAME

                            "then should return the error" {
                                val result = executeQuery { getInt(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }
                    }

                    "when a column name is not known" - {
                        val columnName = UNKNOWN_COLUMN_NAME

                        "then the called method should return an error" - {
                            val result = executeQuery { getInt(columnName) }

                            result.shouldBeError()
                            val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Rows.UndefinedColumn>()
                            val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                            label.get shouldBe columnName
                        }
                    }
                }
            }

            "when calling the `getLong` method" - {

                "when finding by a column index" - {

                    "when a column index is known" - {

                        "for column is a boolean type" - {
                            val index = BOOLEAN_COLUMN_INDEX

                            "then should return the value" {
                                val result = executeQuery { getLong(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a string type" - {
                            val index = STRING_COLUMN_INDEX

                            "then should return the error" {
                                val result = executeQuery { getLong(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a int type" - {
                            val index = INT_COLUMN_INDEX

                            "then should return the value" {
                                val result = executeQuery { getLong(index) }

                                result.shouldBeSuccess()
                                result.value shouldBe INT_COLUMN_VALUE
                            }
                        }

                        "for column is a long type" - {
                            val index = LONG_COLUMN_INDEX

                            "then should return the value" {
                                val result = executeQuery { getLong(index) }

                                result.shouldBeSuccess()
                                result.value shouldBe LONG_COLUMN_VALUE
                            }
                        }

                        "for column is a UUID type" - {
                            val index = UUID_COLUMN_INDEX

                            "then should return the error" {
                                val result = executeQuery { getLong(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }
                    }

                    "when a column index is not known" - {
                        val index = UNKNOWN_COLUMN_INDEX

                        "then the called method should return an error" - {
                            val result = executeQuery { getLong(index) }

                            result.shouldBeError()
                            val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Rows.UndefinedColumn>()
                            val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                            label.get shouldBe index
                        }
                    }
                }

                "when finding by a column name" - {

                    "when a column name is known" - {

                        "for column is a boolean type" - {
                            val columnName = BOOLEAN_COLUMN_NAME

                            "then should return the value" {
                                val result = executeQuery { getLong(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a string type" - {
                            val columnName = STRING_COLUMN_NAME

                            "then should return the error" {
                                val result = executeQuery { getLong(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a int type" - {

                            "then should return the value" {
                                val result = executeQuery { getLong(INT_COLUMN_NAME) }

                                result.shouldBeSuccess()
                                result.value shouldBe INT_COLUMN_VALUE
                            }
                        }

                        "for column is a long type" - {

                            "then should return the value" {
                                val result = executeQuery { getLong(LONG_COLUMN_NAME) }

                                result.shouldBeSuccess()
                                result.value shouldBe LONG_COLUMN_VALUE
                            }
                        }

                        "for column is a UUID type" - {
                            val columnName = UUID_COLUMN_NAME

                            "then should return the error" {
                                val result = executeQuery { getLong(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }
                    }

                    "when a column name is not known" - {
                        val columnName = UNKNOWN_COLUMN_NAME

                        "then the called method should return an error" - {
                            val result = executeQuery { getLong(columnName) }

                            result.shouldBeError()
                            val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Rows.UndefinedColumn>()
                            val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                            label.get shouldBe columnName
                        }
                    }
                }
            }

            "when calling the `getUUID` method" - {

                "when finding by a column index" - {

                    "when a column index is known" - {

                        "for column is a boolean type" - {
                            val index = BOOLEAN_COLUMN_INDEX

                            "then should return the value" {
                                val result = executeQuery { getUUID(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a string type" - {
                            val index = STRING_COLUMN_INDEX

                            "then should return the error" {
                                val result = executeQuery { getUUID(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a int type" - {
                            val index = INT_COLUMN_INDEX

                            "then should return the value" {
                                val result = executeQuery { getUUID(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a long type" - {
                            val index = LONG_COLUMN_INDEX

                            "then should return the value" {
                                val result = executeQuery { getUUID(index) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                                label.get shouldBe index
                            }
                        }

                        "for column is a UUID type" - {
                            val index = UUID_COLUMN_INDEX

                            "then should return the value" {
                                val result = executeQuery { getUUID(index) }

                                result.shouldBeSuccess()
                                result.value shouldBe UUID_COLUMN_VALUE
                            }
                        }
                    }

                    "when a column index is not known" - {
                        val index = UNKNOWN_COLUMN_INDEX

                        "then the called method should return an error" - {
                            val result = executeQuery { getUUID(index) }

                            result.shouldBeError()
                            val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Rows.UndefinedColumn>()
                            val label = cause.label.shouldBeInstanceOf<ColumnLabel.Index>()
                            label.get shouldBe index
                        }
                    }
                }

                "when finding by a column name" - {

                    "when a column name is known" - {

                        "for column is a boolean type" - {
                            val columnName = BOOLEAN_COLUMN_NAME

                            "then should return the value" {
                                val result = executeQuery { getUUID(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a string type" - {
                            val columnName = STRING_COLUMN_NAME

                            "then should return the error" {
                                val result = executeQuery { getUUID(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a int type" - {
                            val columnName = INT_COLUMN_NAME

                            "then should return the value" {
                                val result = executeQuery { getUUID(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a long type" - {
                            val columnName = LONG_COLUMN_NAME

                            "then should return the value" {
                                val result = executeQuery { getUUID(columnName) }

                                result.shouldBeError()
                                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.ReadColumn>()
                                val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                                label.get shouldBe columnName
                            }
                        }

                        "for column is a UUID type" - {
                            val columnName = UUID_COLUMN_NAME

                            "then should return the value" {
                                val result = executeQuery { getUUID(columnName) }

                                result.shouldBeSuccess()
                                result.value shouldBe UUID_COLUMN_VALUE
                            }
                        }
                    }

                    "when a column name is not known" - {
                        val columnName = UNKNOWN_COLUMN_NAME

                        "then the called method should return an error" - {
                            val result = executeQuery { getUUID(columnName) }

                            result.shouldBeError()
                            val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Rows.UndefinedColumn>()
                            val label = cause.label.shouldBeInstanceOf<ColumnLabel.Name>()
                            label.get shouldBe columnName
                        }
                    }
                }
            }
        }
    }

    private fun <T> executeQuery(block: Row.() -> T): T =
        dataSource.connection
            .use { connection ->
                val statement = connection.prepareStatement(SQL)
                val resultSet = statement.executeQuery()
                val rows = Rows(resultSet)
                val row = rows.first()
                block(row)
            }

    companion object {
        private const val TABLE_NAME = "test_row_table"
        private const val UNKNOWN_COLUMN_NAME = "unknown_column"

        private const val BOOLEAN_COLUMN_NAME = "boolean_column"
        private const val BOOLEAN_COLUMN_VALUE = true

        private const val STRING_COLUMN_NAME = "string_column"
        private const val STRING_COLUMN_VALUE = "string_value"

        private const val INT_COLUMN_NAME = "int_column"
        private const val INT_COLUMN_VALUE = Int.MAX_VALUE

        private const val LONG_COLUMN_NAME = "long_column"
        private const val LONG_COLUMN_VALUE = Long.MAX_VALUE

        private const val UUID_COLUMN_NAME = "uuid_column"
        private val UUID_COLUMN_VALUE = UUID.randomUUID()

        private const val UNKNOWN_COLUMN_INDEX = 0
        private const val BOOLEAN_COLUMN_INDEX = 1
        private const val STRING_COLUMN_INDEX = 2
        private const val INT_COLUMN_INDEX = 3
        private const val LONG_COLUMN_INDEX = 4
        private const val UUID_COLUMN_INDEX = 5

        private val SQL = """
            | SELECT $BOOLEAN_COLUMN_NAME, 
            |        $STRING_COLUMN_NAME,
            |        $INT_COLUMN_NAME,
            |        $LONG_COLUMN_NAME,
            |        $UUID_COLUMN_NAME
            |   FROM $TABLE_NAME
        """.trimMargin()
    }
}
