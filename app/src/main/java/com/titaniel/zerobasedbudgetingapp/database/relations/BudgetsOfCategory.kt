package com.titaniel.zerobasedbudgetingapp.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.titaniel.zerobasedbudgetingapp.database.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.entities.Transaction

data class BudgetsOfCategory(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "name",
        entityColumn = "categoryName"
    ) val budgets: List<Budget>
)