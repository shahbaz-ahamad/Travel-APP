package com.example.travel

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.example.travel.databinding.ActivityGetStartedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GetStarted : AppCompatActivity() {
    private lateinit var binding:ActivityGetStartedBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        hideStatusBar()

        auth=Firebase.auth
        binding.constraintlayout.setOnClickListener {
//            goToMainActivity()
            goToLoginActicvity()
        }
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For API level 30 and above (Android 11+)
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            // For API level 30 and below (Android 10 and below)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }    

    private fun goToMainActivity() {
        val intent= Intent(this@GetStarted,MainActivity::class.java)
        startActivity(intent)
    }

    private fun goToLoginActicvity(){
        val intent= Intent(this@GetStarted,LoginActivity::class.java)
        startActivity(intent)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser!=null){
            goToMainActivity()
        }

    }


}