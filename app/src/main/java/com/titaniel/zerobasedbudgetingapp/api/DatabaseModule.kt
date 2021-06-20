package com.titaniel.zerobasedbudgetingapp.api

import android.content.Context
import androidx.room.Room
import com.titaniel.zerobasedbudgetingapp.database.datastore.SettingStore
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import com.titaniel.zerobasedbudgetingapp.database.room.daos.*
import com.titaniel.zerobasedbudgetingapp.database.room.entities.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Singleton

/**
 * Hilt module to provide the database and daos
 */
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideSettingStore(@ApplicationContext context: Context): SettingStore {
        return SettingStore(context, "settings")
    }

    /**
     * Provide transaction dao of [database]
     */
    @Provides
    fun provideTransactionDao(database: Database): TransactionDao {
        return database.transactionDao()
    }

    /**
     * Provide payee dao of [database]
     */
    @Provides
    fun providePayeeDao(database: Database): PayeeDao {
        return database.payeeDao()
    }

    /**
     * Provide category dao of [database]
     */
    @Provides
    fun provideCategoryDao(database: Database): CategoryDao {
        return database.categoryDao()
    }

    /**
     * Provide budget dao of [database]
     */
    @Provides
    fun provideBudgetDao(database: Database): BudgetDao {
        return database.budgetDao()
    }

    /**
     * Provide budget dao of [database]
     */
    @Provides
    fun provideGroupDao(database: Database): GroupDao {
        return database.groupDao()
    }

    /**
     * Provide database in [context]
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
//        return Room.databaseBuilder(
//            context,
//            Database::class.java,
//            "Database"
//        ).build()

        val db = Room.databaseBuilder(
            context,
            Database::class.java,
            "Database"
        ).build()

        GlobalScope.launch {
            db.clearAllTables()
            db.groupDao().add(
                Group("Haushalt", 1, 2),
                Group("Anderes", 2, 3),
                Group("Persönlich", 3, 1)
            )
            db.categoryDao().add(
                Category("Smartphone", 1, 0, 3),
                Category("Fisch", 1, 1, 2),
                Category("Bücher", 1, 2, 1),

                Category("Lebensmittel", 2, 0, 4),
                Category("Sex", 2, 1, 5),
                Category("Sexbücher", 2, 2, 6),

                Category("PC", 3, 0, 7)
            )
            db.budgetDao().add(
                Budget(1, YearMonth.now(), 100, 1),
                Budget(2, YearMonth.now(), 20000, 2),
//                Budget(3, YearMonth.now(), 300, 3),
//                Budget(4, YearMonth.now(), 400, 4),
//                Budget(5, YearMonth.now(), 5090, 5),
                Budget(6, YearMonth.now(), 6000, 6),
                Budget(7, YearMonth.now(), 7124234, 7)
            )
            db.payeeDao().add(
                Payee("payyee", 1)
            )
            db.transactionDao().add(
                Transaction(100, 1, Category.TO_BE_BUDGETED.id, "", LocalDate.now()),
                Transaction(-100, 1, Category.TO_BE_BUDGETED.id, "", LocalDate.now()),
                Transaction(300, 1, Category.TO_BE_BUDGETED.id, "", LocalDate.now())
            )
        }

        return db
    }

}