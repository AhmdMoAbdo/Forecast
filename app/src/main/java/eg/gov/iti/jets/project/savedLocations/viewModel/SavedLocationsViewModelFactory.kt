package eg.gov.iti.jets.project.savedLocations.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.project.model.RepositoryInterface

class SavedLocationsViewModelFactory(var repo:RepositoryInterface):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SavedLocationsViewModel::class.java)){
            SavedLocationsViewModel(repo) as T
        }else{
            throw IllegalArgumentException("View Model Not Found")
        }
    }
}