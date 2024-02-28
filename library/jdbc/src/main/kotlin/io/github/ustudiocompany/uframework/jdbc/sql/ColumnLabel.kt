package io.github.ustudiocompany.uframework.jdbc.sql

public sealed interface ColumnLabel {
    public val detailsKey: String
    public val detailsValue: String

    public class Name(public val get: String) : ColumnLabel {
        override val detailsKey: String = "column-name"
        override val detailsValue: String = get
    }

    public class Index(public val get: Int) : ColumnLabel {
        override val detailsKey: String = "column-index"
        override val detailsValue: String = get.toString()
    }
}
