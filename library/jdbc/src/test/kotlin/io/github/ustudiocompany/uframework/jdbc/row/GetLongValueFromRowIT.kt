package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.functional.kotest.shouldBeError
import io.github.airflux.functional.kotest.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class GetLongValueFromRowIT : AbstractRowTest() {

    init {

        "The `getLong` method" - {
            truncateTable(TABLE_NAME)
            executeSql(INSERT_QUERY)

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

                    "for column is a timestamp type" - {
                        val index = TIMESTAMP_COLUMN_INDEX

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
                        val columnName = INT_COLUMN_NAME

                        "then should return the value" {
                            val result = executeQuery { getLong(columnName) }

                            result.shouldBeSuccess()
                            result.value shouldBe INT_COLUMN_VALUE
                        }
                    }

                    "for column is a long type" - {
                        val columnName = LONG_COLUMN_NAME

                        "then should return the value" {
                            val result = executeQuery { getLong(columnName) }

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

                    "for column is a timestamp type" - {
                        val columnName = TIMESTAMP_COLUMN_NAME

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
    }
}
