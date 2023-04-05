package eg.gov.iti.jets.project.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import eg.gov.iti.jets.project.MainRule
import eg.gov.iti.jets.project.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DaoTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    @get:Rule
    val main = MainRule()

    private lateinit var database:RootDB

    @Before
    fun createDataBase(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RootDB::class.java
        ).build()
    }

    @After
    fun closeDataBase() = database.close()

    @Test
    fun getHomeData_addRoot_RetrieveStoredData() = main.runBlockingTest{
        //Given new root data
        val root = Root(1, 39.31,-74.5,"America/New_York",-18000,
            Current(1646318698,1646306882,1646347929,282.21,278.41,1014,65,275.99
                ,2.55,40,10000,8.75,360, emptyList())
            , emptyList(), emptyList(), emptyList())

        //when adding new root
        database.getHomeData().insertHomeData(root)

        var result:Root? = null
        val job = launch {
            database.getHomeData().getHomeData().collect {
                result = it
            }
        }
        job.cancel()
        //then asserting that the retrieved root is the same as the one we inserted
        assertEquals(root,result)
    }

    @Test
    fun getSavedLocations_ListOfLocations_retrieveTheListFromDB()= main.runBlockingTest {
        //given a list of Locations to create a simulation to DB
        val locationList = listOf(
            SavedLocation("Egypt","1","1"),
            SavedLocation("London","2","2"),
            SavedLocation("Greece","3","3"),
            SavedLocation("Canada","4","4")
        )

        //when adding the list to the DB
        for (i in locationList)database.getHomeData().insertLocation(i)

        var result:List<SavedLocation>? = null
        val job = launch {
            database.getHomeData().getSavedLocations().collect{
                result = it
            }
        }
        job.cancel()
        //then asserting that the retrieved list from DB is similar to the one we created
        assertEquals(locationList[0],result!![0])
        assertEquals(locationList[1],result!![1])
        assertEquals(locationList[2],result!![2])
        assertEquals(locationList[3],result!![3])
    }

    @Test
    fun insertLocation_newLocation_RetrieveTheSameLocation() = main.runBlockingTest {
        //Given new location
        val location = SavedLocation("Egypt","1","1")

        //when new location inserted
        database.getHomeData().insertLocation(location)
        var result:List<SavedLocation>? = null
        val job = launch {
            database.getHomeData().getSavedLocations().collect{
                result = it
            }
        }
        job.cancel()
        val index = (result!!.size)-1

        //then asserting that the last element added to the list is our location
        assertEquals(location.name, result!![index].name)
        assertEquals(location.lat, result!![index].lat)
        assertEquals(location.lon, result!![index].lon)
    }

    @Test
    fun deleteLocation_newLocation_deleteTheInsertedLocation() = main.runBlockingTest {
        //given new location added to the DB to be deleted later
        val location = SavedLocation("Egypt","1","1")
        database.getHomeData().insertLocation(location)

        //when deleting the inserted location
        database.getHomeData().deleteLocation(location)

        var result:List<SavedLocation>? = null
        val job = launch {
            database.getHomeData().getSavedLocations().collect{
                result = it
            }
        }
        job.cancel()
        //then search for the location in the DB to assert it's deletion
        val index = result!!.indexOf(location)
        assertEquals(-1,index)
    }

    @Test
    fun getDBAlerts_ListOfAlerts_retrieveTheListFromDB() = main.runBlockingTest {
        //given a list of alerts to add to DB
        val alertList = listOf(
            DBAlerts(1,"Egypt","2/4/2023","7:37"),
            DBAlerts(2,"London","2/4/2023","7:38"),
            DBAlerts(3,"Greece","2/4/2023","7:39"),
            DBAlerts(4,"Canada","2/4/2023","7:40"))

        for (i in alertList)database.getHomeData().insertNewAlertLocation(i)

        var result:List<DBAlerts>? = null
        val job = launch {
            database.getHomeData().getDBAlerts().collect{
                result = it
            }
        }
        job.cancel()

        //then asserting that the retrieved list is similar to the one we created
        assertEquals(alertList[0],result!![0])
        assertEquals(alertList[1],result!![1])
        assertEquals(alertList[2],result!![2])
        assertEquals(alertList[3],result!![3])
    }

    @Test
    fun insertNewAlertLocation_newAlert_retrieveTheSameAlert() = main.runBlockingTest {
        //Given new Alert
        val alert = DBAlerts(1,"Egypt","2/4/2023","7:11")

        //when new alert insert into Database
        database.getHomeData().insertNewAlertLocation(alert)
        var result:List<DBAlerts>? = null
        val job = launch {
            database.getHomeData().getDBAlerts().collect{
                result = it
            }
        }
        job.cancel()

        //then asserting that the last element added to the DB is our alert
        val index = (result!!.size)-1
        assertEquals(alert.id, result!![index].id)
        assertEquals(alert.country, result!![index].country)
        assertEquals(alert.date, result!![index].date)
        assertEquals(alert.time, result!![index].time)
    }

    @Test
    fun deleteAlert_newAlert_deleteTheInsertedAlert() = main.runBlockingTest {
        //given new Alert added to the DB to be deleted later
        val alert = DBAlerts(1,"Egypt","2/4/2023","7:11")
        database.getHomeData().deleteAlert(alert)

        //when deleting the inserted alert
        database.getHomeData().deleteAlert(alert)

        var result:List<DBAlerts>? = null
        val job = launch {
            database.getHomeData().getDBAlerts().collect{
                result = it
            }
        }
        job.cancel()

        //then search for the alert in the DB to assert it's deletion
        val index = result!!.indexOf(alert)
        assertEquals(-1,index)
    }
}