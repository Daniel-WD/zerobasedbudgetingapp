package com.titaniel.zerobasedbudgetingapp.api

import android.content.Context
import androidx.room.Room
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import com.titaniel.zerobasedbudgetingapp.database.room.daos.BudgetDao
import com.titaniel.zerobasedbudgetingapp.database.room.daos.CategoryDao
import com.titaniel.zerobasedbudgetingapp.database.room.daos.PayeeDao
import com.titaniel.zerobasedbudgetingapp.database.room.daos.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideTransactionDao(database: Database): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun providePayeeDao(database: Database): PayeeDao {
        return database.payeeDao()
    }

    @Provides
    fun provideCategoryDao(database: Database): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideBudgetDao(database: Database): BudgetDao {
        return database.budgetDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        val db = Room.databaseBuilder(
            context,
            Database::class.java,
            "Database"
        ).build()
//        GlobalScope.launch {
//            db.clearAllTables()
//            db.categoryDao().add(Category("Lebensmittel"))
//            db.categoryDao().add(Category("Bücher"))
//            db.categoryDao().add(Category("Protein"))
//            db.categoryDao().add(Category("Sex"))
//            db.categoryDao().add(Category("Freizeit"))
//            db.categoryDao().add(Category("Freundin"))
//            db.categoryDao().add(Category("Mensa"))
//        }
        return db
    }

}