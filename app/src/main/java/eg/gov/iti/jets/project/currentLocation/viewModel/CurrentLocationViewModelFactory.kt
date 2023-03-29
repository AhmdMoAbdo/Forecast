package eg.gov.iti.jets.project.currentLocation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.project.model.RepositoryInterface

class CurrentLocationViewModelFactory(private val repo: RepositoryInterface):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(CurrentLocationViewModel::class.java)){
            CurrentLocationViewModel(repo) as T
        }else{
            throw IllegalArgumentException("View Model Not Found")
        }
    }
}