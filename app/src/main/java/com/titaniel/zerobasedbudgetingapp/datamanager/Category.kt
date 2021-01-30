package com.titaniel.zerobasedbudgetingapp.datamanager

/**
 * Represents a budgeting category.
 * @param budgetedMoney Map mapping a month identifying key to the budgeted value.
 * @param name Category name.
 */
data class Category(val budgetedMoney: Map<Int, Long>, val name: String)