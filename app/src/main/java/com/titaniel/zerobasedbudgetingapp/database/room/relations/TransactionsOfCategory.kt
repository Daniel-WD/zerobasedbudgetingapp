package com.titaniel.zerobasedbudgetingapp.database.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction

/**
 * [TransactionsOfCategory] relation, mapping [transactions] to a [category]
 */
data class TransactionsOfCategory(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    ) val transactions: List<Transaction>
)