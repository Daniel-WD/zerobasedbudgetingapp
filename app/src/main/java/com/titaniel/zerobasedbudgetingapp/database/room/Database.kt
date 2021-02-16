package com.titaniel.zerobasedbudgetingapp.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.titaniel.zerobasedbudgetingapp.database.room.daos.*
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.utils.DatabaseAttributeConverters

@Database(
    entities = [Transaction::class, Category::class, Payee::class, Budget::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseAttributeConverters::class)
abstract class Database : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun payeeDao(): PayeeDao
}
