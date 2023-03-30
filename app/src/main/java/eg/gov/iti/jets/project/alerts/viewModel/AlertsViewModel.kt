package eg.gov.iti.jets.project.alerts.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eg.gov.iti.jets.project.model.DBAlerts
import eg.gov.iti.jets.project.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertsViewModel(var repo:RepositoryInterface):ViewModel() {

    private var _dbAlerts:MutableLiveData<List<DBAlerts>> = MutableLiveData<List<DBAlerts>>()
    val dbAlerts:LiveData<List<DBAlerts>> = _dbAlerts

    init {
        getDBAlerts()
    }

    private fun getDBAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getDBAlertsFromRepo().collect{
                _dbAlerts.postValue(it)
            }
        }
    }

    fun deleteAlert(dbAlerts: DBAlerts){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAlertThroughRepo(dbAlerts)
        }
    }

    fun insertAlert(dbAlerts: DBAlerts){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertNewAlertLocationThroughRepo(dbAlerts)
        }
    }
}