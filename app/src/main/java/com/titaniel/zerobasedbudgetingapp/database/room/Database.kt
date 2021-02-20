package com.titaniel.zerobasedbudgetingapp.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.titaniel.zerobasedbudgetingapp.database.room.daos.BudgetDao
import com.titaniel.zerobasedbudgetingapp.database.room.daos.CategoryDao
import com.titaniel.zerobasedbudgetingapp.database.room.daos.PayeeDao
import com.titaniel.zerobasedbudgetingapp.database.room.daos.TransactionDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.utils.DatabaseAttributeConverters

/**
 * Room database
 */
@Database(
        entities = [Transaction::class, Category::class, Payee::class, Budget::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(DatabaseAttributeConverters::class)
abstract class Database : RoomDatabase() {

    /**
     * Accessor for [TransactionDao]
     */
    abstract fun transactionDao(): TransactionDao

    /**
     * Accessor for [BudgetDao]
     */
    abstract fun budgetDao(): BudgetDao

    /**
     * Accessor for [CategoryDao]
     */
    abstract fun categoryDao(): CategoryDao

    /**
     * Accessor for [PayeeDao]
     */
    abstract fun payeeDao(): PayeeDao
}
