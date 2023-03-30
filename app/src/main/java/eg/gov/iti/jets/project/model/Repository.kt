package eg.gov.iti.jets.project.model

import eg.gov.iti.jets.project.database.LocalSource
import eg.gov.iti.jets.project.network.RemoteSource
import kotlinx.coroutines.flow.Flow

class Repository(private var remoteSource: RemoteSource, private var localSource: LocalSource):RepositoryInterface {

    companion object{
        private var instance:Repository?= null
        fun getInstance(remoteSource: RemoteSource,localSource: LocalSource):Repository{
            return instance?: synchronized(this){
                val temp = Repository(remoteSource,localSource)
                instance= temp
                temp
            }
        }
    }

    override suspend fun getLocationWeather(lat: String, lon: String,lang:String): Flow<Root> {
        return remoteSource.getWeatherFromNetwork(lat,lon,lang)
    }

    override fun getHomeDataFromRepo(): Flow<Root> {
        return localSource.getHomeData()
    }

    override suspend fun insertHomeDataFromRepo(root: Root): Long {
        return localSource.insertHomeData(root)
    }

    override fun getSavedLocationsFromRepo(): Flow<List<SavedLocation>> {
        return localSource.getSavedLocations()
    }

    override suspend fun insertLocationFromRepo(savedLocation: SavedLocation): Long {
        return localSource.insertLocation(savedLocation)
    }

    override suspend fun deleteLocationFromRepo(savedLocation: SavedLocation) {
        return localSource.deleteLocation(savedLocation)
    }

    override fun getDBAlertsFromRepo(): Flow<List<DBAlerts>> {
        return localSource.getDBAlerts()
    }

    override fun insertNewAlertLocationThroughRepo(dbAlerts: DBAlerts): Long {
        return localSource.insertNewAlertLocation(dbAlerts)
    }

    override fun deleteAlertThroughRepo(dbAlerts: DBAlerts) {
        localSource.deleteAlert(dbAlerts)
    }
}