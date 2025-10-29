package com.youchunmaru.db.data.app

import com.youchunmaru.db.data.Table
import com.youchunmaru.db.data.app.details.GroupDetails
import kotlinx.serialization.Serializable

@Serializable
data class Group(override val id: Int, val name: String, val groupDetails: GroupDetails? = null): Table
