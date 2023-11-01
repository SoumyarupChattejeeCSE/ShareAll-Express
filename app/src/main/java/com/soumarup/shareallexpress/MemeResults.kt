package com.soumarup.shareallexpress

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MemeResults : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_results)

        loadMeme()

        //On click functionality of the next button
        val nextButton: ImageButton = findViewById<ImageButton>(R.id.nextMemeButton)
        nextButton.setOnClickListener {

            //To add click sound while pressing the button
            nextButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality demonstrating the loading of memes on clicking the next button
            loadMeme()
        }

        //On click functionality of the share button
        val shareButton: ImageButton = findViewById<ImageButton>(R.id.shareMemeButton)
        shareButton.setOnClickListener {

            //To add click sound while pressing the button
            shareButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality demonstrating the sharing of memes
            val memeDisplayView: ImageView = findViewById<ImageView>(R.id.memeDisplayView)
            val imageDrawable: Drawable = memeDisplayView.getDrawable()
            val imageBitmap: Bitmap = (imageDrawable as BitmapDrawable).bitmap

            val path: String = MediaStore.Images.Media.insertImage(
                contentResolver,
                imageBitmap,
                "Image Description",
                null
            )
            val uri: Uri = Uri.parse(path)

            try {
                val intent: Intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/jpeg"
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(intent, "Share Image Via:"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //On click functionality of the download button
        val downloadButton:ImageButton = findViewById<ImageButton>(R.id.downloadMemeButton)
        downloadButton.setOnClickListener(View.OnClickListener {

            //To add click sound while pressing the button
            downloadButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality demonstrating the downloading of memes to your device
            val memeDisplayView: ImageView = findViewById<ImageView>(R.id.memeDisplayView)
            val bitmapDrawable: BitmapDrawable = memeDisplayView.getDrawable() as BitmapDrawable
            val bitmap: Bitmap = bitmapDrawable.getBitmap()
            var fileOutputStream: FileOutputStream? = null
            val path = Environment.getExternalStorageDirectory()
            val directory: File = File(path.absolutePath + "/Download")
            directory.mkdir()
            val fileName: String = String.format("%d.jpeg", System.currentTimeMillis())
            val outputFile: File = File(directory, fileName)

            Toast.makeText(applicationContext,
                "Meme saved to Downloads", Toast.LENGTH_LONG).show()

            try {
                fileOutputStream = FileOutputStream(outputFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()
                val intent: Intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                intent.data = Uri.fromFile(outputFile)
                sendBroadcast(intent)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun loadMeme() {

        val progressBar: ProgressBar = findViewById<ProgressBar>(R.id.progressBarMemePage)
        progressBar.visibility = View.VISIBLE
        val requestQueue = Volley.newRequestQueue(this)
        val url = "https://meme-api.com/gimme"
        val memeDisplayView: ImageView = findViewById<ImageView>(R.id.memeDisplayView)

        val jsonObjectRequest: JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response->
                try {
                    val imageUrl = response.getString("url")
                    Glide.with(this)
                        .load(imageUrl)
                        .override(memeDisplayView.width, memeDisplayView.height)
                        .listener(object: RequestListener<Drawable> {

                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar.visibility = View.GONE
                                return false
                            }
                        })
                        .into(memeDisplayView);
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }, {
                Log.d("Error", "API FETCH ERROR")
            }
        )
        requestQueue.add(jsonObjectRequest)
    }


}