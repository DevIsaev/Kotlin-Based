package com.example.camera2api

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.camera2api.databinding.ActivityMainBinding
import com.example.camera2api.ml.AutoModel1
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    lateinit var cameraM: CameraManager
    lateinit var handler: Handler
    lateinit var cameraDevice: CameraDevice

    lateinit var bitmap: Bitmap
    lateinit var model1: AutoModel1
    lateinit var imageProcessor: ImageProcessor

    lateinit var labels: List<String>
    var colors = listOf(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED
    )
    val paint = Paint()

    // List to store results for a number of frames
    val detectionResults = mutableListOf<List<Pair<Float, RectF>>>()
    val detectionWindow = 1 // Number of frames to average
    val scaleFactor = 0.6f // Scaling factor to reduce the size of the rectangles

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
        window?.apply {
            setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        get_permission()
        labels = FileUtil.loadLabels(this@MainActivity, "labels.txt")
        imageProcessor = ImageProcessor.Builder().add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR)).build()
        model1 = AutoModel1.newInstance(this@MainActivity)
        var handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        binding.textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {}

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                bitmap = binding.textureView.bitmap!!

                // Creates inputs for reference.
                var image = TensorImage.fromBitmap(bitmap)
                image = imageProcessor.process(image)
                // Runs model inference and gets result.
                val outputs = model1.process(image)
                val locations = outputs.locationsAsTensorBuffer.floatArray
                val classes = outputs.classesAsTensorBuffer.floatArray
                val scores = outputs.scoresAsTensorBuffer.floatArray
                val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer.floatArray

                val h = bitmap.height
                val w = bitmap.width

                // Collect results for the current frame
                val currentDetections = mutableListOf<Pair<Float, RectF>>()
                var x = 0
                scores.forEachIndexed { index, fl ->
                    x = index
                    x *= 4
                    if (fl > 0.5) {
                        val left = locations[x + 1] * w
                        val top = locations[x] * h
                        val right = locations[x + 3] * w
                        val bottom = locations[x + 2] * h

                        val width = (right - left) * scaleFactor
                        val height = (bottom - top) * scaleFactor

                        val scaledLeft = left + (right - left - width) / 2
                        val scaledTop = top + (bottom - top - height) / 2
                        val scaledRight = scaledLeft + width
                        val scaledBottom = scaledTop + height

                        val rect = RectF(scaledLeft, scaledTop, scaledRight, scaledBottom)
                        currentDetections.add(Pair(fl, rect))
                    }
                }

                // Add current detections to the window and keep its size constant
                detectionResults.add(currentDetections)
                if (detectionResults.size > detectionWindow) {
                    detectionResults.removeAt(0)
                }

                // Average the detections over the window
                val averagedDetections = mutableListOf<Pair<Float, RectF>>()
                detectionResults.flatten().groupBy { it.second }.forEach { (rect, groupedDetections) ->
                    val averageScore = groupedDetections.map { it.first }.average().toFloat()
                    averagedDetections.add(Pair(averageScore, rect))
                }

                // Create a mutable bitmap for drawing
                val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutable)

                paint.textSize = h / 50f
                paint.strokeWidth = h / 125f

                // Draw averaged detections
                averagedDetections.forEachIndexed { index, detection ->
                    paint.color = colors[index % colors.size]
                    paint.style = Paint.Style.STROKE
                    canvas.drawRect(detection.second, paint)
                    paint.style = Paint.Style.FILL
                    canvas.drawText(
                        labels[classes[index % classes.size].toInt()] + " ",
                        detection.second.left, detection.second.top, paint
                    )
                }

                binding.imView.setImageBitmap(mutable)
            }
        }

        cameraM = getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    override fun onDestroy() {
        super.onDestroy()
        // Releases model resources if no longer used.
        model1.close()
    }

    @SuppressLint("MissingPermission")
    fun openCamera() {
        cameraM.openCamera(cameraM.cameraIdList[0], object : CameraDevice.StateCallback() {
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0
                var surfaceTexture = binding.textureView.surfaceTexture
                var surface = Surface(surfaceTexture)
                var captureReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureReq.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureReq.build(), null, null)
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {}

                }, handler)
            }

            override fun onDisconnected(p0: CameraDevice) {}

            override fun onError(p0: CameraDevice, p1: Int) {}

        }, handler)
    }

    fun get_permission() {
        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            get_permission()
        }
    }
}
