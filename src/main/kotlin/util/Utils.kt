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
    if (role.permissions.find { it.name == permission } != null) {
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

fun getFilter(permission: String): Pair<Column<String>, String>{
    val filterConfig = permission.split("(")[1]
        .split(")")[0]
        .split("::")
    when (StringFilter.valueOf(filterConfig[0])) {
        StringFilter.SECTION_NAME -> return (StringFilter.SECTION_NAME.column to filterConfig[1])
    }
}
