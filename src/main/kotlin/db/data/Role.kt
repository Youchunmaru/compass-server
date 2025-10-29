package com.youchunmaru.db.data

import kotlinx.serialization.Serializable

@Serializable
data class Role(override val id: Int, val name: String, val permissions: List<Permission> = emptyList()): Table