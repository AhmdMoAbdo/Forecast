package eg.gov.iti.jets.project.savedLocations.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eg.gov.iti.jets.project.model.RepositoryInterface
import eg.gov.iti.jets.project.model.SavedLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedLocationsViewModel(var repo:RepositoryInterface):ViewModel() {

    private var _savedLocations:MutableLiveData<List<SavedLocation>> = MutableLiveData<List<SavedLocation>>()
    val savedLocation:LiveData<List<SavedLocation>> = _savedLocations

    init {
        getSavedLocations()
    }

    private fun getSavedLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getSavedLocationsFromRepo().collect{
                _savedLocations.postValue(it)
            }
        }
    }

    fun addLocationToSaved(savedLocation: SavedLocation){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertLocationFromRepo(savedLocation)
            getSavedLocations()
        }
    }

    fun deleteLocationFromSaved(savedLocation: SavedLocation){
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteLocationFromRepo(savedLocation)
            getSavedLocations()
        }
    }
}