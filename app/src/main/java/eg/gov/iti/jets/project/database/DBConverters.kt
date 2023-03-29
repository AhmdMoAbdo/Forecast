package eg.gov.iti.jets.project.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import eg.gov.iti.jets.project.model.*

class DBConverters{

    @TypeConverter
     fun fromCurrentToGSON(current: Current):String{
        return Gson().toJson(current)
    }

    @TypeConverter
     fun fromGSONToCurrent(stringCurrent:String):Current{
        return Gson().fromJson(stringCurrent,Current::class.java)
    }

    @TypeConverter
     fun fromHourlyArrToGSON(hourlyArr: List<Hourly>):String{
        return Gson().toJson(hourlyArr)
    }

    @TypeConverter
     fun fromGSONToHourList(hourlyString:String):List<Hourly>{
        return Gson().fromJson(hourlyString, Array<Hourly>::class.java).toList()
    }

    @TypeConverter
     fun fromDailyArrToGSON(dailyArr: List<Daily>):String{
        return Gson().toJson(dailyArr)
    }

    @TypeConverter
     fun fromGSONToDailyList(dailyString:String):List<Daily>{
        return Gson().fromJson(dailyString, Array<Daily>::class.java).toList()
    }

    @TypeConverter
     fun fromAlertsArrToGSON(alertsArr: List<Alert>):String{
        return Gson().toJson(alertsArr)
    }

    @TypeConverter
     fun fromGSONToAlertsList(alertsString:String):List<Alert>{
        return Gson().fromJson(alertsString, Array<Alert>::class.java).toList()
    }

    @TypeConverter
     fun fromWeatherArrToGSON(weatherArr: ArrayList<Weather>):String{
        return Gson().toJson(weatherArr)
    }

    @TypeConverter
     fun fromGSONToWeatherList(weatherString:String):ArrayList<Weather>{
        return Gson().fromJson(weatherString, ArrayList<Weather>()::class.java)
    }

}

