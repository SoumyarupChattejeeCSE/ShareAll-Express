package com.soumarup.shareallexpress

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class HomePage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        //On-click functionality of the Emoji Button
        val emojiButton: ImageButton = findViewById<ImageButton>(R.id.emojiButton)
        emojiButton.setOnClickListener {

            //To add click sound while pressing the button
            emojiButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality to traverse to the Emoji Results Page from Home Page
            val intent: Intent = Intent(this, EmojiResults::class.java)
            startActivity(intent)
        }

        //On-click functionality of the Meme button
        val memeButton: ImageButton = findViewById<ImageButton>(R.id.memeButton)
        memeButton.setOnClickListener {

            //To add click sound while pressing the button
            memeButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality to traverse to the Meme Results Page from Home Page
            val intent: Intent = Intent(this, MemeResults::class.java)
            startActivity(intent)
        }

        //On-click functionality of the Sticker button
        val stickerButton: ImageButton = findViewById<ImageButton>(R.id.stickerButton)
        stickerButton.setOnClickListener {

            //To add click sound while pressing the button
            stickerButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality to traverse to the Sticker Results Page from the Home Page
            val intent: Intent= Intent(this, StickerResults::class.java)
            startActivity(intent)
        }

        //On-click functionality of the Back To Home Page button
        val homePageBackButton: ImageButton = findViewById<ImageButton>(R.id.homePageBackButton)
        homePageBackButton.setOnClickListener {

            //To add click sound while pressing the button
            homePageBackButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality to traverse to Start Page back from Home Page
            val intent: Intent = Intent(this, StartPage::class.java)
            startActivity(intent)
        }

    }
}