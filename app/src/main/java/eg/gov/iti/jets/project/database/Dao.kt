package eg.gov.iti.jets.project.database

import androidx.room.*
import eg.gov.iti.jets.project.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RootDao{
    @Query("Select * From Root")
    fun getHomeData(): Flow<Root>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeData(root: Root):Long

    @Query("Select * From SavedLocations")
    fun getSavedLocations():Flow<List<SavedLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(savedLocation: SavedLocation):Long

    @Delete
    suspend fun deleteLocation(savedLocation: SavedLocation)
}
