package me.koddle.repositories

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.sqlclient.preparedQueryAwait
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.Tuple
import me.koddle.exceptions.ModelNotFoundException
import me.koddle.json.jArr
import me.koddle.json.jObj
import me.koddle.tools.DatabaseAccess
import org.koin.core.KoinComponent
import org.koin.core.inject


abstract class Repository(val table: String, val schema: String) : KoinComponent {

    val tableName = "$schema.$table"
    private val da: DatabaseAccess by inject()

    suspend fun all(connection: SqlClient? = null): JsonArray {
        return queryWithSqlClient("SELECT * FROM $tableName", Tuple.tuple(), connection).getRows()
    }

    suspend fun find(id: String, connection: SqlClient? = null): JsonObject {
        val result = queryWithSqlClient("SELECT * FROM $tableName WHERE id = $1", Tuple.of(id), connection).getRow();
        if (result.isEmpty)
            throw ModelNotFoundException("No object found with ID", jArr(id))
        return result
    }

    suspend fun insert(data: JsonObject, connection: SqlClient? = null): JsonObject {
        return queryWithSqlClient("INSERT INTO $tableName (data) VALUES ($1::jsonb) RETURNING *", Tuple.of(data), connection).getRow()
    }

    suspend fun update(id: String, data: JsonObject, connection: SqlClient? = null): JsonObject {
        return queryWithSqlClient("UPDATE $tableName SET data = $1 WHERE id = $2 RETURNING *", Tuple.of(data, id), connection).getRow()
    }

    suspend fun delete(id: String, connection: SqlClient? = null): JsonObject {
        val deleted = queryWithSqlClient("DELETE FROM $tableName WHERE id = $1 RETURNING id", Tuple.of(id), connection).getRow()
        if (deleted.isEmpty)
            throw ModelNotFoundException("Tried to delete an item that does not exist", jArr(id))
        return deleted
    }

    suspend fun query(sql: String, tuple: Tuple, connection: SqlClient? = null): JsonArray = queryWithSqlClient(sql, tuple, connection).getRows()

    suspend fun queryOne(sql: String, tuple: Tuple, connection: SqlClient? = null): JsonObject = queryWithSqlClient(sql, tuple, connection).getRow()

    suspend fun queryWithSqlClient(sql: String, tuple: Tuple, connection: SqlClient?): RowSet<Row> {
        return when (connection) {
            null -> da.getConnection { conn -> conn.preparedQueryAwait(sql, tuple) }
            else -> connection.preparedQueryAwait(sql, tuple)
        }
    }

    fun RowSet<Row>.getRow(): JsonObject {
        return this.map { row -> jsonRow(row, this.columnsNames()) }.firstOrNull() ?: jObj()
    }

    fun RowSet<Row>.getRows(): JsonArray {
        return jArr(this.map { row -> jsonRow(row, this.columnsNames()) })
    }

    private fun jsonRow(row: Row, columnNames: List<String>): JsonObject {
        val json = if (columnNames.contains("data"))
            row.getValue("data") as JsonObject
        else
            jObj()

        columnNames.forEachIndexed { i, s ->
            if (s != "data")
                json.put(s, row.getValue(i))
        }
        return json
    }
}