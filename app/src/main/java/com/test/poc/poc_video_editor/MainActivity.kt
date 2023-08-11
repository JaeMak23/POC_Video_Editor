package com.test.poc.poc_video_editor

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.test.poc.poc_video_editor.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var videoUri: Uri? = null
    var savedFile: File? = null

    // declaring a null variable for MediaController
    private var mediaControls: MediaController? = null

    private val videoContract =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            videoUri = uri
            val ss = "path ${videoUri?.path}"
            binding.importedPathTextView.text = ss
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // data-binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this

        binding.apply {
            openVideo.setOnClickListener { openVideo() }
            playVideoDirectly.setOnClickListener { playVideoDirectly() }

            saveVideo.setOnClickListener {
                savedFile = saveVideoToStorage()
                val ss = "path ${savedFile?.absolutePath}"
                saveToDeviceTextView.text = ss
            }
            playVideoFromDevice.setOnClickListener { playDeviceVideo() }
        }
    }


    private fun openVideo() {
        videoContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
    }

    private fun playVideoDirectly() {
        ll("Playing video directly called.")
        ll(" - videoUri : $videoUri.")
        videoUri?.let {
            ll(" - Playing video directly.")
            playVideo(it)
        } ?: {
            ll(" - Playing video directly is stopped. Empty uri.")
        }
    }

    private fun playDeviceVideo() {
        ll("Playing device video called.")
        ll(" - file : $savedFile.")
        val uri = savedFile?.toUri()
        ll(" - converted uri : $uri.")

        uri?.let {
            ll(" - Playing device video.")
            playVideo(it)
        } ?: {
            ll(" - Playing device video is stopped. Empty file.")
        }
    }


    private fun playVideo(uri: Uri) {
        binding.apply {
            if (mediaControls == null) {
                // creating an object of media controller class
                mediaControls = MediaController(this@MainActivity)

                // set the anchor view for the video view
                mediaControls!!.setAnchorView(binding.videoView)
            }

            // set the media controller for video view
            videoView.setMediaController(mediaControls)

            videoView.setVideoURI(uri)
            videoView.requestFocus()

            // starting the video
            videoView.start()

            // display a toast message
            // after the video is completed
            videoView.setOnCompletionListener {
                Toast.makeText(applicationContext, "Video completed", Toast.LENGTH_LONG).show()
                true
            }

            // display a toast message if any
            // error occurs while playing the video
            videoView.setOnErrorListener { mp, what, extra ->
                Toast.makeText(
                    applicationContext, "An Error Occurred " +
                            "While Playing Video !!!", Toast.LENGTH_LONG
                ).show()
                false
            }
        }
    }

    fun saveVideoToStorage(/*context: Context, uri: Uri*/): File {
        val uri = videoUri!!
        ll(" Save video called")
        ll(" - uri : $uri")

        // Get the file name from the URI.
        val fileName = uri.lastPathSegment ?: "video.mp4"

        ll(" - uri lastPathSegment : ${uri.lastPathSegment}")
        ll(" - file name : $fileName")

        // Create a file in the external storage directory with the specified file name.
        val file = File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName)

        ll(" - file path : ${file.absolutePath}")
        ll(" -  working with file.")

        // Create an input stream from the URI.
        val inputStream = contentResolver.openInputStream(uri)

        // Create an output stream to the file.
        val outputStream = FileOutputStream(file)

        // Copy the data from the input stream to the output stream.
        val bytes = ByteArray(1024)
        var read: Int
        while (inputStream?.read(bytes).also { read = it ?: -1 } != -1) {
            outputStream.write(bytes, 0, read)
        }
        ll(" -  file is closing....")
        // Close the streams.
        inputStream?.close()
        outputStream.close()
        ll(" -  file is closed.")

        // Return the file.
        ll(" - before returning the file.")
        ll(" - - file : $file")
        ll(" - - filepath : ${file.absolutePath}")
        ll(" End of save video.")
        return file
    }

}

fun ll(msg: Any?) = Log.i("poc", "$msg")