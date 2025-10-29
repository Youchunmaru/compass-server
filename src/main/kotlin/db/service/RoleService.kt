package com.youchunmaru.db.service

import com.youchunmaru.db.DBService
import com.youchunmaru.db.data.Role
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class RoleService(val dbService: DBService) : CrudService<RoleService.Roles, Role> (Roles){

    object Roles : IntIdTable() {
        val name = varchar("name", length = 50).uniqueIndex()
    }

    object RolePermissions : Table() {
        val role = reference("role", Roles.id, onDelete = ReferenceOption.CASCADE)
        val permission = reference("permission", PermissionService.Permissions.id, onDelete = ReferenceOption.CASCADE)

        override val primaryKey = PrimaryKey(role, permission)
    }

    init {
        transaction(dbService.database) {
            SchemaUtils.create(Roles)
            SchemaUtils.create(RolePermissions)
        }
    }

    fun createWithPermissions(model: Role): EntityID<Int> {
        return transaction {
            val roleId = super.create(model)

            RolePermissions.batchInsert(model.permissions) {
                this[RolePermissions.role] = roleId
                this[RolePermissions.permission] = it.id
            }

            roleId
        }
    }

    fun updateWithPermissions(id: Int, model: Role) {
        return transaction {
            super.update(id, model)

            RolePermissions.deleteWhere { RolePermissions.role eq id }

            RolePermissions.batchInsert(model.permissions) {
                this[RolePermissions.role] = id
                this[RolePermissions.permission] = it.id
            }
        }
    }

    fun readWithPermissions(id: Int): Role? {
        return transaction {
            val role = super.read(id) ?: return@transaction null

            val permissions = (RolePermissions innerJoin PermissionService.Permissions)
                .selectAll()
                .where {RolePermissions.role eq id}
                .map {dbService.permissionService.toModel(it)}

            role.copy(permissions = permissions)
        }
    }

    fun readAllWithPermissions(): List<Role> {
        return transaction {
            val roles = super.readAll()
            //todo optimize
            roles.map { readWithPermissions(it.id)!! }
        }
    }

    override fun toModel(row: ResultRow): Role = Role (
        id = row[Roles.id].value,
        name = row[Roles.name],
    )

    override fun Roles.map(builder: UpdateBuilder<*>, model: Role) {
        builder[name] = model.name
    }

    override suspend fun routingReceive(call: RoutingCall): Role {
        return call.receive<Role>()
    }
}