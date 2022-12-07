package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {

    @Query("SELECT * FROM DatabaseAsteroid ORDER BY closeApproachDate desc")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM DatabaseAsteroid WHERE closeApproachDate = :closeApproachDate")
    fun getTodayAsteroids(closeApproachDate: String): LiveData<List<DatabaseAsteroid>>

    @Query("select * from DatabaseAsteroid WHERE closeApproachDate BETWEEN :start AND :end "
            + "ORDER BY closeApproachDate desc")
    fun getWeekAsteroids(start: String, end: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("DELETE FROM DatabaseAsteroid WHERE closeApproachDate < :closeApproachDate")
    fun deleteAsteroidsBefore(closeApproachDate: String)
}

@Dao
interface PodDao {
    @Query("SELECT * FROM DatabasePictureOfDay pod ORDER BY pod.date DESC LIMIT 0,1")
    fun getPod(): LiveData<DatabasePictureOfDay>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(pod: DatabasePictureOfDay)

    @Query("DELETE FROM DatabasePictureOfDay WHERE date < :date")
    fun deletePicturesBefore(date: String)
}

@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfDay::class], version = 1)
abstract class APDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
    abstract val podDao: PodDao
}

private lateinit var INSTANCE: APDatabase

fun getDatabase(context: Context): APDatabase {
    synchronized(APDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                APDatabase::class.java,
                "asteroidsDatabase").build()
        }
    }
    return INSTANCE
}
