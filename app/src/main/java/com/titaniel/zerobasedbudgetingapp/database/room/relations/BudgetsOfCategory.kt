package com.titaniel.zerobasedbudgetingapp.database.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category

/**
 * [BudgetsOfCategory] relation, mapping [budgets] to a [category]
 */
data class BudgetsOfCategory(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "name",
        entityColumn = "categoryName"
    ) val budgets: List<Budget>
)