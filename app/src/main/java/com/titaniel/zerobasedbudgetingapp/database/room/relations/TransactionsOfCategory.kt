package com.titaniel.zerobasedbudgetingapp.database.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction

data class TransactionsOfCategory(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "name",
        entityColumn = "categoryName"
    ) val transactions: List<Transaction>
)