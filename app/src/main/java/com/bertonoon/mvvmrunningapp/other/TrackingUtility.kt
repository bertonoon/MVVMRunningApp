package com.bertonoon.mvvmrunningapp.other

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions

object TrackingUtility {

    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun hasForegroundPermission(context: Context) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            EasyPermissions.hasPermissions(context, Manifest.permission.FOREGROUND_SERVICE)
        } else {
            true
        }

    fun hasNotificationPermission(context: Context) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.hasPermissions(context, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }


}