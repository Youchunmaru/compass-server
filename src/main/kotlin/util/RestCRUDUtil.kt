package com.youchunmaru.util

import com.youchunmaru.db.data.Table
import com.youchunmaru.db.service.CrudService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import org.jetbrains.exposed.dao.id.IntIdTable

suspend inline fun <T: IntIdTable, reified M: Table> create(service: CrudService<T,M>, call: RoutingCall) {
    val model = service.routingReceive(call)
    val id = service.create(model)
    call.respond(HttpStatusCode.Created, id.value)
}

suspend inline fun <T: IntIdTable, reified M: Table> read(service: CrudService<T,M>, call: RoutingCall) {
    val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
    val model = service.read(id)
    if (model != null) {
        call.respond(HttpStatusCode.OK, model)
    } else {
        call.respond(HttpStatusCode.NotFound)
    }
}

suspend inline fun <T: IntIdTable, reified M: Table> update(service: CrudService<T,M>, call: RoutingCall) {
    val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
    val model = service.routingReceive(call)
    service.update(id, model)
    call.respond(HttpStatusCode.OK)
}

suspend inline fun <T: IntIdTable, reified M: Table> delete(service: CrudService<T,M>, call: RoutingCall) {
    val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
    service.delete(id)
    call.respond(HttpStatusCode.OK)
}

suspend inline fun <T: IntIdTable, reified M: Table> readAll(service: CrudService<T,M>, call: RoutingCall) {
    val models = service.readAll()
    call.respond(HttpStatusCode.OK, models)
}