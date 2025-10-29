package com.youchunmaru.db.data.app

import com.youchunmaru.db.data.Table
import com.youchunmaru.db.data.app.details.SectionDetails
import kotlinx.serialization.Serializable

@Serializable
data class Section(override val id: Int, val name: String, val color: String, val sectionDetails: SectionDetails? = null): Table