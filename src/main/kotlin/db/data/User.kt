package com.youchunmaru.db.data

import kotlinx.serialization.Serializable

@Serializable
data class User(override val id: Int, val username: String, val password: String, val role: Role? = null) : Table
