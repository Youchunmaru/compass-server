package com.youchunmaru.data

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(val username: String, val password: String)
