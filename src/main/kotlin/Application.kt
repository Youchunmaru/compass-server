package com.youchunmaru

import com.youchunmaru.db.DBService
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    val dbService = DBService(environment)
    configureSecurity(dbService)
    configureRouting(dbService)
}
