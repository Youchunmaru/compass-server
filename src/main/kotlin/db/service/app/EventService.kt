package db.service.app

import com.youchunmaru.db.DBService
import com.youchunmaru.db.data.app.Event
import com.youchunmaru.db.service.CrudService
import com.youchunmaru.db.service.app.GroupService
import com.youchunmaru.db.service.app.MemberService
import com.youchunmaru.db.service.app.SectionService
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Date

class EventService(val dbService: DBService): CrudService<EventService.Events, Event>(Events) {


    object Events : IntIdTable() {
        val name = varchar("name", 50)
        val startDate = long("start_date")
        val endDate = long("end_date")
        val group = reference("group", GroupService.Groups.id).nullable()
        val section = reference("section", SectionService.Sections.id).nullable()
    }

    object EventMembers : Table() {
        val event = reference("event", Events.id, onDelete = ReferenceOption.CASCADE)
        val member = reference("member", MemberService.Members.id, onDelete = ReferenceOption.CASCADE)
    }

    init {
        transaction(dbService.database) {
            SchemaUtils.create(EventMembers)
            SchemaUtils.create(Events)
        }
    }

    override fun toModel(row: ResultRow): Event {
        return Event(
            id = row[Events.id].value,
            name = row[Events.name],
            startDate = Date(132L),
            endDate = Date(123L)
        )
    }

    override fun Events.map(builder: UpdateBuilder<*>, model: Event) {
        builder[name] = model.name
    }

    override suspend fun routingReceive(call: RoutingCall): Event {
        return call.receive<Event>()
    }
}