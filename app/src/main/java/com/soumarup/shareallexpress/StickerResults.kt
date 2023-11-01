package com.soumarup.shareallexpress

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class StickerResults : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker_results)

        var gifUrl:String = String()

        fun loadSticker() {

            val progressBar: ProgressBar = findViewById<ProgressBar>(R.id.progressBarStickerPage)
            progressBar.visibility = View.VISIBLE
            val requestQueue = Volley.newRequestQueue(this)
            val url = "https://api.giphy.com/v1/gifs/random?api_key=0OGsZQHHxHKhbHN5OtABYXhPdrGHpRS9"
            val stickerDisplayView: ImageView = findViewById<ImageView>(R.id.stickerDisplayView)

            val jsonObjectRequest: JsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response->
                    try {
                        // Extract the "embed_url" from the JSON response
                        val dataObject = response.getJSONObject("data")
                        val imageUrl = dataObject.getJSONObject("images")
                            .getJSONObject("downsized").getString("url")

                        gifUrl = imageUrl

                        Glide.with(this)
                            .asGif()
                            .override(stickerDisplayView.width, stickerDisplayView.height)
                            .listener(object: RequestListener<GifDrawable> {

                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<GifDrawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    progressBar.visibility = View.GONE
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: GifDrawable?,
                                    model: Any?,
                                    target: Target<GifDrawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    progressBar.visibility = View.GONE
                                    return false
                                }
                            })
                            .load(imageUrl)
                            .into(stickerDisplayView);

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }, {
                    Log.d("Error", "API FETCH ERROR")
                }
            )

            requestQueue.add(jsonObjectRequest)

        }
        loadSticker()

        //On-click functionality of the next button
        val nextButton: ImageButton = findViewById<ImageButton>(R.id.nextStickerButton)
        nextButton.setOnClickListener {

            //To add click sound while pressing the button
            nextButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality demonstrating the loading of stickers(GIFs) on clicking the next button
            loadSticker()
        }

        //On-click functionality of the share button
        val shareButton: ImageButton = findViewById<ImageButton>(R.id.shareStickerButton)
        shareButton.setOnClickListener {

            //To add click sound while pressing the button
            shareButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality demonstrating the sharing of stickers(GIFs)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, gifUrl)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share GIF")
            startActivity(Intent.createChooser(shareIntent, "Share GIF URL"))
        }

        //On click functionality of the download button
        val downloadButton: ImageButton = findViewById<ImageButton>(R.id.downloadStickerButton)
        downloadButton.setOnClickListener(View.OnClickListener {

            //To add click sound while pressing the button
            downloadButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality demonstrating the downloading of stickers(GIFs) to your device
            Glide.with(this)
                .asBitmap()
                .load(gifUrl)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        var fileOutputStream: FileOutputStream? = null
                        val path = Environment.getExternalStorageDirectory()
                        val directory: File = File(path.absolutePath + "/Download")
                        directory.mkdir()
                        val fileName: String = String.format("%d.png", System.currentTimeMillis())
                        val outputFile: File = File(directory, fileName)

                        Toast.makeText(applicationContext,
                            "GIF saved to Downloads", Toast.LENGTH_LONG).show()

                        try {
                            fileOutputStream = FileOutputStream(outputFile)
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                            fileOutputStream.flush()
                            fileOutputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                })
        })
    }
}