package com.titaniel.zerobasedbudgetingapp.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.titaniel.zerobasedbudgetingapp.database.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.entities.Transaction

data class TransactionsOfCategory(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "name",
        entityColumn = "categoryName"
    ) val transactions: List<Transaction>
)