package com.youchunmaru.db.data.app.details

import com.youchunmaru.db.data.app.Group
import com.youchunmaru.db.data.app.Section
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.sql.Date

@Serializable
data class MemberDetails(val group: Group, val sections: List<Section> = emptyList())
