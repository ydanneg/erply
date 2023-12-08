@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ydanneg.erply.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
fun testScope() = TestScope(UnconfinedTestDispatcher())
