package com.youchunmaru.db.data.app

import com.youchunmaru.db.data.Table
import com.youchunmaru.db.data.app.details.EventDetails
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.sql.Date

@Serializable
data class Event(override val id: Int, val name: String, @Contextual val startDate: Date, @Contextual val endDate: Date, val eventDetails: EventDetails? = null): Table
