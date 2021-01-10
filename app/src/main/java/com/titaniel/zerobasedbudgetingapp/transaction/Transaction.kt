package com.titaniel.zerobasedbudgetingapp.transaction

import com.titaniel.zerobasedbudgetingapp.budget.Category
import java.util.*

/**
 * Data class representing a transaction
 */
data class Transaction(
    val value: Long,
    val payee: String,
    val description: String,
    val utcTimestamp: Long,
    val category: Category
)