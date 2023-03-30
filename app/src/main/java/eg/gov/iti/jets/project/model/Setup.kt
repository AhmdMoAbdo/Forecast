package eg.gov.iti.jets.project.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import eg.gov.iti.jets.project.R
import java.util.*

class Setup {

    companion object{

          const val apiKey = "353e9f9e5836cd2d31eafe8c6be06294"
          const val FavToHomeSharedPref = "FavToHome"
          const val HomeLocationSharedPref = "HomeLocation"
          const val SettingsSharedPref = "Settings"
          const val SettingsSharedPrefAlerts = "alerts"
          const val SettingsSharedPrefLanguage = "language"
          const val SettingsSharedPrefTemp = "temp"
          const val SettingsSharedPrefSpeed = "speed"
          const val SettingsSharedPrefMapping = "mapping"

        fun checkForInternet(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }

        //Temperature and wind speed converters
        fun fromCtoF(c: Double): Double = ((c+(9.0/5.0))+32)
        fun fromCtoK(c: Double): Double = (c + 273.15)
        fun fromMStoMH(ms: Double): Double = (ms * 2.237)

        //exchanging API icons with local ones
        fun getImage(img:String):Int{
            when (img) {
                "01d" -> return R.drawable.a01d
                "01n" -> return R.drawable.a01n
                "02d" -> return R.drawable.a02d
                "02n" -> return R.drawable.a02n
                "03d" -> return R.drawable.a03d
                "03n" -> return R.drawable.a03n
                "04d" -> return R.drawable.a04d
                "04n" -> return R.drawable.a04n
                "09d" -> return R.drawable.a09d
                "09n" -> return R.drawable.a09n
                "10d" -> return R.drawable.a10d
                "10n" -> return R.drawable.a10n
                "11d" -> return R.drawable.a11d
                "11n" -> return R.drawable.a11n
                "13d" -> return R.drawable.a13d
                "13n" -> return R.drawable.a13n
                "50d" -> return R.drawable.a50d
                else -> return R.drawable.a50n
            }
        }

        //prepare array list for the hour adapter from API Root
        fun getHour(root: Root): List<Hour> {
            val arr = mutableListOf<Hour>()
            for (i in 0..24) {
                val long = (root.hourly[i].dt + root.timezone_offset - 7200).toLong() * 1000
                val date = Date(long).toString()
                var currHour = date.split(" ")[3].split(":")[0].toInt()
                var currHourString: String
                if (currHour > 12) {
                    currHour -= 12
                    currHourString = currHour.toString() + "PM"
                } else if (currHour == 12) {
                    currHourString = currHour.toString() + "PM"
                } else if (currHour == 0) {
                    currHourString = "12AM"
                } else {
                    currHourString = currHour.toString() + "AM"
                }
                val hour = Hour(
                    i,
                    currHourString,
                    root.hourly[i].weather[0].icon,
                    root.hourly[i].temp
                )
                arr.add(hour)
            }
            return arr
        }

        //prepare array list for the Day adapter from API Root
        fun getDay(root: Root,context: Context): List<Day> {
            val arr = mutableListOf<Day>()
            for (i in 1 until root.daily.size) {
                val long = root.daily[i].dt.toLong() * 1000
                val date = Date(long).toString()
                val name = getFullDayName(date.split(" ")[0],context)
                val day = Day(
                    i,
                    name,
                    root.daily[i].temp.max,
                    root.daily[i].temp.min,
                    root.daily[i].weather[0].description,
                    root.daily[i].weather[0].icon
                )
                arr.add(day)
            }
            return arr
        }

        //replace abbreviations of days to the full day name
        private fun getFullDayName(day: String,context: Context):String {
            return when(day){
                "Sat"->  context.getString(R.string.saturday)
                "Sun"-> context.getString(R.string.sunday)
                "Mon"-> context.getString(R.string.monday)
                "Tue"-> context.getString(R.string.tuesday)
                "Wed"-> context.getString(R.string.wednesday)
                "Thu"-> context.getString(R.string.thursday)
                else -> context.getString(R.string.friday)
            }
        }


         fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
            convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

        private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
            if (sourceDrawable == null) {
                return null
            }
            return if (sourceDrawable is BitmapDrawable) {
                sourceDrawable.bitmap
            } else {
                val constantState = sourceDrawable.constantState ?: return null
                val drawable = constantState.newDrawable().mutate()
                val bitmap: Bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth, drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
        }
    }
}