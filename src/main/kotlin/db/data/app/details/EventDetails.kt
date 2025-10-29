package com.youchunmaru.db.data.app.details

import com.youchunmaru.db.data.app.Group
import com.youchunmaru.db.data.app.Member
import com.youchunmaru.db.data.app.Section
import kotlinx.serialization.Serializable

@Serializable
data class EventDetails(val participants: List<Member>, val group: Group? = null, val section: Section? = null)
