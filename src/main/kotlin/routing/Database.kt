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
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureDatabaseRouting(dbService: DBService) {

    routing {
        authenticate("auth-jwt") {
            route("/db-api") {
                configureUserRouting(dbService)
                configureSectionRouting(dbService)
                configureRoleRouting(dbService)
                configurePermissionRouting(dbService)
                configureMemberRouting(dbService)
                configureGroupRouting(dbService)
                configureEventRouting(dbService)
                configureAccountingRouting(dbService)
            }
        }
    }
}