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

fun Application.configureGroupRouting(dbService: DBService) {
    val service = dbService.groupService
    routing {
        authenticate("auth-jwt") {
            route("/groups") {
                post("/create"){
                    doAuthOperation("admin.admin", call, dbService) {
                        val model = service.routingReceive(call)
                        val id = service.create(model)
                        call.respond(HttpStatusCode.Created, id.value)
                    }
                }
                get("/{id}") {
                    doAuthOperation("${DBPermissions.GROUPS}.${CRUDPermissions.READ}", call, dbService) {
                        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                        val model = service.read(id)
                        if (model != null) {
                            call.respond(HttpStatusCode.OK, model)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }
                }
                put("/{id}") {
                    doAuthOperation("admin.admin", call, dbService) {
                        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                        val model = service.routingReceive(call)
                        service.update(id, model)
                        call.respond(HttpStatusCode.OK)
                    }
                }
                delete ("/{id}"){
                    doAuthOperation("admin.admin", call, dbService) {
                        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                        service.delete(id)
                        call.respond(HttpStatusCode.OK)
                    }
                }
                get ("/all"){
                    doAuthOperation("${DBPermissions.GROUPS}.${CRUDPermissions.READ}", call, dbService) {
                        val models = service.readAll()
                        call.respond(HttpStatusCode.OK, models)
                    }
                }
            }
        }

    }
}