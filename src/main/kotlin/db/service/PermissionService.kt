package com.youchunmaru.db.service

import com.youchunmaru.db.DBService
import com.youchunmaru.db.data.Permission
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class PermissionService (val dbService: DBService): CrudService<PermissionService.Permissions, Permission> (Permissions) {

    object Permissions : IntIdTable() {
        val name = varchar("name", length = 50).uniqueIndex()
    }

    init {
        transaction(dbService.database) {
            SchemaUtils.create(Permissions)
        }
    }

    override fun toModel(row: ResultRow): Permission = Permission (
        id = row[Permissions.id].value,
        name = row[Permissions.name],
    )

    override fun Permissions.map(builder: UpdateBuilder<*>, model: Permission) {
        builder[name] = model.name
    }

    override suspend fun routingReceive(call: RoutingCall): Permission {
        return call.receive<Permission>()
    }
}