package com.udacity.asteroidradar.network

import com.squareup.moshi.Json
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.Asteroid
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.database.DatabasePictureOfDay


@JsonClass(generateAdapter = true)
data class NetworkPictureOfDay(
    val date : String,
    @Json(name = "media_type") val mediaType: String,
    val title: String,
    val url: String)

/**
 * Convert Network results to database objects
 */
fun NetworkPictureOfDay.asDatabaseModel(): DatabasePictureOfDay {
    return DatabasePictureOfDay(
            date,
            mediaType,
            title,
            url)
}

fun List<Asteroid>.asDatabaseModel(): Array<DatabaseAsteroid> {
    return map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous)
    }.toTypedArray()
}
