package com.shareimages

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class AddPermissions(private val activity: Activity) {

    fun requestPermission() {
        var permissionsToRequest = mutableListOf<String>()
        if (!getPermission()) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsToRequest.toTypedArray(),
                0
            )
        }
    }

    private fun getPermission() = ActivityCompat.checkSelfPermission(
        activity,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
        activity,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
        activity,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
