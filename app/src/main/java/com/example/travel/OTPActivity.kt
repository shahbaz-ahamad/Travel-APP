package com.example.travel

import android.content.Context
import android.content.Intent
import android.graphics.Insets.add
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.travel.databinding.ActivityOtpactivityBinding
import com.example.travel.dataclass.ProfileDataModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpactivityBinding
    private lateinit var OTP: String
    lateinit var phoneNumber: String
    private lateinit var typedOTP: String
    private lateinit var auth: FirebaseAuth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var databaseReference: DatabaseReference
    private var countDownTimer: CountDownTimer? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        OTP = intent.getStringExtra("OTP").toString()
        phoneNumber = intent.getStringExtra("phoneNumber").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!

        binding.verify.setOnClickListener {
            binding.apply {
                progressBar.visibility = View.VISIBLE

                typedOTP =
                    et1.text.toString() + et2.text.toString() + et3.text.toString() + et4.text.toString() + et5.text.toString() + et6.text.toString()

                if (typedOTP.isNotEmpty() && typedOTP.length == 6) {

                    val credential: PhoneAuthCredential =
                        PhoneAuthProvider.getCredential(OTP, typedOTP)
                    signInWithPhoneAuthCredential(credential)

                } else {
                    Toast.makeText(this@OTPActivity, "Enter valid OTP", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }
        }

        startCountDown()
        resendVisibility()
        binding.resendOTP.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            resendVisibility()
            ResendOTP()
        }

        addTextChangeListner()

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //progressDialog.dismiss()
                    binding.progressBar.visibility = View.GONE
                    goToDashboard()
                    val user = task.result?.user
                    addProfileToRD()

                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        //progressDialog.dismiss()
                        binding.progressBar.visibility = View.GONE
                    }
                    // Update UI
                }
            }
    }

    private fun goToDashboard() {
        val intent = Intent(this@OTPActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addTextChangeListner() {
        binding.et1.addTextChangedListener(EditTextWatcher(binding.et1))
        binding.et2.addTextChangedListener(EditTextWatcher(binding.et2))
        binding.et3.addTextChangedListener(EditTextWatcher(binding.et3))
        binding.et4.addTextChangedListener(EditTextWatcher(binding.et4))
        binding.et5.addTextChangedListener(EditTextWatcher(binding.et5))
        binding.et6.addTextChangedListener(EditTextWatcher(binding.et6))
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            when (view.id) {
                binding.et1.id -> if (text.length == 1) binding.et2.requestFocus()
                binding.et2.id -> if (text.length == 1) binding.et3.requestFocus() else if (text.isEmpty()) binding.et1.requestFocus()
                binding.et3.id -> if (text.length == 1) binding.et4.requestFocus() else if (text.isEmpty()) binding.et2.requestFocus()
                binding.et4.id -> if (text.length == 1) binding.et5.requestFocus() else if (text.isEmpty()) binding.et3.requestFocus()
                binding.et5.id -> if (text.length == 1) binding.et6.requestFocus() else if (text.isEmpty()) binding.et4.requestFocus()
                binding.et6.id -> if (text.isEmpty()) binding.et5.requestFocus()
            }
        }

    }

    private fun ResendOTP() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
//            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
//            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                binding.progressBar.visibility=View.VISIBLE
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                binding.progressBar.visibility=View.VISIBLE
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                binding.progressBar.visibility=View.VISIBLE
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
//            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            binding.progressBar.visibility = View.GONE
            OTP = verificationId
            resendToken = token
        }
    }

    private fun resendVisibility() {

        binding.apply {
            et1.setText("")
            et2.setText("")
            et3.setText("")
            et4.setText("")
            et5.setText("")
            et6.setText("")
            resendOTP.visibility = View.GONE
            resendOTP.isEnabled = false

            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                resendOTP.visibility = View.VISIBLE
                resendOTP.isEnabled = true
            }, 10000)

        }
    }

    private fun startCountDown(){
        countDownTimer =object : CountDownTimer(10000,1000){
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.timer.text=secondsRemaining.toString()
            }

            override fun onFinish() {
                binding.timer.visibility=View.GONE
            }

        }

        countDownTimer?.start()
    }

    fun addProfileToRD(){
        databaseReference= FirebaseDatabase.getInstance().getReference("Travel").child("Users")
        databaseReference.child(auth?.currentUser?.uid.toString()).setValue(ProfileDataModel(auth.currentUser?.phoneNumber,""))
    }
}





