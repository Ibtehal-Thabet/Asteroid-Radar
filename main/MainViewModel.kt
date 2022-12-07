package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.ApiFilter
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.AsteroidRepository
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val repository = AsteroidRepository(database)
    private val filter = MutableLiveData(ApiFilter.SHOW_SAVED)

    val pod = repository.pictureOfDay
    val asteroids = Transformations.switchMap(filter) {
        when(it) {
            ApiFilter.SHOW_TODAY -> repository.todayAsteroids
            ApiFilter.SHOW_WEEK -> repository.weekAsteroids
            else -> repository.allAsteroids
        }
    }


    init {
        viewModelScope.launch {
            repository.refreshPictureOfDay()
            repository.refreshAsteroids()
        }
    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    fun updateFilter(filter: ApiFilter) {
        this.filter.value = filter
    }


}
