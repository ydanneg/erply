/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ydanneg.erply.datastore

import androidx.datastore.core.DataStoreFactory
import com.ydanneg.erply.di.DataStoreModule
import com.ydanneg.erply.security.EncryptedData
import com.ydanneg.erply.security.EncryptionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Singleton
import kotlin.random.Random

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class],
)
object TestDataStoreModule {

    @Provides
    @Singleton
    fun encryptionManager(): EncryptionManager {
        return object : EncryptionManager {
            private lateinit var encryptedData: EncryptedData

            override suspend fun encryptText(keyAlias: String, data: String): EncryptedData {
                encryptedData = EncryptedData(Random.nextBytes(128), data.toByteArray())
                return encryptedData
            }

            override suspend fun decryptText(keyAlias: String, encrypted: ByteArray, iv: ByteArray): String {
                return String(encryptedData.data)
            }

        }
    }
//
//    @Provides
//    @Singleton
//    fun providesUserSessionDataStore(
//        @ApplicationScope scope: CoroutineScope,
//        userSessionSerializer: UserSessionSerializer,
//        tmpFolder: TemporaryFolder,
//    ): DataStore<UserSessionProto> =
//        tmpFolder.testUserSessionDataStore(
//            coroutineScope = scope,
//            serializer = userSessionSerializer,
//        )
}

fun File.testUserSessionDataStore(
    coroutineScope: CoroutineScope,
    serializer: UserSessionSerializer = UserSessionSerializer(),
) = DataStoreFactory.create(
    serializer = serializer,
    scope = coroutineScope,
) {
    resolve("user_session_test.pb").apply {
        createNewFile()
    }
}

fun File.testUserPreferencesDataStore(
    coroutineScope: CoroutineScope,
    serializer: UserPreferencesSerializer = UserPreferencesSerializer(),
) = DataStoreFactory.create(
    serializer = serializer,
    scope = coroutineScope,
) {
    resolve("user_prefs_test.pb").apply {
        createNewFile()
    }
}
