package com.yh.demo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.yh.watermark.Watermark
import com.yh.watermark.model.FullTextWatermark
import com.yh.watermark.model.TextWatermark
import com.yh.watermark.utils.FileUtils
import kotlinx.android.synthetic.main.act_main.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by CYH on 2020/4/26 09:24
 */
@RuntimePermissions
class MainAct : AppCompatActivity() {

    companion object {
        private const val LOG_TAG = "MainAct"

        const val FILE_PROVIDER_AUTHORITY = ".provider"

        private const val REQUEST_TAKE_CAMERA_PHOTO = 0x123
        private const val REQUEST_OPEN_FILE = 0x124
    }

    private var capturedUri: Uri? = null
    private var mCurrentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.act_main)

//        takePicWithPermissionCheck()

        choosePicWithPermissionCheck()

        btn.setOnClickListener {
            if (TextUtils.isEmpty(mCurrentPhotoPath)) {
                return@setOnClickListener
            }

            val watermarkBitmap = Watermark.create(capturedUri!!)
                .setOutConfigure(0.5F, Bitmap.Config.ARGB_8888)
                .loadWatermark(
                    FullTextWatermark("尊园地产&房星科技")
                        .setLineSpace(4)
                        .setMax(maxTextSize = 30F)
                        .setTextStyle(Color.WHITE, Paint.Style.FILL, R.font.medium3270)
                        .setAlpha((0xFF * 0.2).toInt())
                        .setRotationAngle(-45F)
                    ,
                    TextWatermark("xxx部门 2020-4-22 15:15:32")
                        .setMax(maxTextSize = 20F)
                        .setTextStyle(Color.WHITE, Paint.Style.FILL, R.font.medium3270)
                        .setTextShadow(4F, 2F, 2F, Color.DKGRAY)
                        .setPadding(5, 5, 5, 5)
                        .setGravity(Gravity.TOP or Gravity.START)
                        .setAlpha((0xFF * 0.65).toInt())
                    , TextWatermark("张麻子(15323)")
                        .setMax(maxTextSize = 20F)
                        .setTextStyle(Color.WHITE, Paint.Style.FILL, R.font.medium3270)
                        .setTextShadow(4F, 2F, 2F, Color.DKGRAY)
                        .setPadding(5, 5, 5, 5)
                        .setGravity(Gravity.BOTTOM or Gravity.END)
                        .setAlpha((0xFF * 0.65).toInt())
                )
                .getWatermarkBitmap()
            if (null != watermarkBitmap) {
                img.setImageBitmap(watermarkBitmap)
            }

//            val originOption = BitmapFactory.Options()
//            originOption.inJustDecodeBounds = true
//            BitmapFactory.decodeFile(mCurrentPhotoPath, originOption)
//            val originW = originOption.outWidth
//            val originH = originOption.outHeight
//
//            val outW = originW / 2
//            val outH = originH / 2
//            val newConfig = Bitmap.Config.RGB_565
//            Log.w(LOG_TAG, "[w:$outW * h:$outH]")
//
//            val watermarkBitmap = Bitmap.createBitmap(outW, outH, newConfig)
//            val watermarkCanvas = Canvas(watermarkBitmap)
//
//            val newOption = BitmapFactory.Options()
//            newOption.inJustDecodeBounds = false
//            newOption.inPreferredConfig = Bitmap.Config.RGB_565
//            newOption.outWidth = outW
//            newOption.outHeight = outH
//            newOption.inSampleSize = originW / outW
//            Log.d(LOG_TAG, "inSampleSize: ${newOption.inSampleSize}")
//            watermarkCanvas.drawBitmap(
//                BitmapFactory.decodeFile(mCurrentPhotoPath, newOption),
//                0F,
//                0F,
//                null
//            )
//
//            val watermarkPaint = Paint()
//
//            val watermarkText = WatermarkText()
//            watermarkText.text = "xxx部门 2020-4-22 15:15:32 张麻子(15323) 张麻子(15323) 张麻子(15323) 张麻子(15323) 张麻子(15323)"
//            watermarkText.maxW = outW
//            watermarkText.maxTextSize = 30F
//            watermarkText.textAlpha = (0xFF * 0.65).toInt()
//            watermarkText.textColor = Color.WHITE
//            watermarkText.textFount = R.font.medium3270
//            watermarkText.textShadowBlurRadius = 1F
//            watermarkText.textShadowXOffset = 2F
//            watermarkText.textShadowYOffset = 2F
//            watermarkText.textShadowColor = Color.DKGRAY
//            val scaledWMBitmap = watermarkText.convert2Bitmap(outW, outH)
////            scaledWMBitmap = adjustPhotoRotation(
////                scaledWMBitmap,
////                watermarkText.position
////            )
//            if (null != scaledWMBitmap) {
//                watermarkCanvas.drawBitmap(
//                    scaledWMBitmap,
//                    watermarkText.position.positionX * outW,
//                    watermarkText.position.positionY * outH,
//                    watermarkPaint
//                )
//            }
//
//            img.setImageBitmap(watermarkBitmap)
//            Log.w(LOG_TAG, "watermarkBitmap: $watermarkBitmap")

//            if (null != watermarkBitmap) {
//                thread {
//                    val outPath = createMediaFile()
//                    if (null != outPath) {
//                        val bos = BufferedOutputStream(FileOutputStream(outPath))
//                        watermarkBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
//                        bos.close()
//                    }
//                    Log.w(LOG_TAG, "write: $outPath")
//                }
//            }
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun choosePic() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, REQUEST_OPEN_FILE)
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun takePic() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createMediaFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Log.d(LOG_TAG, "Error occurred while creating the file")

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCurrentPhotoPath = photoFile.absolutePath
                // Get the content URI for the image file
                capturedUri = FileProvider.getUriForFile(
                    this,
                    packageName + FILE_PROVIDER_AUTHORITY,
                    photoFile
                )

                Log.d(LOG_TAG, "takePic: $capturedUri")

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri)

                startActivityForResult(takePictureIntent, REQUEST_TAKE_CAMERA_PHOTO)
            }
        }
    }

    @Throws(IOException::class)
    private fun createMediaFile(): File? { // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val file = File.createTempFile(fileName, ".jpg", storageDir)
        Log.d(LOG_TAG, "createMediaFile: $file")
        return file
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK == resultCode) {
            when (requestCode) {
                REQUEST_TAKE_CAMERA_PHOTO -> {
                    img.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath))
                }
                REQUEST_OPEN_FILE -> {
                    displayImg(data)
                }
            }
        }
    }

    private fun displayImg(data: Intent?) {
        if (null == data) {
            return
        }
        val uri = data.data ?: return
        capturedUri = uri
        mCurrentPhotoPath = FileUtils.getPath(applicationContext, uri)
        img.setImageURI(uri)
        Log.d(LOG_TAG, "displayImg: $capturedUri")
        Log.d(LOG_TAG, "displayImg: $mCurrentPhotoPath")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        onRequestPermissionsResult(requestCode, grantResults)
    }
}