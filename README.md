# Share-Image

Compartilhar imagens tiradas da camera.

# Introdução

A funcionalidade do aplicativo está em realizar o compartilhamento de imagens retiradas da camera.
As imagens retiradas da camera são salvas dentro da pasta com o nome do aplicativo ex: “Android/media/com.sharefiles/Share Images”.

## Screenshots

![image1](screenshots/share-image.gif "Gif animado")

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

Adicionar depêndencias no arquivo - build.gradle (Module: app)

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

Criar o layout para exibição da camera.

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
   xmlns:android="http://schemas.android.com/apk/res/android"
   xmlns:tools="http://schemas.android.com/tools"
   xmlns:app="http://schemas.android.com/apk/res-auto"
   android:layout_width="match_parent"
   android:layout_height="match_parent"
   tools:context=".MainActivity">

   <androidx.camera.view.PreviewView
    android:id="@+id/cameraPreview"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

## Funções

Funções realizadas pra tirar foto, salvar no dispositivo e compratilhar imagem:

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

### Take Photo

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

### Compratilhar Imagem

```kotlin
fun shared(){
  try {
    val uri: Uri? = getUri()
    if (uri != null) {
      val intent = Intent(Intent.ACTION_SEND).apply {
          type = "*/*"
          putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file))
      }
      try {
          context.startActivity(Intent.createChooser(intent, "Shared"))
      } catch (e: Exception) {
          e.printStackTrace()
      }
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

private fun getUri(): Uri? {
  return try {
    FileProvider.getUriForFile(
      context,
      BuildConfig.APPLICATION_ID + ".provider",
      file
    )
  } catch (e: IllegalArgumentException) {
    Log.e("File Selector", "The selected file can't be shared: $file")
    null
  }
}
```
