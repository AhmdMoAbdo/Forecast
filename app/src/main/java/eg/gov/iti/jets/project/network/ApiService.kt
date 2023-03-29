package eg.gov.iti.jets.project.network

import eg.gov.iti.jets.project.model.Root
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("onecall")
    suspend fun getSpecificLocation(@Query("lat")lat:String,@Query("lon")lon:String,@Query("exclude")min:String,@Query("appid")id:String,@Query("units")unit:String,@Query("lang")lang:String):Root
}