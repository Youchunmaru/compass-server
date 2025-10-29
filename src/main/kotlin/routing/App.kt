package com.youchunmaru.routing

import com.youchunmaru.db.DBService
import com.youchunmaru.routing.app.configureHomeRouting
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureAppRouting(dbService: DBService) {
    routing {
        authenticate("auth-jwt") {
            route("/api") {
                configureHomeRouting(dbService)
            }
        }
    }
}