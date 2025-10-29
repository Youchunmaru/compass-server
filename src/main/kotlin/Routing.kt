package com.youchunmaru

import com.youchunmaru.db.DBService
import com.youchunmaru.routing.configureAppRouting
import com.youchunmaru.routing.configureDatabaseRouting
import io.ktor.server.application.*

fun Application.configureRouting(dbService: DBService) {

    configureDatabaseRouting(dbService)
    configureAppRouting(dbService)

}
