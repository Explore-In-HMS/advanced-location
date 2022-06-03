# AdvancedLocation

[![Licence MIT](https://img.shields.io/badge/licence-MIT-blue.svg)](https://github.com/talhaoz/advanced-location/blob/dev/LICENCE)
[![Release](https://jitpack.io/v/talhaoz/advanced-location.svg)](https://jitpack.io/#talhaoz/advanced-location)
[![Downloads](https://jitpack.io/v/talhaoz/advanced-location/month.svg)](#download)

A powerful library for easy implementation of HMS Location Kit. 💙

## Features
- Auto Location Permission Request
- Requesting Location Updates by caring battery consumption
- Requesting Location Updates with custom values
- Current Location (One time request)
- Last Known Location (One time request)
- Background Location Updates
- Auto Activity Recognition Permission Request
- Activity Type Recognition

## Setup
##### build.gradle (project)
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### build.gradle (app)
```gradle
dependencies {
    ...
    implementation 'com.github.talhaoz:AdvancedLocation:1.0.0'
}
```


## Use

```kotlin
// Creates an AdvancedLocation object
val advancedLocation = AdvancedLocation() 
```
- #### Request Location Updates
```kotlin
/**
*  @param activity is required to check for location permission
*  @param locationType can be HIGH_ACCURACY, EFFICIENT_POWER, LOW_POWER or PASSIVE
*  @param interval interval(refresh frequency) --> default value = 0L
*  @param resultListener returns an object of position(latitude,longitude)
*/
advancedLocation.requestLocationUpdates(
    this,
    LocationType.EFFICIENT_POWER,
    UpdateInterval.INTERVAL_15_SECONDS
) {
    Log.d(TAG, "Lat: ${it.latitude}\n Long: ${it.longitude}")
}
```
- #### Request Location Updates with custom values
```kotlin
/**
*  @param activity is required to check for location permission
*  @param interval interval(refresh frequency) --> default value = 0L
*  @param smallestDisplacement(max difference between positions) --> default value = 0F
*  @param resultListener returns an object of position(latitude,longitude)
*/
advancedLocation.requestCustomLocationUpdates(
    this,
    interval = 20L,
    smallestDisplacement = 1F
) {
    Log.d(TAG, "Lat: ${it.latitude}\n Long: ${it.longitude}")
}
```
- #### Remove Location Update request
```kotlin
advancedLocation.removeLocationUpdateRequest()
```
- #### Current Location (One time request)
```kotlin
/**
*  @param activity is required for permission
*  @param resultListener returns the position(latitude,longitude)
*/
advancedLocation.getCurrentLocation(this) {
    Log.d(TAG, "Lat: ${it.latitude}\n Long: ${it.longitude}")
}
```

- #### Last Known Location (One time request)
```kotlin
/**
*  @param activity is required in case initialization fails
*  @param taskListener returns the position(latitude,longitude) on success
*  or returns an exception on failure
*/
advancedLocation.getLastLocation(this) {
    val position = it.value
    Log.d(TAG, "Lat: ${position.latitude}\n Long: ${position.longitude}")
}
```

- #### Background Location Updates
```kotlin
/**
*  Requests Location Updates by running a foreground service and stores them in the Room DB (including old locations)
*
*  @param activity is required for permission
*  @param updateInterval location update refresh frequency (Default value = 5 mins)
*  @returns BackgroundLocationResult that accesses to RoomDB (only get)
*/
advancedLocation.startBackgroundLocationUpdates(
    this,
    UpdateInterval.INTERVAL_FIVE_MINUTES
).let {
    val list = it.getAllLocationUpdates()
    list?.forEach { location ->
        Log.d(TAG, "### Lat: ${location.latitude}\n Long: ${location.longitude}")
    }
}
```
- #### Stop Background Location Updates
```kotlin
advancedLocation.stopBackgroundLocationUpdates()
```
- #### Clear Stored Location Updates from DB
```kotlin
advancedLocation.clearLocationDB()
```
- #### Activity Recognition
```kotlin
// Requests the current activity type and stores the result in the Room DB
advancedLocation.getActivityType().let { activityTypeResult ->
    val activityType = activityTypeResult.getActivityType()
    activityType.let {
        Log.d(TAG, "Activity Type: ${it?.type}")
    }
}
```
- #### Clear Stored Activity Type from DB
```kotlin
advancedLocation.clearActivityTypeDB()
```
  

## License

MIT License

Copyright (c) 2022 Talha ÖZ
