package eg.gov.iti.jets.project.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import eg.gov.iti.jets.project.MainRule
import eg.gov.iti.jets.project.database.FakeLocalSource
import eg.gov.iti.jets.project.network.ApiClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class RepositoryTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val main = MainRule()

    private lateinit var localSource: FakeLocalSource
    lateinit var repo: Repository

    @Before
    fun setup() {
        localSource = FakeLocalSource()
        repo = Repository(ApiClient.getInstance(), localSource)
    }

    @Test
    fun getHomeDataFromRepo_addRoot_RetrieveStoredData() = main.runBlockingTest {
        //Given new root data
        val root = Root(
            1, 39.31, -74.5, "America/New_York", -18000,
            Current(
                1646318698,
                1646306882,
                1646347929,
                282.21,
                278.41,
                1014,
                65,
                275.99,
                2.55,
                40,
                10000,
                8.75,
                360,
                emptyList()
            ), emptyList(), emptyList(), emptyList()
        )

        //when adding new root
        repo.insertHomeDataFromRepo(root)

        var result: Root? = null
        repo.getHomeDataFromRepo().collect {
            result = it

        }

        //then asserting that the retrieved root is the same as the one we inserted
        assertEquals(root, result)
    }

    @Test
    fun getSavedLocationsFromRepo_ListOfLocations_retrieveTheListFromDB() = main.runBlockingTest {
        //given a list of Locations to create a simulation to DB
        val locationList = listOf(
            SavedLocation("Egypt", "1", "1"),
            SavedLocation("London", "2", "2"),
            SavedLocation("Greece", "3", "3"),
            SavedLocation("Canada", "4", "4")
        )

        //when adding the list to the DB
        for (i in locationList) repo.insertLocationFromRepo(i)

        var result: List<SavedLocation>? = null

        repo.getSavedLocationsFromRepo().collect {
            result = it

        }

        //then asserting that the retrieved list from DB is similar to the one we created
        assertEquals(locationList[0], result!![0])
        assertEquals(locationList[1], result!![1])
        assertEquals(locationList[2], result!![2])
        assertEquals(locationList[3], result!![3])
    }

    @Test
    fun insertLocationFromRepo_newLocation_RetrieveTheSameLocation() = main.runBlockingTest {
        //Given new location
        val location = SavedLocation("Egypt", "1", "1")

        //when new location inserted
        repo.insertLocationFromRepo(location)
        var result: List<SavedLocation>? = null

        repo.getSavedLocationsFromRepo().collect {
            result = it
        }

        val index = (result!!.size) - 1

        //then asserting that the last element added to the list is our location
        assertEquals(location.name, result!![index].name)
        assertEquals(location.lat, result!![index].lat)
        assertEquals(location.lon, result!![index].lon)
    }

    @Test
    fun deleteLocationFromRepo_newLocation_deleteTheInsertedLocation() = main.runBlockingTest {
        //given new location added to the DB to be deleted later
        val location = SavedLocation("Egypt", "1", "1")
        repo.insertLocationFromRepo(location)

        //when deleting the inserted location
        repo.deleteLocationFromRepo(location)

        var result: List<SavedLocation>? = null
        repo.getSavedLocationsFromRepo().collect {
            result = it
        }

        //then search for the location in the DB to assert it's deletion
        val index = result!!.indexOf(location)
        assertEquals(-1, index)
    }

    @Test
    fun getDBAlertsFromRepo_ListOfAlerts_retrieveTheListFromDB() = main.runBlockingTest {
        //given a list of alerts to add to DB
        val alertList = listOf(
            DBAlerts(1, "Egypt", "2/4/2023", "7:37"),
            DBAlerts(2, "London", "2/4/2023", "7:38"),
            DBAlerts(3, "Greece", "2/4/2023", "7:39"),
            DBAlerts(4, "Canada", "2/4/2023", "7:40")
        )

        for (i in alertList) repo.insertNewAlertLocationThroughRepo(i)

        var result: List<DBAlerts>? = null

        repo.getDBAlertsFromRepo().collect {
            result = it
        }

        //then asserting that the retrieved list is similar to the one we created
        assertEquals(alertList[0], result!![0])
        assertEquals(alertList[1], result!![1])
        assertEquals(alertList[2], result!![2])
        assertEquals(alertList[3], result!![3])
    }

    @Test
    fun insertNewAlertLocationThroughRepo_newAlert_retrieveTheSameAlert() = main.runBlockingTest {
        //Given new Alert
        val alert = DBAlerts(1, "Egypt", "2/4/2023", "7:11")

        //when new alert insert into Database
        repo.insertNewAlertLocationThroughRepo(alert)
        var result: List<DBAlerts>? = null
        repo.getDBAlertsFromRepo().collect {
            result = it
        }

        //then asserting that the last element added to the DB is our alert
        val index = (result!!.size) - 1
        assertEquals(alert.id, result!![index].id)
        assertEquals(alert.country, result!![index].country)
        assertEquals(alert.date, result!![index].date)
        assertEquals(alert.time, result!![index].time)
    }

    @Test
    fun deleteAlertThroughRepo_newAlert_deleteTheInsertedAlert() = main.runBlockingTest {
        //given new Alert added to the DB to be deleted later
        val alert = DBAlerts(1, "Egypt", "2/4/2023", "7:11")
        repo.insertNewAlertLocationThroughRepo(alert)

        //when deleting the inserted alert
        repo.deleteAlertThroughRepo(alert)

        var result: List<DBAlerts>? = null
        repo.getDBAlertsFromRepo().collect {
            result = it
        }

        //then search for the alert in the DB to assert it's deletion
        val index = result!!.indexOf(alert)
        assertEquals(-1, index)
    }

}