package com.example.di

import android.content.Context
import com.example.database.AppDatabase
import com.example.database.CardRepository
import com.example.core.usecase.ProcessDepositUseCase
import com.example.sync.SyncOutboxDao
import com.example.sync.SyncOutboxRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideSyncOutboxDao(db: AppDatabase): SyncOutboxDao {
        return db.syncOutboxDao()
    }

    @Provides
    @Singleton
    fun provideCardRepository(@ApplicationContext context: Context): CardRepository {
        return CardRepository.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideProcessDepositUseCase(
        @ApplicationContext context: Context,
        repository: CardRepository
    ): ProcessDepositUseCase {
        return ProcessDepositUseCase(context, repository)
    }
}


