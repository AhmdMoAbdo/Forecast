package eg.gov.iti.jets.project.database

import android.content.Context
import eg.gov.iti.jets.project.model.*
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(context: Context):LocalSource {

    private val rootDao:RootDao by lazy {
        val rootDB:RootDB = RootDB.getInstance(context)
        rootDB.getHomeData()
    }


    override fun getHomeData(): Flow<Root> {
        return rootDao.getHomeData()
    }

    override suspend fun insertHomeData(root: Root): Long {
        return rootDao.insertHomeData(root)
    }


    override fun getSavedLocations(): Flow<List<SavedLocation>> {
        return rootDao.getSavedLocations()
    }

    override suspend fun insertLocation(savedLocation: SavedLocation): Long {
        return rootDao.insertLocation(savedLocation)
    }

    override suspend fun deleteLocation(savedLocation: SavedLocation) {
        return rootDao.deleteLocation(savedLocation)
    }

    override fun getDBAlerts(): Flow<List<DBAlerts>> {
        return rootDao.getDBAlerts()
    }

    override fun insertNewAlertLocation(dbAlerts: DBAlerts): Long {
        return rootDao.insertNewAlertLocation(dbAlerts)
    }

    override fun deleteAlert(dbAlerts: DBAlerts) {
        rootDao.deleteAlert(dbAlerts)
    }
}