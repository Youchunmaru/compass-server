package com.youchunmaru.db.service.app

import com.youchunmaru.db.DBService
import com.youchunmaru.db.data.app.Group
import com.youchunmaru.db.service.CrudService
import com.youchunmaru.db.service.TableService
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class GroupService(val dbService: DBService): CrudService<GroupService.Groups, Group> (Groups) {


    object Groups : IntIdTable() {
        val name = varchar("name", 50).uniqueIndex()
    }

    init {
        transaction(dbService.database) {
            SchemaUtils.create(Groups)
        }
    }

    override fun toModel(row: ResultRow): Group {
        return Group(
            id = row[Groups.id].value,
            name = row[Groups.name]
        )
    }

    override fun Groups.map(builder: UpdateBuilder<*>, model: Group) {
        builder[name] = model.name
    }

    override suspend fun routingReceive(call: RoutingCall): Group {
        return call.receive<Group>()
    }
}