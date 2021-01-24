package com.titaniel.zerobasedbudgetingapp.transaction

import com.titaniel.zerobasedbudgetingapp.budget.Category

/**
 * Data class representing a transaction
 * @param value Transaction value
 * @param payee Payee
 * @param description Description
 * @param utcTimestamp Timestamp
 * @param category Category
 */
data class Transaction(
    val value: Long,
    val payee: String,
    val description: String,
    val utcTimestamp: Long,
    val category: Category
)