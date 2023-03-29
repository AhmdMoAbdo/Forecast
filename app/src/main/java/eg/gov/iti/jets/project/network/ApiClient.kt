package eg.gov.iti.jets.project.network

import eg.gov.iti.jets.project.model.Root
import eg.gov.iti.jets.project.model.Setup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ApiClient : RemoteSource{


    private val apiService : ApiService by lazy {
        RetrofitHelper.getInstance().create(ApiService::class.java)
    }

    companion object{
        private var instance:ApiClient?=null
        fun getInstance():ApiClient{
            return instance?: synchronized(this){
                val temp = ApiClient()
                instance =temp
                temp
            }
        }
    }

    override suspend fun getWeatherFromNetwork(lat:String,lon:String,lang:String): Flow<Root> {
        return flowOf( apiService.getSpecificLocation(lat,lon,"minutely",Setup.apiKey,"metric",lang))
    }

}