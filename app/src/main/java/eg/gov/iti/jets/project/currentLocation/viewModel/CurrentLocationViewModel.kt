package eg.gov.iti.jets.project.currentLocation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eg.gov.iti.jets.project.model.*
import eg.gov.iti.jets.project.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.*

class CurrentLocationViewModel(private val repo: RepositoryInterface) : ViewModel() {

    private var _location= MutableStateFlow<ApiState>(ApiState.Loading)
    val location= _location.asStateFlow()

    private var _dbHomeData: MutableLiveData<Root> = MutableLiveData<Root>()
    val dbHomeData: LiveData<Root> = _dbHomeData


     fun getLocation(lat: String, lon: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getLocationWeather(lat, lon, lang)
                .catch {
                    e->_location.value = ApiState.Failure(e)
                }
                .collect {
                _location.value = ApiState.Success(it)
            }
        }
    }

    fun getHomeDataFromDataBase() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getHomeDataFromRepo().collect {
                _dbHomeData.postValue(it)
            }
        }
    }

    fun insertHomeDataToDataBase(root: Root) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertHomeDataFromRepo(root)
        }
    }


     fun setTime(timestamp: Int, required: Char): String {
        val long = timestamp.toLong() * 1000
        val date = Date(long).toString()
        val dateArr = date.split(" ")
        val time = dateArr[3].split(":")[0].toInt()
        val minute = dateArr[3].split(":")[1]
        var currHour = time
        var currHourString: String
        if (currHour > 12) {
            currHour -= 12
            currHourString = currHour.toString() + ":" + minute + "PM"
        } else if (currHour == 12) {
            currHourString = currHour.toString() + ":" + minute + "PM"
        } else if (currHour == 0) {
            currHourString = "12:${minute}AM"
        } else {
            currHourString = currHour.toString() + ":" + minute + "AM"
        }
        return when (required) {
            'F' -> "${dateArr[0]}: ${dateArr[2]}-${dateArr[1]}-${dateArr[5]}\n$currHourString"
            'T' -> currHourString
            else -> "${dateArr[0]}: ${dateArr[2]}-${dateArr[1]}-${dateArr[5]}"
        }
    }

     fun sunRiseOrSunSet(root: Root): String {
        var nextStop = ""
        val currentLong = root.current.dt.toLong() * 1000
        val sunsetLong = root.current.sunset.toLong() * 1000
        val sunriseLong = root.current.sunrise.toLong() * 1000
        if (currentLong >= sunsetLong || (currentLong < sunsetLong && currentLong < sunriseLong)) {
            nextStop = setTime(root.current.sunrise + root.timezone_offset - 7200, 'T')
        } else {
            nextStop = setTime(root.current.sunset + root.timezone_offset - 7200, 'T')
        }
        return nextStop
    }

}