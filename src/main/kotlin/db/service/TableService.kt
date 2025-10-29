package com.youchunmaru.db.service

import io.ktor.server.routing.RoutingCall
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface TableService<M> {

    suspend fun <M> dbQuery(block: suspend () -> M): M =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun routingReceive(call: RoutingCall) : M
}