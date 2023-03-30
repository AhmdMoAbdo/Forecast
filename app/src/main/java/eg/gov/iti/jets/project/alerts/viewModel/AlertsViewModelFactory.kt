package eg.gov.iti.jets.project.alerts.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.project.model.RepositoryInterface

class AlertsViewModelFactory(var repo:RepositoryInterface):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertsViewModel::class.java))
            AlertsViewModel(repo) as T
        else {
            throw IllegalArgumentException("View Model Not Found")
        }
    }
}