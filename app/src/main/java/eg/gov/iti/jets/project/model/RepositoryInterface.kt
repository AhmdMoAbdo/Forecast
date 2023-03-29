package eg.gov.iti.jets.project.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {

    suspend fun getLocationWeather(lat:String,lon:String,lang:String): Flow<Root>

    fun getHomeDataFromRepo(): Flow<Root>
    suspend fun insertHomeDataFromRepo(root: Root):Long

    fun getSavedLocationsFromRepo():Flow<List<SavedLocation>>
    suspend fun insertLocationFromRepo(savedLocation: SavedLocation):Long
    suspend fun deleteLocationFromRepo(savedLocation: SavedLocation)
}