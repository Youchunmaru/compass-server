package com.youchunmaru.routing.db

import com.youchunmaru.db.DBService
import com.youchunmaru.util.CRUDPermissions
import com.youchunmaru.util.DBPermissions
import com.youchunmaru.util.doAuthOperation
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import com.youchunmaru.util.create
import com.youchunmaru.util.delete
import com.youchunmaru.util.read
import com.youchunmaru.util.readAll
import com.youchunmaru.util.update
import io.ktor.server.routing.Route

fun Route.configureEventRouting(dbService: DBService) {
    val service = dbService.eventService
    route("/events") {
        post("/create"){
            doAuthOperation("${DBPermissions.EVENTS}.${CRUDPermissions.CREATE}", call, dbService) {
                create(service, call)
            }
        }
        get("/{id}") {
            doAuthOperation("${DBPermissions.EVENTS}.${CRUDPermissions.READ}", call, dbService) {
                read(service, call)
            }
        }
        put("/{id}") {
            doAuthOperation("${DBPermissions.EVENTS}.${CRUDPermissions.UPDATE}", call, dbService) {
                update(service, call)
            }
        }
        delete ("/{id}"){
            doAuthOperation("${DBPermissions.EVENTS}.${CRUDPermissions.DELETE}", call, dbService) {
                delete(service, call)
            }
        }
        get ("/all"){
            doAuthOperation("${DBPermissions.EVENTS}.${CRUDPermissions.READ}", call, dbService) {
                readAll(service, call)
            }
        }
    }
}