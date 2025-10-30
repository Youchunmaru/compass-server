package com.youchunmaru.db.service

import com.youchunmaru.db.data.Table
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.ops.InListOrNotInListBaseOp
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

abstract class CrudService<T: IntIdTable, M: Table> (protected val table: T): TableService<M> {

    // Abstract function to be implemented by subclasses to map a ResultRow to the data model.
    abstract fun toModel(row: ResultRow): M

    // Abstract function to be implemented by subclasses to map a data model to an insert/update statement.
    // This is used for both create and update operations.
    // Important: dont add id of M
    abstract fun T.map(builder: UpdateBuilder<*>, model: M)

    /**
     * Creates a new record in the database.
     * @param model The data model instance to insert.
     * @return The ID of the newly created entity.
     */
    open fun create(model: M): EntityID<Int> = transaction {
        table.insertAndGetId {
            table.map(it, model)
        }
    }

    /**
     * Reads all records from the table.
     * @return A list of all data model instances.
     */
    open fun readAll(): List<M> = transaction {
        table.selectAll().map(::toModel)
    }

    /**
     * Reads a single record by its ID.
     * @param id The ID of the entity to retrieve.
     * @return The data model instance, or null if not found.
     */
    open fun read(id: Int): M? = transaction {
        table.selectAll().where { table.id eq id }
            .map(::toModel)
            .singleOrNull()
    }

    /**
     * Reads a single record by its ID.
     * @param id The ID of the entity to retrieve.
     * @return The data model instance, or null if not found.
     */
    open fun readWithFilters(id: Int, filters: List<Pair<Column<String>, List<String>>>): M? = transaction {
        val usableFilters = filters.filter { table.columns.contains(it.first) }
        table.selectAll().where { table.id eq id and
                (usableFilters.map { it.first inList it.second }
                    .map { it as Op<Boolean> }
                    .reduce { acc, op -> acc and op }) }
            .map(::toModel)
            .singleOrNull()
    }

    /**
     * Updates an existing record in the database.
     * @param id The ID of the entity to update.
     * @param model The data model instance with updated values.
     */
    open fun update(id: Int, model: M) {
        transaction {
            table.update({ table.id eq id }) {
                table.map(it, model)
            }
        }
    }

    /**
     * Deletes a record from the database by its ID.
     * @param id The ID of the entity to delete.
     */
    open fun delete(id: Int) {
        transaction {
            table.deleteWhere { table.id eq id }
        }
    }
}