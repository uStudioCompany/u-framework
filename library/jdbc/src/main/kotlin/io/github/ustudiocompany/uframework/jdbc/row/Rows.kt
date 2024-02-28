package io.github.ustudiocompany.uframework.jdbc.row

import java.sql.ResultSet

public class Rows(private val resultSet: ResultSet) : Iterable<Row> {

    override fun iterator(): Iterator<Row> = ResultSetIterator()

    private inner class ResultSetIterator : AbstractIterator<Row>() {
        override fun computeNext() {
            if (resultSet.next())
                setNext(Row(resultSet))
            else
                done()
        }
    }
}
