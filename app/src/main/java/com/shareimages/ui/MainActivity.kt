package com.shareimages.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.shareimages.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private var cameraSelector: CameraSelector? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var isRecording: Boolean = false
    private var isRotateCamera: Boolean = false
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        eventListener()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun eventListener() {
        materialButton.setOnClickListener {
            takePhoto()
        }

        topAppBar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.item_galery->{
                    startActivity(Intent(this, GaleryActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun takePhoto() {

        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.UK)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object :
            ImageCapture.OnImageSavedCallback {

            override fun onError(exc: ImageCaptureException) {
                var msg = "Photo capture failed: ${exc.message}"
                Snackbar.make(materialButton, "$msg", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(resources.getColor(android.R.color.holo_green_light))
                    .setTextColor(resources.getColor(android.R.color.white))
                    .show()
                Log.e(TAG, "$msg", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                val msg = "Photo capture succeeded: $savedUri"
                Snackbar.make(materialButton, "$msg", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(resources.getColor(android.R.color.holo_green_light))
                    .setTextColor(resources.getColor(android.R.color.white))
                    .show()
                Log.i(TAG, msg)
            }
        })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraPreview.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .build()

            // Select back camera as a default
            cameraSelector = selectCamera(Camera.BACK)

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                cameraSelector?.let {
                    cameraProvider.bindToLifecycle(
                        this, it, preview, imageCapture
                    )
                }

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun selectCamera(tipo: Camera): CameraSelector {
        return when (tipo) {
            Camera.BACK -> CameraSelector.DEFAULT_BACK_CAMERA
            Camera.FRONT -> CameraSelector.DEFAULT_FRONT_CAMERA
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "${permissions[i]} granted")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}


enum class Camera {
    BACK,
    FRONT
}


