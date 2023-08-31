package com.example.travel.fragment

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.travel.LoginActivity
import com.example.travel.R
import com.example.travel.databinding.EditNameBinding
import com.example.travel.databinding.FragmentProfileBinding
import com.example.travel.databinding.RatingLayoutBinding
import com.example.travel.dataclass.ProfileDataModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var ratingBinding: RatingLayoutBinding
    private lateinit var editNameBinding: EditNameBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var name: String
    private lateinit var profilePic: String
    private lateinit var inputMethodManager:InputMethodManager
    private var IMAGE_REQUEST=1
    private lateinit var IMAGE_URI:String
    private lateinit var progressDialog:ProgressDialog
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        progressDialog= ProgressDialog(requireContext())

        fetchProfileFromRD()
        binding.shareApp.setOnClickListener {
            shareApp()
        }
        binding.rateUs.setOnClickListener {
            rateUs()
        }

        binding.logout.setOnClickListener {
            Firebase.auth.signOut()

            gotoSignUp()
        }

        binding.editName.setOnClickListener {
            editName()
        }
        binding.profileImageUpdate.setOnClickListener {
            updateProfileImage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun updateProfileImage() {
        val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(intent,"Pick Image...."),IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        progressDialog.setTitle("Uploading.....")
        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK){
            Glide
                .with(requireContext())
                .load(data?.data)
                .into(binding.profileImage)
            IMAGE_URI= data?.data.toString()
        }


//        databaseReference=FirebaseDatabase.getInstance().getReference("Travel").child("Users").child(auth.currentUser?.uid.toString())
//        databaseReference.child("profile")
//            .setValue(IMAGE_URI)
    }

    private fun editName() {

        editNameBinding = EditNameBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(editNameBinding.root)
        bottomSheetDialog.behavior
        bottomSheetDialog.show()

        // Get the layout params of the bottom sheet content view
        val layoutParams = editNameBinding.root.layoutParams
        layoutParams.height = 800 // Set your desired height here
        // Apply the updated layout params
        editNameBinding.root.layoutParams = layoutParams
        // Adjust window soft input mode


        // Focus on the EditText field
        editNameBinding.name.requestFocus()
        // Open the keyboard
        inputMethodManager=requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editNameBinding.name,InputMethodManager.SHOW_IMPLICIT)





        editNameBinding.save.setOnClickListener {
            val enteredName = editNameBinding.name.text.toString()
            databaseReference = FirebaseDatabase.getInstance().getReference("Travel").child("Users")
            databaseReference.child(auth.currentUser?.uid.toString()).child("name")
                .setValue(enteredName)
            bottomSheetDialog.dismiss()
            fetchProfileFromRD()
        }

        editNameBinding.cancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


    }

    private fun gotoSignUp() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun rateUs() {
        ratingBinding = RatingLayoutBinding.inflate(layoutInflater)
//        val view = layoutInflater.inflate(R.layout.rating_layout, null)

        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(ratingBinding.root)
        bottomSheetDialog.show()

        ratingBinding.submitButton.setOnClickListener {
            val rating = ratingBinding.ratingBar.rating
            Toast.makeText(requireContext(), "You rated:$rating", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }
    }


    fun shareApp() {
        val appLink = "www.testlink.com"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Please Downlaod this App")
        intent.putExtra(Intent.EXTRA_TEXT, "Download Link:$appLink")
        startActivity(Intent.createChooser(intent, "Share....."))
    }

    fun fetchProfileFromRD() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Travel").child("Users")
            .child(auth.currentUser?.uid.toString())
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    name = snapshot.child("name").value.toString()
                    profilePic = snapshot.child("profile").value.toString()
                    binding.textViewName.text = name
                    if (profilePic != "") {
                        Glide
                            .with(requireContext())
                            .load(profilePic.toUri())
                            .into(binding.profileImage)

                    } else {
                        binding.profileImage.setImageResource(R.drawable.profile)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


}