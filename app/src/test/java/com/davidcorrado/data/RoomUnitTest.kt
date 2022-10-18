package com.davidcorrado.data

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.*
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"], application = Application::class)
class RoomUnitTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun queryRoom() = runTest {
        repeat(50) {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val db = Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java
            ).build()
            val dao = db.loginSessionDao()
            val loginSession = getLoginSession()
            dao.save(loginSession)
            val session = dao.getSession().first()
            assertTrue(session == loginSession)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun queryStateFlow() = runTest{
        repeat(50) {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val db = Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java
            ).build()
            val dao = db.loginSessionDao()
            val scope = TestScope(UnconfinedTestDispatcher())
            val sessionFlow = dao.getSession().stateIn(
                scope,
                SharingStarted.Eagerly,
                null
            )
            val loginSession = getLoginSession()
            dao.save(loginSession)
            val session = sessionFlow.first()
            assertTrue(session == loginSession)
        }
    }
    private fun getLoginSession(): LoginSession {
        return LoginSession(
            userId = "669",
        )
    }
    private fun getLoginSession2(): LoginSession {
        return LoginSession(
            userId = "668",
        )
    }
}