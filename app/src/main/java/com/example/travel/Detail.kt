package com.example.travel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.travel.databinding.ActivityDetailBinding
import com.example.travel.dataclass.ItemDataModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Detail : AppCompatActivity() {
    private lateinit var binding:ActivityDetailBinding
    private lateinit var dataModel: ItemDataModel
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        dataModel=intent.getSerializableExtra("dataModel") as ItemDataModel
        addDataToDetailActivity()
        binding.backArrow.setOnClickListener {
            goToMainActivity()
        }
        liledOrNot()
        binding.likeImageView.setOnClickListener {

            if(dataModel.liked==true){
                binding.likeImageView.setImageResource(R.drawable.fav)
                updateLikedDislike(false)
            }
            if (dataModel.liked==false){
                binding.likeImageView.setImageResource(R.drawable.liked)
                updateLikedDislike(true)
            }
        }
    }

    private fun goToMainActivity() {
        val intent= Intent(this@Detail,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private  fun addDataToDetailActivity(){
        binding.titleTv.text=dataModel.title
        binding.location.text=dataModel.location
        binding.score.text= dataModel.score.toString()
        Glide.with(applicationContext)
            .load(dataModel.pic.toUri())
            .into(binding.imageViewDetail)

        binding.decription.text=dataModel.description
        binding.priceTv.text="$"+dataModel.price.toString()
        binding.bed.text=dataModel.bed.toString()+"Bed"

        if(dataModel.guide==false){
            binding.guideLayout.visibility=View.GONE
        }
        if(dataModel.wifi==false){
            binding.wifiLayout.visibility=View.GONE
        }

    }

    private fun liledOrNot(){

        if(dataModel.liked==true){
            binding.likeImageView.setImageResource(R.drawable.liked)
        }
    }

    fun updateLikedDislike(b: Boolean) {
        databaseReference=FirebaseDatabase.getInstance().getReference("Travel").child("Popular").child(dataModel.title)
        databaseReference.child("liked").setValue(b)
    }
}