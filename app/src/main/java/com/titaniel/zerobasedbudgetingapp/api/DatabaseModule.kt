package com.titaniel.zerobasedbudgetingapp.api

import android.content.Context
import androidx.room.Room
import com.titaniel.zerobasedbudgetingapp.database.datastore.SettingStore
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import com.titaniel.zerobasedbudgetingapp.database.room.daos.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        return Room.databaseBuilder(
            context,
            Database::class.java,
            "Database"
        ).build()

//        val db = Room.databaseBuilder(
//            context,
//            Database::class.java,
//            "Database"
//        ).build()
//
//        GlobalScope.launch {
//            db.clearAllTables()
//            db.categoryDao().add(
//                Category("Freundin", 0),
//                Category("Lebensmittel", 1),
//                Category("PC", 2),
//                Category("Fisch", 3),
//                Category("Sex", 4)
//            )
//        }
//
//        return db
    }

}