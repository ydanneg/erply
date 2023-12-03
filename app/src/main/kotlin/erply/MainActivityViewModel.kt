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

package erply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import erply.data.datastore.UserPreferencesDataSource
import erply.data.repository.UserSessionRepository
import erply.model.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userSessionRepository: UserSessionRepository,
    userPreferencesDataSource: UserPreferencesDataSource
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> =
        combine(userSessionRepository.isLoggedIn, userPreferencesDataSource.userPreferences) { isLoggedIn, userPreferences ->
            Pair(isLoggedIn, userPreferences)
        }
            .map { MainActivityUiState.Success(it.second) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                MainActivityUiState.Loading
            )

    val userPreferences = userPreferencesDataSource.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), UserPreferences())

}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userPreferences: UserPreferences) : MainActivityUiState
}
