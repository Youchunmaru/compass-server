package com.youchunmaru.routing

import com.youchunmaru.db.DBService
import com.youchunmaru.routing.db.configureAccountingRouting
import com.youchunmaru.routing.db.configureEventRouting
import com.youchunmaru.routing.db.configureGroupRouting
import com.youchunmaru.routing.db.configureMemberRouting
import com.youchunmaru.routing.db.configurePermissionRouting
import com.youchunmaru.routing.db.configureRoleRouting
import com.youchunmaru.routing.db.configureSectionRouting
import com.youchunmaru.routing.db.configureUserRouting
import io.ktor.server.application.Application

fun Application.configureDatabaseRouting(dbService: DBService) {

    configureUserRouting(dbService)
    configureSectionRouting(dbService)
    configureRoleRouting(dbService)
    configurePermissionRouting(dbService)
    configureMemberRouting(dbService)
    configureGroupRouting(dbService)
    configureEventRouting(dbService)
    configureAccountingRouting(dbService)
}