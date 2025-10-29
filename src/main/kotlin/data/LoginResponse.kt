package com.youchunmaru.data

import com.youchunmaru.db.data.User
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val token: String, val user: User)
