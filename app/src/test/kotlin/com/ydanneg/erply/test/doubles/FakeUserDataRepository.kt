package com.ydanneg.erply.test.doubles

import com.ydanneg.erply.data.repository.UserDataRepository
import com.ydanneg.erply.model.UserData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

class FakeUserDataRepository(userData: UserData) : UserDataRepository {

    private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val userData: Flow<UserData> = _userData.filterNotNull()

    init {
        _userData.tryEmit(userData)
    }
}
