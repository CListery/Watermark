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
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.yh.appinject.logger.logD
import com.yh.appinject.logger.logE
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
                logE("Error occurred while creating the file", throwable = ex)
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

                logD("takePic: $capturedUri")

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
        logD("createMediaFile: $file")
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
        logD("displayImg:\n$capturedUri\n$mCurrentPhotoPath")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        onRequestPermissionsResult(requestCode, grantResults)
    }
}