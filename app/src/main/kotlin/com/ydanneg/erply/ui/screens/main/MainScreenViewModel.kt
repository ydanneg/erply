package com.ydanneg.erply.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ydanneg.erply.data.repository.UserSessionRepository
import com.ydanneg.erply.sync.WorkManagerSyncManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    syncManager: WorkManagerSyncManager,
    val userSessionRepository: UserSessionRepository
) : ViewModel() {

    init {
        syncManager.requestSync()
    }

    fun logOut() = viewModelScope.launch {
        userSessionRepository.logout()
    }
}
