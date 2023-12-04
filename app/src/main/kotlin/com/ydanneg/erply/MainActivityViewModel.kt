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

package com.ydanneg.erply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ydanneg.erply.data.repository.UserDataRepository
import com.ydanneg.erply.model.UserData
import com.ydanneg.erply.util.toStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData
        .map { MainActivityUiState.Success(it) }
        .toStateFlow(viewModelScope, MainActivityUiState.Loading)
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userData: UserData) : MainActivityUiState
}
