package com.youchunmaru.db.data.app.details

import com.youchunmaru.db.data.Permission
import com.youchunmaru.db.data.app.Member
import kotlinx.serialization.Serializable

@Serializable
data class SectionDetails(val members: List<Member>, val permission: Permission? = null)
