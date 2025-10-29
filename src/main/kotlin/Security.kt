package com.youchunmaru

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.youchunmaru.data.Session
import com.youchunmaru.data.LoginResponse
import com.youchunmaru.data.UserCredentials
import com.youchunmaru.db.DBService
import com.youchunmaru.db.data.Permission
import com.youchunmaru.db.data.Role
import com.youchunmaru.db.data.User
import com.youchunmaru.db.data.app.Group
import com.youchunmaru.util.DBPermissions
import com.youchunmaru.util.PasswordEncoder
import com.youchunmaru.util.getAuthUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json
import java.util.Date

fun Application.configureSecurity(dbService: DBService) {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val millis30days = 2_592_000_000//30 * 24 * 60 * 60 * 1000L
    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("user").asString() != "") JWTPrincipal(credential.payload) else null
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Invalid JWT token")
            }
        }
        jwt("auth-jwt-init") {//init auth - used to create auth session for initial setup
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString() == "init") JWTPrincipal(credential.payload) else null
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Invalid JWT token")
            }
        }
    }
    install(Sessions) {
        cookie<Session>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    routing {
        post("/login") {
            val user = call.receive<UserCredentials>()
            var authorized = false
            if(user.username.isNotEmpty() && user.password.isNotEmpty()) {
                val dbUser = dbService.userService.readByUsernameWithRoleWithPermissions(user.username)

                if (dbUser != null) {
                    if (PasswordEncoder.verifyPassword(user.password, dbUser.password)) {
                        authorized = true

                        val token = JWT.create()
                            .withAudience(jwtAudience)
                            .withIssuer(jwtIssuer)
                            .withClaim("user", Json.encodeToString(dbUser))//save user in claim for trusted auth - user obj to skip db query
                            .withExpiresAt(Date(System.currentTimeMillis() + millis30days))
                            .sign(Algorithm.HMAC256(jwtSecret))

                        call.respond(LoginResponse(token, dbUser.copy(password = "")))
                    }
                }
            }
            if(!authorized) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
            }
        }
        authenticate("auth-jwt") {
            post("login/validate"){
                val user = call.receive<User>()
                val tokenUser = getAuthUser(call)
                if(user.id == tokenUser?.id){
                    call.respond(HttpStatusCode.OK)
                }else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid user")
                }
            }
        }

        get("/init/auth"){
            val groups = dbService.groupService.readAll()
            val users = dbService.userService.readAll()

            if(groups.isEmpty() && users.isEmpty()) {
                val token = JWT.create()
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .withClaim("username", "init")
                    .withExpiresAt(Date(System.currentTimeMillis() + (millis30days / 30)))
                    .sign(Algorithm.HMAC256(jwtSecret))

                call.respond(LoginResponse(token, User(0, "init", "", Role(0, "INIT"))))
            }else {
                call.respond(HttpStatusCode.Forbidden, "App already initialized")
            }

        }
        authenticate("auth-jwt-init") {
            post("/init/{group}"){
                val group = call.parameters["group"] ?: throw IllegalArgumentException("Invalid group name")
                val groups = dbService.groupService.readAll()
                if(groups.isEmpty()){

                    val admin = call.receive<UserCredentials>()
                    val role = Role(0, "ADMIN")
                    val roleId = dbService.roleService.create(role)

                    val id = dbService.userService.create(User(
                        id = 0,
                        username = admin.username,
                        password = PasswordEncoder.encode(admin.password),
                        role = role.copy(id = roleId.value)))

                    for(dbPermission in DBPermissions.entries){
                        dbService.permissionService.create(
                            Permission(0, "${dbPermission.name}.*"))
                        for(crudPermission in dbPermission.crudPermissions){
                            dbService.permissionService.create(
                                Permission(0, "${dbPermission.name}.${crudPermission.name}"))
                        }
                    }

                    dbService.groupService.create(Group(0, group))
                    call.respond(HttpStatusCode.Created, id)
                }else {
                    call.respond(HttpStatusCode.Forbidden, "App already initialized")
                }
            }
        }

        /*get("/session/increment") {
            val session = call.sessions.get<Session>() ?: Session()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }*/
    }
}

