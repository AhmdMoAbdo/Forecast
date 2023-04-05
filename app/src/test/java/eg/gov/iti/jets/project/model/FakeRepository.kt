package eg.gov.iti.jets.project.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository:RepositoryInterface {

    private val rootList:MutableList<Root> = arrayListOf()
    private val alertList:MutableList<DBAlerts> = arrayListOf()
    private val locationList:MutableList<SavedLocation> = arrayListOf()

    override suspend fun getLocationWeather(lat: String, lon: String, lang: String): Flow<Root> {
        TODO("Not yet implemented")
    }

    override fun getHomeDataFromRepo(): Flow<Root> {
         return flowOf(rootList[0])
    }

    override suspend fun insertHomeDataFromRepo(root: Root): Long {
        rootList.add(root)
        return 1
    }

    override fun getSavedLocationsFromRepo(): Flow<List<SavedLocation>> {
        return flowOf(locationList)
    }

    override suspend fun insertLocationFromRepo(savedLocation: SavedLocation): Long {
        locationList.add(savedLocation)
        return 1
    }

    override suspend fun deleteLocationFromRepo(savedLocation: SavedLocation) {
        locationList.remove(savedLocation)
    }

    override fun getDBAlertsFromRepo(): Flow<List<DBAlerts>> {
        return flowOf(alertList)
    }

    override fun insertNewAlertLocationThroughRepo(dbAlerts: DBAlerts): Long {
        alertList.add(dbAlerts)
        return 1
    }

    override fun deleteAlertThroughRepo(dbAlerts: DBAlerts) {
        alertList.remove(dbAlerts)
    }
}