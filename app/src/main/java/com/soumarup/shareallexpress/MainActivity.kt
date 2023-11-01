package com.soumarup.shareallexpress

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Functionality to make the splash page go full-screen mode
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //Functionality to traverse to the Start Page from Splash Screen Page after 3000ms
        Handler(Looper.getMainLooper()).postDelayed({
            val intent: Intent = Intent(this, StartPage::class.java)
            startActivity(intent)
            finish()
        }, 3000)

    }
}