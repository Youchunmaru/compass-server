package com.youchunmaru.db.data.app

import com.youchunmaru.db.data.Table
import com.youchunmaru.db.data.app.details.MemberDetails
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class Member(override val id: Int, val firstName: String, val lastName: String, val birthDate: Instant, val memberDetails: MemberDetails? = null): Table