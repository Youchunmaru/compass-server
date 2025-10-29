package com.youchunmaru.db.data.app.details

import com.youchunmaru.db.data.app.Member
import kotlinx.serialization.Serializable

@Serializable
data class GroupDetails(val members: List<Member>)
