
package com.hms.advancedlocationlibrary.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.hms.advancedlocationlibrary.AdvancedLocation.Companion.coroutineScopeIO
import com.hms.advancedlocationlibrary.data.listeners.ResultListener
import com.hms.advancedlocationlibrary.utils.Constants.DELAY_TIME
import com.hms.advancedlocationlibrary.utils.Constants.LOG_PREFIX
import com.hms.advancedlocationlibrary.utils.AdvancedLocationException
import com.hms.advancedlocationlibrary.utils.AdvancedLocationException.Companion.MISSING_PERMISSION
import com.hms.advancedlocationlibrary.utils.device.DeviceUtils.isAboveM
import com.hms.advancedlocationlibrary.utils.device.DeviceUtils.isAboveQ
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class PermissionManager(private val context: Context) {

    companion object {
        private const val TAG = "${LOG_PREFIX}PermissionManager"

        private const val PERMISSION_SINGLE = "PermissionSingle"
        private const val PERMISSION_MULTIPLE = "PermissionMultiple"

        private val LOCATION_PERMISSION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        @RequiresApi(Build.VERSION_CODES.Q)
        private val LOCATION_PERMISSION_BACKGROUND = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        private val ACTIVITY_PERMISSION = arrayOf("com.hms.permission.ACTIVITY_RECOGNITION")

        @RequiresApi(Build.VERSION_CODES.Q)
        private val ACTIVITY_PERMISSION_Q = arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
    }

    private fun checkLocationPermission() = hasPermission(LOCATION_PERMISSION)

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkBackgroundLocationPermission() = hasPermission(LOCATION_PERMISSION_BACKGROUND)

    private fun checkActivityPermission() = hasPermission(ACTIVITY_PERMISSION)

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkActivityPermissionQ() = hasPermission(ACTIVITY_PERMISSION_Q)

    private fun hasPermission(permissions: Array<String>) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    @Throws(AdvancedLocationException::class)
    fun doIfLocationPermitted(background: Boolean = true, function: () -> Unit) {
        if (background && isAboveQ()) {
            if (hasPermission(LOCATION_PERMISSION_BACKGROUND)) {
                function()
            } else {
                throw AdvancedLocationException(MISSING_PERMISSION, LOCATION_PERMISSION_BACKGROUND)
            }        } else {
            if (hasPermission(LOCATION_PERMISSION)) {
                function()
            } else {
                throw AdvancedLocationException(MISSING_PERMISSION, LOCATION_PERMISSION)
            }
        }
    }

    fun doIfActivityPermitted(function: () -> Unit) {

        if (isAboveQ() && checkActivityPermissionQ()) {
            function()
        } else if (isAboveQ().not() && checkActivityPermission()) {
            function()
        } else {
            val missingPermission = if (isAboveQ()) ACTIVITY_PERMISSION_Q else ACTIVITY_PERMISSION
            throw AdvancedLocationException(MISSING_PERMISSION, missingPermission)
        }
    }

    fun doWithActivityPermission(activity: FragmentActivity, function:() -> Unit) {
        Log.d(TAG, "doWithActivityPermission()")

        if (isAboveQ() && hasPermission(ACTIVITY_PERMISSION_Q)) {
            function()
        } else if (isAboveQ().not() && hasPermission(ACTIVITY_PERMISSION)) {
            function()
        } else {
            val requiredPermission = if (isAboveQ()) ACTIVITY_PERMISSION_Q else ACTIVITY_PERMISSION
            requestPermission(activity, requiredPermission) { permissionMap ->
                if (hasPermission(permissionMap.keys.toTypedArray())) {
                    function()
                } else {
                    Log.w(
                        TAG,
                        "doWithActivityPermission --> Activity identification permission is requested, but rejected."
                    )
                }
            }

        }
    }

    fun doWithLocationPermission(activity: FragmentActivity, function:() -> Unit, background: Boolean = false) = coroutineScopeIO.launch {

        //checkIfResuming(activity)

        if (isAboveQ() && checkBackgroundLocationPermission()) {
            function()
        } else if (isAboveQ().not() && isAboveM() && checkLocationPermission()) {
            function()
        } else {
            requestLocationPermission(activity) { result ->
                if (isAboveQ()) {
                    var backgroundPermitted = false
                    var locationPermitted = false

                    for (permission in result) {
                        if (permission.key == Manifest.permission.ACCESS_FINE_LOCATION && permission.value) {
                            locationPermitted = true
                        } else if (permission.key == Manifest.permission.ACCESS_BACKGROUND_LOCATION && permission.value) {
                            backgroundPermitted = true
                        }
                    }

                    if (background && backgroundPermitted && locationPermitted) {
                        Log.d(
                            TAG,
                            "requestLocationPermission - onResult --> Permissions granted, including background location. Starting function."
                        )
                        function()
                    } else if (!background && locationPermitted) {
                        Log.d(
                            TAG,
                            "requestLocationPermission - onResult --> Permissions granted. Starting function."
                        )
                        function()
                    } else {
                        Log.w(
                            TAG,
                            "requestLocationPermission - onResult --> Required permissions are not granted. Can't start function."
                        )
                    }
                } else if (isAboveM()) {
                    for (permission in result) {
                        if (permission.key == Manifest.permission.ACCESS_FINE_LOCATION && permission.value) {
                            Log.d(
                                TAG,
                                "requestLocationPermission - onResult --> Permissions granted. Starting function."
                            )
                            function()
                            break
                        }
                    }
                }
            }
        }
    }

    private fun requestLocationPermission(activity: FragmentActivity, listener: ResultListener<MutableMap<String, Boolean>>) {
        Log.d(TAG, "requestLocationPermission()")

        when {
            isAboveQ() -> {
                requestPermission(activity, LOCATION_PERMISSION_BACKGROUND, listener)
            }
            isAboveM() -> {
                requestPermission(activity, LOCATION_PERMISSION, listener)
            }
            else -> {
                Log.d(TAG, "requestLocationPermission() Build version is lower than M.")
            }
        }
    }

    private suspend fun waitUntilComponentCreation(activity: ComponentActivity) {
        while (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED).not()) {
            Log.d(TAG, "checkIfResuming --> Activity is not in RESUMED state. Waiting...")
            delay(DELAY_TIME)
        }
    }

    private fun requestPermission(activity: FragmentActivity, permissions: Array<String>, listener: ResultListener<MutableMap<String, Boolean>>) {

        if (permissions.size == 1) {
            Log.d(TAG, "requestPermission() Requesting single permission.")

            val resultLauncher =
                activity.activityResultRegistry.register(PERMISSION_SINGLE, ActivityResultContracts.RequestPermission()) {
                    val resultMap = HashMap<String, Boolean>()
                    resultMap[permissions.first()] = it
                    listener.onResult(resultMap)
                }

            resultLauncher.launch(permissions.first())
        } else if (permissions.size > 1) {
            Log.d(TAG, "requestPermission() Requesting multiple permissions.")

            val resultLauncher = activity.activityResultRegistry.register(PERMISSION_MULTIPLE, ActivityResultContracts.RequestMultiplePermissions()) {
                listener.onResult(it as MutableMap<String, Boolean>)
            }

            resultLauncher.launch(permissions)

        } else {
            Log.d(TAG, "requestPermission() Permissions is empty.")
        }

    }

}