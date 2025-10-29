package com.youchunmaru.db.service.app

import com.youchunmaru.db.DBService
import com.youchunmaru.db.data.app.Section
import com.youchunmaru.db.service.CrudService
import com.youchunmaru.db.service.TableService
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class SectionService(val dbService: DBService): CrudService<SectionService.Sections, Section>(Sections){


    object Sections : IntIdTable() {
        val name = varchar("name", 50).uniqueIndex()
        val color = varchar("color", 50)
    }

    init {
        transaction(dbService.database) {
            SchemaUtils.create(Sections)
        }
    }

    override fun toModel(row: ResultRow): Section {
        return Section(
            id = row[Sections.id].value,
            name = row[Sections.name],
            color = row[Sections.color]
        )
    }

    override fun Sections.map(builder: UpdateBuilder<*>, model: Section) {
        builder[name] = model.name
    }

    override suspend fun routingReceive(call: RoutingCall): Section {
        return call.receive<Section>()
    }


}