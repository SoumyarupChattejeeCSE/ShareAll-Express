package com.soumarup.shareallexpress

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract.Directory
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.random.Random


class EmojiResults : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emoji_results)

        val progressBar: ProgressBar = findViewById<ProgressBar>(R.id.progressBarEmojiPage)

        val emojiInformationMap = LinkedHashMap<String, String>()
        val emojiIndexedInformationMap = LinkedHashMap<Int, ArrayList<String>>()
        var index:Int = 0

        CoroutineScope(Dispatchers.IO).launch {
            progressBar.visibility = View.VISIBLE
            val response: JSONArray = fetchAndProcessData()
            withContext(Dispatchers.Main) {
                val pageTitle: TextView = findViewById<TextView>(R.id.emojiPageHeading)
                for (i in 0 until response.length()) {
                    val emojiObject: JSONObject = response.getJSONObject(i)
                    val emojiUnicodeName: String = emojiObject.getString("unicodeName")
                    val emojiUnicodeNameArray = emojiUnicodeName.split(" ")
                    val emojiName: String = emojiUnicodeNameArray.subList(1, emojiUnicodeNameArray.size).joinToString(" ")
                    val emojiImageText: String = emojiObject.getString("character")

                    val description: ArrayList<String> = ArrayList<String>()
                    description.add(emojiName)
                    description.add(emojiImageText)

                    emojiInformationMap[emojiName] = emojiImageText
                    emojiIndexedInformationMap[index] = description
                    index += 1
                }

                //On-load functionality of the Emoji Page
                progressBar.visibility = View.GONE
                val emojiDisplayView: ImageView = findViewById<ImageView>(R.id.emojiDisplayView)
                val bitmap = createEmojiBitmap(emojiIndexedInformationMap.get(0)?.get(1).toString())
                emojiDisplayView.setImageBitmap(bitmap)

                val emojiTitle: TextView = findViewById<TextView>(R.id.displayEmojiTitle)
                emojiTitle.text = emojiIndexedInformationMap.get(0)?.get(0).toString()

                //On-click functionality of the search button
                val searchButton: ImageButton = findViewById<ImageButton>(R.id.searchButton)
                searchButton.setOnClickListener {

                    //To add click sound while pressing the button
                    searchButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

                    //Functionality demonstrating the emoji search along with the handling of edge cases
                    if(emojiTitle.text.toString().isEmpty()) {
                        emojiDisplayView.setImageResource(R.drawable.image_unavailable)
                        emojiTitle.text = "Not Available"
                    }

                    val searchBar: EditText = findViewById<EditText>(R.id.searchBar)
                    val searchEmojiTitle = searchBar.text.toString()

                    var matchFound: Boolean = true
                    for((key, value) in emojiInformationMap) {
                        matchFound = searchEmojiTitle.equals(key, ignoreCase = true)
                        if(matchFound == true) {
                            val bitmap: Bitmap = createEmojiBitmap(value.toString())
                            emojiDisplayView.setImageBitmap(bitmap)
                            emojiTitle.text = key
                            break
                        }
                    }
                    if(matchFound == false) {
                        emojiDisplayView.setImageResource(R.drawable.image_unavailable)
                        emojiTitle.text = "Emoji Not Available"
                    }
                }

                //On-click functionality of reload/refresh button
                val refreshButton: ImageButton = findViewById<ImageButton>(R.id.refreshButton)
                refreshButton.setOnClickListener {

                    //To add click sound while pressing the button
                    refreshButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

                    //Functionality demonstrating the working of refresh button
                    progressBar.visibility = View.VISIBLE
                    val bitmap: Bitmap = createEmojiBitmap(emojiIndexedInformationMap.get(0)?.get(1).toString())
                    progressBar.visibility = View.GONE
                    emojiDisplayView.setImageBitmap(bitmap)
                    emojiTitle.text = emojiIndexedInformationMap.get(0)?.get(0).toString()
                    val searchBar: EditText = findViewById<EditText>(R.id.searchBar)
                    searchBar.text.clear()

                }

                //On-click functionality of the next button
                val nextButton: ImageButton = findViewById<ImageButton>(R.id.nextEmojiButton)
                nextButton.setOnClickListener {

                    //To add click sound while pressing the button
                    nextButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

                    //Functionality demonstrating the displaying of next emojis
                    progressBar.visibility = View.VISIBLE
                    val totalNumberOfEmojis = emojiIndexedInformationMap.size
                    val random = Random.Default
                    val randomNumber = random.nextInt(0,  totalNumberOfEmojis)
                    progressBar.visibility = View.GONE
                    emojiDisplayView.setImageBitmap(createEmojiBitmap
                        (emojiIndexedInformationMap.get(randomNumber)?.get(1).toString()))
                    emojiTitle.text = emojiIndexedInformationMap.get(randomNumber)?.get(0).toString()
                }

                //On click functionality of the share button
                val shareButton: ImageButton = findViewById<ImageButton>(R.id.shareEmojiButton)
                shareButton.setOnClickListener {

                    //To add click sound while pressing the button
                    shareButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

                    //Functionality demonstrating the sharing of emojis
                    val imageDrawable: Drawable = emojiDisplayView.getDrawable()
                    val imageBitmap:Bitmap = (imageDrawable as BitmapDrawable).bitmap

                    val path: String = MediaStore.Images.Media.insertImage(
                        contentResolver,
                        imageBitmap,
                        "Image Description",
                        null)

                    val uri: Uri = Uri.parse(path)

                    try {
                        val intent: Intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/jpeg"
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        startActivity(Intent.createChooser(intent, "Share Image Via:"))
                    } catch(e: Exception) {
                        e.printStackTrace()
                    }
                }

                //On click functionality of the download button
                val downloadButton:ImageButton = findViewById<ImageButton>(R.id.downloadEmojiButton)
                downloadButton.setOnClickListener(View.OnClickListener {

                    //To add click sound while pressing the button
                    downloadButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

                    //Functionality demonstrating the downloading of emojis to your device
                    val bitmapDrawable: BitmapDrawable = emojiDisplayView.getDrawable() as BitmapDrawable
                    val bitmap: Bitmap = bitmapDrawable.getBitmap()
                    var fileOutputStream: FileOutputStream? = null
                    val path = Environment.getExternalStorageDirectory()
                    val Directory: File = File(path.absolutePath + "/Download")
                    Directory.mkdir()
                    val filename = String.format("%d.jpeg", System.currentTimeMillis())
                    val outputFile: File = File(Directory, filename)

                    Toast.makeText(applicationContext,
                        "Emoji saved to Downloads", Toast.LENGTH_LONG).show()

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
        }
    }
    private suspend fun fetchAndProcessData(): JSONArray {
        return suspendCancellableCoroutine { continuation ->

            val requestQueue = Volley.newRequestQueue(this)
            val url = "https://emoji-api.com/emojis?access_key=b7bb9fd18c963094ea94cfcb89196b7e651c414f"

            val jsonArrayRequest = JsonArrayRequest(
                Request.Method.GET, url, null,
                { response ->
                    try {
                        continuation.resumeWith(Result.success(response))
                    } catch(e: Exception) {
                        e.printStackTrace()
                    }
                },
                { continuation.cancel(Exception("API Fetch Error"))
                })

            requestQueue.add(jsonArrayRequest)
        }
    }
    private fun createEmojiBitmap(emojiUnicode: String): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = 200f // Adjust text size as needed
        paint.color = Color.BLACK // Text color
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        val textBounds = android.graphics.Rect()
        paint.getTextBounds(emojiUnicode, 0, emojiUnicode.length, textBounds)

        val width = textBounds.width()
        val height = textBounds.height()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint()
        backgroundPaint.color = Color.BLACK
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        canvas.drawText(emojiUnicode, 0f, -textBounds.top.toFloat(), paint)

        return bitmap
    }
}