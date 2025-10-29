package com.youchunmaru.db.data.app

import com.youchunmaru.db.data.Table
import com.youchunmaru.db.data.app.details.AccountingDetails
import kotlinx.serialization.Serializable

@Serializable
data class Accounting(override val id: Int, val label: String, val amount: Int, val receiptId: String, val accountingDetails: AccountingDetails? = null): Table
