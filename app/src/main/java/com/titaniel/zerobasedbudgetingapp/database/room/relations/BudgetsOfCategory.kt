package com.titaniel.zerobasedbudgetingapp.database.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category

data class BudgetsOfCategory(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "name",
        entityColumn = "categoryName"
    ) val budgets: List<Budget>
)