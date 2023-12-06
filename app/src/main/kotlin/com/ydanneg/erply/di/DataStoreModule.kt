package com.ydanneg.erply.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.ydanneg.erply.datastore.UserPreferencesProto
import com.ydanneg.erply.datastore.UserSessionProto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.ydanneg.erply.datastore.UserPreferencesSerializer
import com.ydanneg.erply.datastore.UserSessionSerializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesUserSessionDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(ErplyDispatchers.IO) dispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserSessionSerializer,
    ): DataStore<UserSessionProto> =
        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope(scope.coroutineContext + dispatcher)
        ) {
            context.dataStoreFile("user_session.pb")
        }


    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(ErplyDispatchers.IO) dispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserPreferencesSerializer,
    ): DataStore<UserPreferencesProto> =
        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope(scope.coroutineContext + dispatcher)
        ) {
            context.dataStoreFile("user_preferences.pb")
        }
}
