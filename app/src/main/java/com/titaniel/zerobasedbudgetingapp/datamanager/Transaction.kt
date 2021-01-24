package com.titaniel.zerobasedbudgetingapp.datamanager

/**
 * Data class representing a transaction
 * @param value Transaction value
 * @param payee Payee
 * @param category Category
 * @param description Description
 * @param utcTimestamp Timestamp
 */
data class Transaction(
    val value: Long,
    val payee: String,
    val category: String,
    val description: String,
    val utcTimestamp: Long
)