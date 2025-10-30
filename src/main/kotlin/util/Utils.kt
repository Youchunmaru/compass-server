package com.youchunmaru.util

import com.youchunmaru.data.DatabaseConfig
import com.youchunmaru.db.DBService
import com.youchunmaru.db.data.Role
import com.youchunmaru.db.data.User
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Column

fun getDatabaseConfig(environment: ApplicationEnvironment): DatabaseConfig {
    val url = environment.config.property("db.url").getString()
    val user = environment.config.property("db.user").getString()
    val driver = environment.config.property("db.driver").getString()
    val password = environment.config.property("db.password").getString()
    return DatabaseConfig(url, user, password, driver)
}

fun getAuthUser(call: RoutingCall): User? {
    val userString = call.authentication.principal<JWTPrincipal>()?.getClaim("user", String::class)
    val user = Json.decodeFromString<User>(userString ?: return null)
    return user
}

fun checkPermission(role: Role, permission: String): Boolean {
    if (role.name == "ADMIN") {
        return true
    }
    if (role.permissions.find { it.name == permission } != null) {//todo add parsing for filter
        return true
    }
    return role.permissions.find {it.name.split(".")[1] == "*"} != null
}

suspend fun doAuthOperation(permission: String, call: RoutingCall, dbService: DBService, operation: suspend () -> Any){
    val user = getAuthUser(call)
    if (user?.role == null) {
        call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
        return
    }
    if (checkPermission(user.role, permission)) {
        operation()
    }else {
        call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
    }
}
/**
 * PERMISSION.SUB_PERMISSION(FILTER_1::VALUE_1,VALUE_2;FILTER_2::VALUE_3)
 *
 * only varchar columns are supported for filters
 * */
fun getFilters(permission: String): List<Pair<Column<String>, List<String>>>{//adds on top of the standard permission system
    val filters = mutableListOf<Pair<Column<String>, List<String>>>()
    val filterConfigs = permission.split("(")[1]
        .split(")")[0]
        .split(";")
    for (filterConfig in filterConfigs) {
        if (filterConfig.isEmpty()) {
            continue
        }
        val filter = filterConfig.split("::")
        filters.add(StringFilter.valueOf(filter[0]).column to filter[1].split(","))
    }
    return filters
}
