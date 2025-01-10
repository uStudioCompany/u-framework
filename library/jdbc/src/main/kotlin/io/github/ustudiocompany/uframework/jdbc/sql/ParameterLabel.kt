package io.github.ustudiocompany.uframework.jdbc.sql

public sealed interface ParameterLabel {
    public val detailsKey: String
    public val detailsValue: String

    public data class Name(public val get: String) : ParameterLabel {
        override val detailsKey: String = "statement-param-name"
        override val detailsValue: String = get
    }

    public data class Index(public val get: Int) : ParameterLabel {
        override val detailsKey: String = "statement-param-index"
        override val detailsValue: String = get.toString()
    }
}
