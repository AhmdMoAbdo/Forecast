package eg.gov.iti.jets.project.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val baseUrl:String = "https://api.openweathermap.org/data/3.0/"
    fun getInstance():Retrofit{
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(
            baseUrl).build()
    }
}