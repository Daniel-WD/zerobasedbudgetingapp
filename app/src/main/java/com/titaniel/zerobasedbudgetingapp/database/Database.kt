package com.titaniel.zerobasedbudgetingapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.titaniel.zerobasedbudgetingapp.database.daos.*
import com.titaniel.zerobasedbudgetingapp.database.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.entities.Transaction

@Database(
    entities = [Transaction::class, Category::class, Payee::class, Budget::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun payeeDao(): PayeeDao
}
