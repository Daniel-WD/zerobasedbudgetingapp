package com.titaniel.zerobasedbudgetingapp.datamanager

/**
 * Represents a budgeting category.
 * @param manualBudgetedMoney Map mapping a month identifying key to the budgeted value.
 * @param transactionSums Map mapping a month identifying key to the sum of transaction values of that month.
 * @param name Category name.
 */
data class Category(
    val manualBudgetedMoney: MutableMap<Long, Long>,
    val transactionSums: MutableMap<Long, Long>,
    val name: String
) {

    fun realBudgetedValue(monthTimestamp: Long): Long {
        val addition = { a: Long, b: Long -> a.plus(b) }
        val allSums = transactionSums
            .filter { entry -> entry.key <= monthTimestamp }
            .map { entry -> entry.value }
            .fold(0L, addition)

        val allManualMoney = manualBudgetedMoney
            .filter { entry -> entry.key <= monthTimestamp }
            .map { entry -> entry.value }
            .fold(0L, addition)

        return allSums + allManualMoney
    }
}