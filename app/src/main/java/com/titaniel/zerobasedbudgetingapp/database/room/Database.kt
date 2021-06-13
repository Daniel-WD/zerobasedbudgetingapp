package com.titaniel.zerobasedbudgetingapp.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.titaniel.zerobasedbudgetingapp.database.room.daos.*
import com.titaniel.zerobasedbudgetingapp.database.room.entities.*
import com.titaniel.zerobasedbudgetingapp.utils.DatabaseAttributeConverters

/**
 * Room database
 */
@Database(
    entities = [Transaction::class, Category::class, Payee::class, Budget::class, Group::class],
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

    /**
     * Accessor for [GroupDao]
     */
    abstract fun groupDao(): GroupDao
}
