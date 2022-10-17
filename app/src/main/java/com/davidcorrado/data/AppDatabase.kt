package com.davidcorrado.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class LoginSession(
    @PrimaryKey
    val userId: String
)

@Database(
    entities = [
        LoginSession::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loginSessionDao(): LoginSessionDao
}

@Dao
interface LoginSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(loginSession: LoginSession)

    @Query(
        value = """
        SELECT * FROM LoginSession
        LIMIT 1
    """
    )
    fun getSession(): Flow<LoginSession?>

    @Query(
        value = """
            DELETE FROM LoginSession
        """
    )
    suspend fun delete()
}
