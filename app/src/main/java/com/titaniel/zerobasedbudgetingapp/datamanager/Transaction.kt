package com.titaniel.zerobasedbudgetingapp.datamanager

import java.util.*

/**
 * Data class representing a transaction
 * @param value Transaction value
 * @param payee Payee
 * @param category Category
 * @param description Description
 * @param utcTimestamp Timestamp
 */
data class Transaction(
    var value: Long,
    var payee: String,
    var category: String,
    var description: String,
    var utcTimestamp: Long
) {
    val uuid = UUID.randomUUID().toString()
}