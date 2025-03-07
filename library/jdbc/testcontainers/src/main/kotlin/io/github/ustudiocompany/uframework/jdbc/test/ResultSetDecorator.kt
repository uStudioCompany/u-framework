package io.github.ustudiocompany.uframework.jdbc.test

import io.kotest.assertions.failure
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.sql.Array
import java.sql.Blob
import java.sql.Clob
import java.sql.Date
import java.sql.NClob
import java.sql.Ref
import java.sql.ResultSet
import java.sql.RowId
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp

internal class ResultSetDecorator(private val rows: ResultSet) : ResultSet by rows {

    override fun next(): Boolean {
        throw failure(ERROR_MESSAGE)
    }

    override fun close() {
        throw failure(ERROR_MESSAGE)
    }

    override fun setFetchDirection(direction: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun getFetchDirection(): Int {
        throw failure(ERROR_MESSAGE)
    }

    override fun setFetchSize(rows: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNull(columnIndex: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBoolean(columnIndex: Int, x: Boolean) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateByte(columnIndex: Int, x: Byte) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateShort(columnIndex: Int, x: Short) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateInt(columnIndex: Int, x: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateLong(columnIndex: Int, x: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateFloat(columnIndex: Int, x: Float) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateDouble(columnIndex: Int, x: Double) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBigDecimal(columnIndex: Int, x: BigDecimal?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateString(columnIndex: Int, x: String?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBytes(columnIndex: Int, x: ByteArray?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateDate(columnIndex: Int, x: Date?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateTime(columnIndex: Int, x: Time?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateTimestamp(columnIndex: Int, x: Timestamp?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?, length: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?, length: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?, length: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateObject(columnIndex: Int, x: Any?, scaleOrLength: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateObject(columnIndex: Int, x: Any?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNull(columnLabel: String?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBoolean(columnLabel: String?, x: Boolean) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateByte(columnLabel: String?, x: Byte) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateShort(columnLabel: String?, x: Short) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateInt(columnLabel: String?, x: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateLong(columnLabel: String?, x: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateFloat(columnLabel: String?, x: Float) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateDouble(columnLabel: String?, x: Double) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBigDecimal(columnLabel: String?, x: BigDecimal?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateString(columnLabel: String?, x: String?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBytes(columnLabel: String?, x: ByteArray?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateDate(columnLabel: String?, x: Date?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateTime(columnLabel: String?, x: Time?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateTimestamp(columnLabel: String?, x: Timestamp?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?, length: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?, length: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?, length: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateObject(columnLabel: String?, x: Any?, scaleOrLength: Int) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateObject(columnLabel: String?, x: Any?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun insertRow() {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateRow() {
        throw failure(ERROR_MESSAGE)
    }

    override fun deleteRow() {
        throw failure(ERROR_MESSAGE)
    }

    override fun refreshRow() {
        throw failure(ERROR_MESSAGE)
    }

    override fun cancelRowUpdates() {
        throw failure(ERROR_MESSAGE)
    }

    override fun moveToInsertRow() {
        throw failure(ERROR_MESSAGE)
    }

    override fun moveToCurrentRow() {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateRef(columnIndex: Int, x: Ref?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateRef(columnLabel: String?, x: Ref?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBlob(columnIndex: Int, x: Blob?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBlob(columnLabel: String?, x: Blob?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateClob(columnIndex: Int, x: Clob?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateClob(columnLabel: String?, x: Clob?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateArray(columnIndex: Int, x: Array?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateArray(columnLabel: String?, x: Array?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun getRowId(columnIndex: Int): RowId? {
        throw failure(ERROR_MESSAGE)
    }

    override fun getRowId(columnLabel: String?): RowId? {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateRowId(columnIndex: Int, x: RowId?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateRowId(columnLabel: String?, x: RowId?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun getHoldability(): Int {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNString(columnIndex: Int, nString: String?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNString(columnLabel: String?, nString: String?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNClob(columnIndex: Int, nClob: NClob?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNClob(columnLabel: String?, nClob: NClob?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateSQLXML(columnIndex: Int, xmlObject: SQLXML?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateSQLXML(columnLabel: String?, xmlObject: SQLXML?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNCharacterStream(columnIndex: Int, x: Reader?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNCharacterStream(columnLabel: String?, reader: Reader?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBlob(columnIndex: Int, inputStream: InputStream?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBlob(columnLabel: String?, inputStream: InputStream?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateClob(columnIndex: Int, reader: Reader?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateClob(columnLabel: String?, reader: Reader?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNClob(columnIndex: Int, reader: Reader?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNClob(columnLabel: String?, reader: Reader?, length: Long) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNCharacterStream(columnIndex: Int, x: Reader?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNCharacterStream(columnLabel: String?, reader: Reader?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBlob(columnIndex: Int, inputStream: InputStream?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateBlob(columnLabel: String?, inputStream: InputStream?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateClob(columnIndex: Int, reader: Reader?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateClob(columnLabel: String?, reader: Reader?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNClob(columnIndex: Int, reader: Reader?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun updateNClob(columnLabel: String?, reader: Reader?) {
        throw failure(ERROR_MESSAGE)
    }

    override fun <T : Any?> unwrap(iface: Class<T?>?): T? {
        throw failure(ERROR_MESSAGE)
    }

    override fun isWrapperFor(iface: Class<*>?): Boolean {
        throw failure(ERROR_MESSAGE)
    }

    private companion object {
        private const val ERROR_MESSAGE = "Prohibited from use."
    }
}
