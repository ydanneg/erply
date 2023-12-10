package com.ydanneg.erply.data.repository

import com.ydanneg.erply.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
}
