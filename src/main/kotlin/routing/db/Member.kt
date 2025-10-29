package com.youchunmaru.routing.db

import com.youchunmaru.db.DBService
import com.youchunmaru.db.data.Table
import com.youchunmaru.db.service.CrudService
import com.youchunmaru.util.CRUDPermissions
import com.youchunmaru.util.DBPermissions
import com.youchunmaru.util.doAuthOperation
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.jetbrains.exposed.dao.id.IntIdTable

fun Application.configureMemberRouting(dbService: DBService) {

    val service = dbService.memberService
    routing {
        authenticate("auth-jwt") {
            route("/members") {

                post("/create"){
                    doAuthOperation("${DBPermissions.MEMBERS}.${CRUDPermissions.CREATE}", call, dbService) {
                        val model = service.routingReceive(call)
                        val id = service.create(model)
                        call.respond(HttpStatusCode.Created, id.value)
                    }
                }
                get("/{id}") {
                    doAuthOperation("${DBPermissions.MEMBERS}.${CRUDPermissions.READ}", call, dbService) {
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
                    doAuthOperation("${DBPermissions.MEMBERS}.${CRUDPermissions.UPDATE}", call, dbService) {
                        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                        val model = service.routingReceive(call)
                        service.update(id, model)
                        call.respond(HttpStatusCode.OK)
                    }
                }
                delete ("/{id}"){
                    doAuthOperation("${DBPermissions.MEMBERS}.${CRUDPermissions.DELETE}", call, dbService) {
                        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                        service.delete(id)
                        call.respond(HttpStatusCode.OK)
                    }
                }
                get ("/all"){
                    doAuthOperation("${DBPermissions.MEMBERS}.${CRUDPermissions.READ}", call, dbService) {
                        val models = service.readAllWithDetails()
                        call.respond(HttpStatusCode.OK, models)
                    }
                }
            }
        }
    }
}