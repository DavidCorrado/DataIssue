package com.davidcorrado.data

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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
            assertTrue(dao.getSession().first() == loginSession)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun stateFlowWithStateFlow() = runTest{
        repeat(50) {
            val loginSession2 = getLoginSession2()
            val mutableFlow = MutableStateFlow<LoginSession?>(loginSession2)
            assertTrue(mutableFlow.first() == loginSession2)
            val scope = TestScope(UnconfinedTestDispatcher())
            val sessionFlow = mutableFlow.stateIn(
                scope,
                SharingStarted.Eagerly,
                null//Not used
            )
            assertTrue(sessionFlow.first() == loginSession2)
            val loginSession = getLoginSession()
            mutableFlow.emit(loginSession)
            assertTrue(sessionFlow.first() == loginSession)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun flowWithStateFlow() = runTest{
        repeat(50) {
            val loginSession2 = getLoginSession2()

            val flow = flow<LoginSession?> {
                emit(loginSession2)
            }
            assertTrue(flow.first() == loginSession2)
            val scope = TestScope(UnconfinedTestDispatcher())
            val sessionFlow = flow.stateIn(
                scope,
                SharingStarted.Eagerly,
                null
            )
            assertTrue(sessionFlow.first() == loginSession2)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun queryRoomEmptyStateFlow() = runTest{
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
            assertTrue(sessionFlow.first() == null)

            val loginSession = getLoginSession()
            dao.save(loginSession)
            assertTrue(sessionFlow.first() == loginSession)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun queryRoomRestoreStateFlow() = runTest{
        repeat(50) {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val db = Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java
            ).build()
            val dao = db.loginSessionDao()
            val scope = TestScope(UnconfinedTestDispatcher())
            val loginSession = getLoginSession()
            dao.save(loginSession)
            val sessionFlow = dao.getSession().stateIn(
                scope,
                SharingStarted.Eagerly,
                null
            )
            assertTrue(sessionFlow.first() == loginSession)
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