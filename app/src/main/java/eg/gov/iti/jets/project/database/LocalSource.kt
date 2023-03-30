package eg.gov.iti.jets.project.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import eg.gov.iti.jets.project.model.*
import kotlinx.coroutines.flow.Flow

interface LocalSource {

    fun getHomeData(): Flow<Root>
    suspend fun insertHomeData(root: Root):Long

    fun getSavedLocations():Flow<List<SavedLocation>>
    suspend fun insertLocation(savedLocation: SavedLocation):Long
    suspend fun deleteLocation(savedLocation: SavedLocation)

    fun getDBAlerts():Flow<List<DBAlerts>>
    fun insertNewAlertLocation(dbAlerts: DBAlerts):Long
    fun deleteAlert(dbAlerts: DBAlerts)


}