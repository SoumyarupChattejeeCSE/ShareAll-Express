package com.soumarup.shareallexpress

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StartPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_page)

        //On-click functionality of the get started button
        val getStartedButton:Button = findViewById<Button>(R.id.getStartedButton)
        getStartedButton.setOnClickListener {

            //To add click sound while pressing the button
            getStartedButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality to traverse to the Home Page from the Start Page
            val intent: Intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

        //On-click functionality of the exit button
        val exitButton: Button = findViewById<Button>(R.id.exitButton)
        exitButton.setOnClickListener {

            //To add click sound while pressing the button
            exitButton.playSoundEffect(android.view.SoundEffectConstants.CLICK)

            //Functionality to clear the entire activity stack and close our application
            finishAffinity()
            System.exit(0)
        }
    }
}