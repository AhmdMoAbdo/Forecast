package eg.gov.iti.jets.project.network

import eg.gov.iti.jets.project.model.Root
import kotlinx.coroutines.flow.Flow

interface RemoteSource {
    suspend fun getWeatherFromNetwork(lat:String,lon:String,lang:String): Flow<Root>
}