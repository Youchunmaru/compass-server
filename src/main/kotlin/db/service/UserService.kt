package com.youchunmaru.db.service


import com.youchunmaru.db.DBService
import com.youchunmaru.db.data.Role
import com.youchunmaru.db.data.User
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class UserService(val dbService: DBService): CrudService<UserService.Users, User> (Users){

    object Users : IntIdTable() {
        val username = varchar("username", length = 50).uniqueIndex()
        val password = varchar("password", length = 255)
        val role = reference("role", RoleService.Roles.id).nullable()
    }

    init {
        transaction(dbService.database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun readByUsernameWithRoleWithPermissions(username: String): User?{
        return transaction {
            val user = (table leftJoin RoleService.Roles)
                .selectAll()
                .where { Users.username eq username }
                .map(::toModel)
                .singleOrNull()

            if (user?.role == null) {
                return@transaction user
            }

            val role = dbService.roleService.readWithPermissions(user.role.id)
            user.copy(role = role!!)
        }
    }

    fun readWithRole(id: Int): User? = transaction {

        val user = (table leftJoin RoleService.Roles)
            .selectAll()
            .where { Users.id eq id }
            .map(::toModel)
            .singleOrNull()

        if (user?.role == null) {
            return@transaction user
        }
        val role = dbService.roleService.read(user.role.id)
        user.copy(role = role!!)
    }

    fun readAllWithRole(): List<User> {
        return transaction {
            val users = (table leftJoin RoleService.Roles)
                .selectAll()
                .map(::toModel)

            users.map { user ->
                if (user.role == null) {//todo optimize
                    return@map user
                }
                val role = dbService.roleService.read(user.role.id)
                user.copy(role = role!!)
            }
        }
    }

    private fun toRole(row: ResultRow): Role? {
        if (row.getOrNull(RoleService.Roles.id) == null) {
            return null
        }
        return dbService.roleService.toModel(row);
    }

    override fun toModel(row: ResultRow): User = User (
        id = row[Users.id].value,
        password = row[Users.password],
        username = row[Users.username],
        role = toRole(row),
    )

    override fun Users.map(builder: UpdateBuilder<*>, model: User) {
        builder[username] = model.username
        builder[password] = model.password
        builder[role] = model.role?.id
    }

    override suspend fun routingReceive(call: RoutingCall): User {
        return call.receive<User>()
    }
}
