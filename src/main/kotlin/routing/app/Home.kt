package com.youchunmaru.routing.app

import com.youchunmaru.db.DBService
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.configureHomeRouting(dbService: DBService) {
    route("/home"){
        post ("/"){

        }
    }
}