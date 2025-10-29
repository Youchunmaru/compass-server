package com.youchunmaru.db.data

import kotlinx.serialization.Serializable

@Serializable
data class Permission(override val id: Int, val name: String): Table