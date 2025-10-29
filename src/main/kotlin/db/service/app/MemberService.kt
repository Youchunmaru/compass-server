package com.youchunmaru.db.service.app

import com.youchunmaru.db.DBService
import com.youchunmaru.db.data.app.Group
import com.youchunmaru.db.data.app.Member
import com.youchunmaru.db.data.app.details.MemberDetails
import com.youchunmaru.db.service.CrudService
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Date

class MemberService(val dbService: DBService): CrudService<MemberService.Members, Member>(Members) {


    object Members : IntIdTable() {
        val firstName = varchar("first_name", 50)
        val lastName = varchar("last_name", 50)
        val birthDate = long("birth_date")
        val group = reference("group", GroupService.Groups.id)
    }

    object MemberSections : Table() {
        val member = reference("member", Members.id, onDelete = ReferenceOption.CASCADE)
        val section = reference("section", SectionService.Sections.id, onDelete = ReferenceOption.CASCADE)
    }

    init {
        transaction(dbService.database) {
            SchemaUtils.create(MemberSections)
            SchemaUtils.create(Members)
        }
    }

    fun readAllWithDetails(): List<Member> {//todo optimize
        return transaction {
            val members = super.readAll()
            val membersReturn = mutableListOf<Member>()
            for ((index, member) in members.withIndex()) {
                val sections = (MemberSections innerJoin SectionService.Sections)
                    .selectAll()
                    .where { MemberSections.member eq member.id }
                    .map {it -> dbService.sectionService.toModel(it)}
                membersReturn.add(index, member.copy(memberDetails = MemberDetails(Group(0,""), sections)))
            }
            membersReturn
        }
    }

    override fun toModel(row: ResultRow): Member {
        return Member(
            id = row[Members.id].value,
            firstName = row[Members.firstName],
            lastName = row[Members.lastName],
            birthDate = Instant.fromEpochMilliseconds(row[Members.birthDate]),
        )
    }

    override fun Members.map(builder: UpdateBuilder<*>, model: Member) {
        builder[firstName] = model.firstName
    }

    override suspend fun routingReceive(call: RoutingCall): Member {
        return call.receive<Member>()
    }
}