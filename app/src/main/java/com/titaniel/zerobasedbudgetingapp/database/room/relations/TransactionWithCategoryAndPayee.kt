package com.titaniel.zerobasedbudgetingapp.database.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction

/**
 * [TransactionWithCategoryAndPayee] relation. A [Transaction] with its [category] and [payee]
 */
data class TransactionWithCategoryAndPayee(
    @Embedded val transaction: Transaction,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    ) val category: Category?,
    @Relation(
        parentColumn = "payeeId",
        entityColumn = "id"
    ) val payee: Payee
)