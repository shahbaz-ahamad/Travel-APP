package com.example.travel

import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.travel.databinding.ActivityMainBinding
import com.example.travel.fragment.HomeFragment
import com.example.travel.fragment.LikedFragment
import com.example.travel.fragment.ProfileFragment
import com.example.travel.fragment.SettingFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(){

    private lateinit var binding:ActivityMainBinding
    private lateinit var fragmentManager:FragmentManager
    private lateinit var fragmentTransaction:FragmentTransaction
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        hideStatusBar()
        loadHomeFragment(HomeFragment())

        auth= Firebase.auth

        binding.layoutHome.setOnClickListener {
           val background=background()
            resetBackgrounds()
            binding.layoutHome.background=background

            val homeFragment=HomeFragment()
            loadFragment(homeFragment)
        }
        binding.layoutProfile.setOnClickListener {
            val background=background()
            resetBackgrounds()
            binding.layoutProfile.background=background
            val profileFragment=ProfileFragment()
            loadFragment(profileFragment)
        }
        binding.layoutLiked.setOnClickListener {
            val background=background()
            resetBackgrounds()
            binding.layoutLiked.background=background
            val likedFragment=LikedFragment()
            loadFragment(likedFragment)
        }
        binding.layoutSetting.setOnClickListener {
            val background=background()
            resetBackgrounds()
            binding.layoutSetting.background=background
            val settingFragment=SettingFragment()
            loadFragment(settingFragment)
        }


    }

    private fun loadFragment(fragment:Fragment) {

        fragmentManager=supportFragmentManager
        fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer,fragment)
        fragmentTransaction.commit()
    }

    private fun loadHomeFragment(fragment: Fragment){
        val background=background()
        binding.layoutHome.background=background
        fragmentManager=supportFragmentManager
        fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer,fragment)
        fragmentTransaction.commit()
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
    fun background(): Drawable? {
        val selcetedbackground=ContextCompat.getDrawable(this,R.drawable.selected_background)
        return selcetedbackground
    }
    fun resetBackgrounds() {
        val unselectedBackground=ContextCompat.getDrawable(this,R.drawable.unselected_background)
        binding.layoutHome.background = unselectedBackground
        binding.layoutProfile.background = unselectedBackground
        binding.layoutLiked.background = unselectedBackground
        binding.layoutSetting.background = unselectedBackground
    }

    override fun onBackPressed() {

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment is HomeFragment) {
            super.onBackPressed()
            finishAffinity()
        }
        else{
            super.onBackPressed()
        }
    }
}