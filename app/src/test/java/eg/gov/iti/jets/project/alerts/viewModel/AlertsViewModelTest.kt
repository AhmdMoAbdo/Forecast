package eg.gov.iti.jets.project.alerts.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import eg.gov.iti.jets.project.MainRule
import eg.gov.iti.jets.project.getOrAwaitValue
import eg.gov.iti.jets.project.model.DBAlerts
import eg.gov.iti.jets.project.model.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AlertsViewModelTest{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    lateinit var viewModel: AlertsViewModel
    lateinit var repo: FakeRepository

    @Before
    fun setup(){
        repo = FakeRepository()
        viewModel = AlertsViewModel(repo)
    }

    @Test
    fun insertAlert_newAlert_retrieveTheSameAlert(){
        //Given new Alert
        val alert = DBAlerts(1,"Egypt","2/4/2023","7:11")

        //when new alert insert
        viewModel.insertAlert(alert)

        //then asserting that the last element added to the list is our alert
        val result = viewModel.dbAlerts.getOrAwaitValue{}
        val index = (result.size)-1
        assertEquals(alert.id,result[index].id)
        assertEquals(alert.country,result[index].country)
        assertEquals(alert.date,result[index].date)
        assertEquals(alert.time,result[index].time)
    }

    @Test
    fun deleteAlert_newAlert_deleteTheInsertedAlert(){
        //given new Alert added to the list to be deleted later
        val alert = DBAlerts(1,"Egypt","2/4/2023","7:23")
        viewModel.insertAlert(alert)

        //when deleting the inserted alert
        viewModel.deleteAlert(alert)

        //then search for the alert in the list to assert it's deletion
        val result = viewModel.dbAlerts.getOrAwaitValue{}
        assertEquals(-1, result.indexOf(alert))
    }
    @Test
    fun getDbAlerts_ListOfAlerts_retrieveTheListFromDB(){
        //given a list of alerts to create a simulation to DB
        val alertList = listOf(
            DBAlerts(1,"Egypt","2/4/2023","7:37"),
            DBAlerts(2,"London","2/4/2023","7:38"),
            DBAlerts(3,"Greece","2/4/2023","7:39"),
            DBAlerts(4,"Canada","2/4/2023","7:40"))

        for (i in alertList)viewModel.insertAlert(i)

        //then asserting that the retrieved list is similar to the one we created
        val result = viewModel.dbAlerts.getOrAwaitValue {}
        assertEquals(alertList[0],result[0])
        assertEquals(alertList[1],result[1])
        assertEquals(alertList[2],result[2])
        assertEquals(alertList[3],result[3])
    }
}