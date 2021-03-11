package com.titaniel.zerobasedbudgetingapp.database.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category

/**
 * [BudgetWithCategory] relation. [Budget] with [Category].
 */
data class BudgetWithCategory(
    @Embedded val budget: Budget,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    ) val category: Category
)