package eg.gov.iti.jets.project.database

import eg.gov.iti.jets.project.model.DBAlerts
import eg.gov.iti.jets.project.model.Root
import eg.gov.iti.jets.project.model.SavedLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSource:LocalSource {

     private val rootList:MutableList<Root> = arrayListOf()
     private val alertList:MutableList<DBAlerts> = arrayListOf()
     private val locationList:MutableList<SavedLocation> = arrayListOf()

    override fun getHomeData(): Flow<Root> {
        return flowOf(rootList[0])
    }

    override suspend fun insertHomeData(root: Root): Long {
        rootList.add(root)
        return 1
    }

    override fun getSavedLocations(): Flow<List<SavedLocation>> {
        return flowOf(locationList)
    }

    override suspend fun insertLocation(savedLocation: SavedLocation): Long {
        locationList.add(savedLocation)
        return 1
    }

    override suspend fun deleteLocation(savedLocation: SavedLocation) {
        locationList.remove(savedLocation)
    }

    override fun getDBAlerts(): Flow<List<DBAlerts>> {
        return flowOf(alertList)
    }

    override fun insertNewAlertLocation(dbAlerts: DBAlerts): Long {
        alertList.add(dbAlerts)
        return 1
    }

    override fun deleteAlert(dbAlerts: DBAlerts) {
        alertList.remove(dbAlerts)
    }
}