package io.github.ustudiocompany.uframework.jdbc.row.extract

internal fun columnTypes(vararg types: MultiColumnTable) = types.toList()

internal enum class MultiColumnTable(val columnIndex: Int, val columnName: String, val dataType: String) {
    BOOLEAN(columnIndex = 1, columnName = "boolean_column", dataType = "BOOLEAN"),
    TEXT(columnIndex = 2, columnName = "text_column", dataType = "TEXT"),
    VARCHAR(columnIndex = 3, columnName = "varchar_column", dataType = "VARCHAR(4)"),
    CHAR(columnIndex = 4, columnName = "char_column", dataType = "CHAR(4)"),
    INTEGER(columnIndex = 5, columnName = "integer_column", dataType = "INTEGER"),
    BIGINT(columnIndex = 6, columnName = "bigint_column", dataType = "BIGINT"),
    TIMESTAMP(columnIndex = 7, columnName = "timestamp_column", dataType = "TIMESTAMP"),
    UUID(columnIndex = 8, columnName = "uuid_column", dataType = "UUID"),
    ;

    companion object {
        private const val EMPTY_ROW_ID = 1

        const val ROW_ID_COLUMN_NAME = "row_id"
        const val MULTI_COLUMN_TABLE_NAME = "public.multi_column_table"

        private val columns = entries.sortedBy { it.columnIndex }
            .map { Triple(it.columnIndex, it.columnName, it.dataType) }

        fun makeCreateTableSql() = columns.joinToString(
            prefix = "CREATE TABLE $MULTI_COLUMN_TABLE_NAME ($ROW_ID_COLUMN_NAME INTEGER PRIMARY KEY,",
            postfix = ");",
        ) { (_, columnName, type) ->
            "$columnName $type"
        }

        fun makeInsertEmptyRowSql() =
            "INSERT INTO $MULTI_COLUMN_TABLE_NAME ($ROW_ID_COLUMN_NAME) VALUES ($EMPTY_ROW_ID)"

        fun makeSelectEmptyRowSql() = columns.joinToString(
            prefix = "SELECT ",
            postfix = " FROM $MULTI_COLUMN_TABLE_NAME WHERE $ROW_ID_COLUMN_NAME = $EMPTY_ROW_ID;",
        ) { (_, columnName, _) -> columnName }

        fun makeSelectAllColumnsSql(rowId: Int) = columns.joinToString(
            prefix = "SELECT ",
            postfix = " FROM $MULTI_COLUMN_TABLE_NAME WHERE $ROW_ID_COLUMN_NAME = $rowId;",
        ) { (_, columnName, _) -> columnName }

        fun getColumnsExclude(vararg exclude: MultiColumnTable): List<MultiColumnTable> =
            entries.filter { it !in exclude }
    }
}
