package com.titaniel.zerobasedbudgetingapp.datamanager

/**
 * Represents a budgeting category.
 * @param manualBudgetedMoney Map mapping a month identifying key to the budgeted value.
 * @param transactionSums Map mapping a month identifying key to the sum of transaction values of that month.
 * @param name Category name.
 */
data class Category(
    val manualBudgetedMoney: Map<Long, Long>,
    val transactionSums: Map<Long, Long>,
    val name: String
)