# Share-Image

Compartilhar imagens tiradas da camera.

# Introdução

A funcionalidade do aplicativo está em realizar o compartilhamento de imagens retiradas da camera.
As imagens retiradas da camera são salvas dentro da pasta com o nome do aplicativo ex: “Android/media/com.sharefiles/Share Images”.

## Screenshots

![image1](screenshots/image_animada.gif "Gif animado")

## Material Design

Componentes utilizados do material Design :

- [App bars: top](https://material.io/components/app-bars-top)
- [Buttons](https://material.io/components/buttons)
- [Cards](https://material.io/components/cards)

## Bibliotecas utilizadas

- [Recyclerview](https://developer.android.com/guide/topics/ui/layout/recyclerview)
- [Camera X](https://developer.android.com/training/camerax)

## Referências

- [Bitmap](https://developer.android.com/reference/android/graphics/Bitmap)
- [BitmapFactory](https://developer.android.com/reference/android/graphics/BitmapFactory)

## Passos para começar

- Pré-requisitos:

  - O nível mínimo de API com suporte é 21.
  - Android Studio 3.6 ou superior.

## Configuração do projeto

Adicionar depêndencias no arquivo - Build.gradle (Module: app)

```gradle
  apply plugin: 'kotlin-android-extensions'

  android {
    compileOptions {
      sourceCompatibility JavaVersion.VERSION_1_8
      targetCompatibility JavaVersion.VERSION_1_8
    }
  }

  dependencies {
    def camerax_version = "1.0.0-beta07"
    implementation "androidx.camera:camera-camera2:$camerax_version"
    implementation "androidx.camera:camera-lifecycle:$camerax_version"
    implementation "androidx.camera:camera-view:1.0.0-alpha14"
  }
```

## Funções

Funções realizadas pra baixar e salvar as imagens:

### Start Camera

```kotlin
private fun startCamera() {
  val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
  cameraProviderFuture.addListener(Runnable {

    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

    val preview = Preview.Builder()
      .build()
      .also {
        it.setSurfaceProvider(cameraPreview.createSurfaceProvider())
      }

    imageCapture = ImageCapture.Builder()
      .setTargetRotation(Surface.ROTATION_0)
      .build()

    cameraSelector = selectCamera(Camera.BACK)

    try {
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

```

### take Photo

```kotlin
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


```

### Buscar Imagem

```kotlin
fun getImage(): ArrayList<Bitmap>? {

  var images = arrayListOf<Bitmap>()
  try {

    var files = File("$getPath")
    var listFiles = files.listFiles()
    var bitmap: Bitmap? = null

    for (file in listFiles) {
      bitmap = BitmapFactory.decodeFile("${getPath}/${file.name}")
      images.add(bitmap)
    }

    return images
  } catch (e: java.lang.Exception) {
    e.printStackTrace()
  }

  return arrayListOf()
}

private val getPath = context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)


```
