package com.youchunmaru.db.service.app

import com.youchunmaru.db.DBService
import com.youchunmaru.db.data.app.Accounting
import com.youchunmaru.db.service.CrudService
import com.youchunmaru.db.service.TableService
import db.service.app.EventService
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class AccountingService(val dbService: DBService): CrudService<AccountingService.Accountings, Accounting>(Accountings) {


    object Accountings : IntIdTable() {
        val label = varchar("label", 50)
        val amount = integer("amount")
        val receiptId = varchar("receipt_id", 50)
        val group = reference("group", GroupService.Groups.id).nullable()
        val section = reference("section", SectionService.Sections.id).nullable()
        val event = reference("event", EventService.Events.id).nullable()
    }

    init {
        transaction(dbService.database) {
            SchemaUtils.create(Accountings)
        }
    }

    override fun toModel(row: ResultRow): Accounting {
        return Accounting(
            id = row[Accountings.id].value,
            label = row[Accountings.label],
            amount = 0,
            receiptId = ""
        )
    }

    override fun Accountings.map(builder: UpdateBuilder<*>, model: Accounting) {
        builder[label] = model.label
    }

    override suspend fun routingReceive(call: RoutingCall): Accounting {
        return call.receive<Accounting>()
    }
}