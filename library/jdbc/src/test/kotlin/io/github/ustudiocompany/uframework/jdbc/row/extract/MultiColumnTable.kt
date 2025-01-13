package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.entries

internal fun columnTypes(vararg types: MultiColumnTable) = types.toList()

internal enum class MultiColumnTable(
    val columnIndex: Int,
    val columnName: String,
    val displayType: String,
    val dataType: String
) {
    BOOLEAN(columnIndex = 1, columnName = "boolean_column", displayType = "BOOLEAN", dataType = "bool"),
    TEXT(columnIndex = 2, columnName = "text_column", displayType = "TEXT", dataType = "text"),
    VARCHAR(columnIndex = 3, columnName = "varchar_column", displayType = "VARCHAR(4)", dataType = "varchar"),
    CHAR(columnIndex = 4, columnName = "char_column", displayType = "CHAR(4)", dataType = "bpchar"),
    INTEGER(columnIndex = 5, columnName = "integer_column", displayType = "INTEGER", dataType = "int4"),
    BIGINT(columnIndex = 6, columnName = "bigint_column", displayType = "BIGINT", dataType = "int8"),
    TIMESTAMP(columnIndex = 7, columnName = "timestamp_column", displayType = "TIMESTAMP", dataType = "timestamp"),
    UUID(columnIndex = 8, columnName = "uuid_column", displayType = "UUID", dataType = "uuid"),
    JSONB(columnIndex = 9, columnName = "jsonb_column", displayType = "JSONB", dataType = "jsonb"),
    ;

    companion object {
        private const val EMPTY_ROW_ID = 1

        const val ROW_ID_COLUMN_NAME = "row_id"
        const val MULTI_COLUMN_TABLE_NAME = "public.multi_column_table"

        private val COLUMNS = entries.sortedBy { it.columnIndex }
            .map { Triple(it.columnIndex, it.columnName, it.displayType) }

        fun makeCreateTableSql() = COLUMNS.joinToString(
            prefix = "CREATE TABLE $MULTI_COLUMN_TABLE_NAME ($ROW_ID_COLUMN_NAME INTEGER PRIMARY KEY,",
            postfix = ");",
        ) { (_, columnName, type) ->
            "$columnName $type"
        }

        fun makeInsertEmptyRowSql() =
            "INSERT INTO $MULTI_COLUMN_TABLE_NAME ($ROW_ID_COLUMN_NAME) VALUES ($EMPTY_ROW_ID)"

        fun makeSelectEmptyRowSql() = COLUMNS.joinToString(
            prefix = "SELECT ",
            postfix = " FROM $MULTI_COLUMN_TABLE_NAME WHERE $ROW_ID_COLUMN_NAME = $EMPTY_ROW_ID;",
        ) { (_, columnName, _) -> columnName }

        fun makeSelectAllColumnsSql(rowId: Int) = COLUMNS.joinToString(
            prefix = "SELECT ",
            postfix = " FROM $MULTI_COLUMN_TABLE_NAME WHERE $ROW_ID_COLUMN_NAME = $rowId;",
        ) { (_, columnName, _) -> columnName }

        fun getColumnsExclude(vararg exclude: MultiColumnTable): List<MultiColumnTable> =
            getColumnsExclude(exclude.toList())

        fun getColumnsExclude(exclude: Iterable<MultiColumnTable>): List<MultiColumnTable> =
            entries.filter { it !in exclude }
    }
}
