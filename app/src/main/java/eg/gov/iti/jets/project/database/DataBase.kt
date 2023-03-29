package eg.gov.iti.jets.project.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eg.gov.iti.jets.project.model.*

@Database(entities = [Root::class, SavedLocation::class], version = 1)
@TypeConverters(DBConverters::class)
abstract class RootDB : RoomDatabase() {
    abstract fun getHomeData(): RootDao

    companion object {
        @Volatile
        private var Instance: RootDB? = null
        fun getInstance(context: Context): RootDB {
            return Instance ?: synchronized(this) {
                val temp =
                    Room.databaseBuilder(context.applicationContext, RootDB::class.java, "Root")
                        .build()
                Instance = temp
                temp
            }
        }
    }
}
