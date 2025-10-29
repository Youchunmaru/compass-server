package com.youchunmaru.db

import com.youchunmaru.db.data.Table
import com.youchunmaru.db.service.CrudService
import com.youchunmaru.db.service.PermissionService
import com.youchunmaru.db.service.RoleService
import com.youchunmaru.db.service.UserService
import com.youchunmaru.db.service.app.AccountingService
import com.youchunmaru.db.service.app.GroupService
import com.youchunmaru.db.service.app.MemberService
import com.youchunmaru.db.service.app.SectionService
import com.youchunmaru.util.getDatabaseConfig
import db.service.app.EventService
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database

class DBService(applicationEnvironment: ApplicationEnvironment){

    val database: Database
    val userService: UserService
    val roleService: RoleService
    val permissionService: PermissionService
    val accountingService: AccountingService
    val groupService: GroupService
    val sectionService: SectionService
    val eventService: EventService
    val memberService: MemberService

    val serviceMap = HashMap<String, CrudService<out IntIdTable, out Table>>()

    init {
        val config = getDatabaseConfig(applicationEnvironment)
        database = Database.connect(
            url = config.url,
            driver = config.driver,
            user = config.user,
            password = config.password
        )
        userService = UserService(this)
        roleService = RoleService(this)
        permissionService = PermissionService(this)
        accountingService = AccountingService(this)
        groupService = GroupService(this)
        sectionService = SectionService(this)
        eventService = EventService(this)
        memberService = MemberService(this)
        serviceMap["accounting"] = accountingService
        serviceMap["group"] = groupService
        serviceMap["section"] = sectionService
        serviceMap["event"] = eventService
        serviceMap["member"] = memberService
        serviceMap["user"] = userService
        serviceMap["role"] = roleService
        serviceMap["permission"] = permissionService
        serviceMap["accounting"] = accountingService
    }

}