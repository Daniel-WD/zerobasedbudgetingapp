package com.titaniel.zerobasedbudgetingapp.transaction

import com.titaniel.zerobasedbudgetingapp.budget.Category
import java.util.*

data class Transaction(
    val value: Long,
    val payee: String,
    val description: String,
    val date: Date,
    val category: Category
)