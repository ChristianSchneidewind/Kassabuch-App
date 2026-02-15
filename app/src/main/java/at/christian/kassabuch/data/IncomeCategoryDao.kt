package at.christian.kassabuch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeCategoryDao {
    @Query("SELECT * FROM income_category ORDER BY name ASC")
    fun observeAll(): Flow<List<IncomeCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: IncomeCategoryEntity)

    @Update
    suspend fun update(category: IncomeCategoryEntity)
}
