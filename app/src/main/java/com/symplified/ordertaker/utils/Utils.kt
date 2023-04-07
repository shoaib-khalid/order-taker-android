package com.symplified.ordertaker.utils

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import java.text.DecimalFormat

object Utils {

    const val ASPECT_RATIO_TOLERANCE = 0.01f
    private val formatter: DecimalFormat = DecimalFormat("#,##0.00")
    private const val TAG = "Utils"

//    /** Convert NV21 format byte buffer to bitmap. */
//    fun convertToBitmap(data: ByteBuffer, width: Int, height: Int, rotationDegrees: Int): Bitmap? {
//        data.rewind()
//        val imageInBuffer = ByteArray(data.limit())
//        data.get(imageInBuffer, 0, imageInBuffer.size)
//        try {
//            val image = YuvImage(
//                imageInBuffer, InputImage.IMAGE_FORMAT_NV21, width, height, null
//            )
//            val stream = ByteArrayOutputStream()
//            image.compressToJpeg(Rect(0, 0, width, height), 80, stream)
//            val bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
//            stream.close()
//
//            // Rotate the image back to straight.
//            val matrix = Matrix()
//            matrix.postRotate(rotationDegrees.toFloat())
//            return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
//        } catch (e: java.lang.Exception) {
//            Log.e(TAG, "Error: " + e.message)
//        }
//        return null
//    }
//
//    fun isPortraitMode(context: Context): Boolean =
//        context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
//
//    fun generateValidPreviewSizeList(camera: Camera): List<CameraSizePair> {
//        val parameters = camera.parameters
//        val supportedPreviewSizes = parameters.supportedPreviewSizes
//        val supportedPictureSizes = parameters.supportedPictureSizes
//        val validPreviewSizes = ArrayList<CameraSizePair>()
//        for (previewSize in supportedPreviewSizes) {
//            val previewAspectRatio = previewSize.width.toFloat() / previewSize.height.toFloat()
//
//            // By looping through the picture sizes in order, we favor the higher resolutions.
//            // We choose the highest resolution in order to support taking the full resolution
//            // picture later.
//            for (pictureSize in supportedPictureSizes) {
//                val pictureAspectRatio = pictureSize.width.toFloat() / pictureSize.height.toFloat()
//                if (abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
//                    validPreviewSizes.add(CameraSizePair(previewSize, pictureSize))
//                    break
//                }
//            }
//        }
//
//        // If there are no picture sizes with the same aspect ratio as any preview sizes, allow all of
//        // the preview sizes and hope that the camera can handle it.  Probably unlikely, but we still
//        // account for it.
//        if (validPreviewSizes.isEmpty()) {
//            Log.w(TAG, "No preview sizes have a corresponding same-aspect-ratio picture size.")
//            for (previewSize in supportedPreviewSizes) {
//                // The null picture size will let us know that we shouldn't set a picture size.
//                validPreviewSizes.add(CameraSizePair(previewSize, null))
//            }
//        }
//
//        return validPreviewSizes
//    }

    fun getScreenWidthInDp(activity: Activity): Float {
        return if (Build.VERSION.SDK_INT >= 30) {
            activity.windowManager.currentWindowMetrics.bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        } / activity.resources.displayMetrics.density
    }

    fun formatPrice(price: Double): String = formatter.format(price)
}