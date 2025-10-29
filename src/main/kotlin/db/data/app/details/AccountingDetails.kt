package com.youchunmaru.db.data.app.details

import com.youchunmaru.db.data.app.Event
import com.youchunmaru.db.data.app.Group
import com.youchunmaru.db.data.app.Section
import kotlinx.serialization.Serializable

@Serializable
data class AccountingDetails(val group: Group? = null, val section: Section? = null, val event: Event? = null)
