package com.udacity.asteroidradar.domain

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.Util
import com.udacity.asteroidradar.api.Api
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.APDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.util.*

class AsteroidRepository (private val database: APDatabase){

    val today = Util.convertDateStringToFormattedString(Calendar.getInstance().time, Constants.API_QUERY_DATE_FORMAT)
    val week = Util.convertDateStringToFormattedString(Util.addDaysToDate(Calendar.getInstance().time, Constants.DEFAULT_END_DATE_DAYS),
        Constants.API_QUERY_DATE_FORMAT)


    //    Asteroid repository
    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it.asDomainModel()
        }

    val todayAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getTodayAsteroids(today)) {
            it.asDomainModel()
        }

    val weekAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getWeekAsteroids(today, week)) {
            it.asDomainModel()
        }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidList = Api.ret.getAsteroids(Constants.API_KEY).await()
                database.asteroidDao.insertAll(*parseAsteroidsJsonResult(JSONObject(asteroidList)).asDatabaseModel())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }


//    Photo of the day repository
    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(database.podDao.getPod()) {
            it?.asDomainModel()
        }

    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            try{
                val pictureOfDay = Api.ret.getPictureOfDay(Constants.API_KEY).await()
                database.podDao.insert(pictureOfDay.asDatabaseModel())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}