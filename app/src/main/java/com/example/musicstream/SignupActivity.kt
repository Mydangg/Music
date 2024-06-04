package com.example.musicstream

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicstream.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createAccountBtn.setOnClickListener {
            val email = binding.emailEdittext.text.toString()
            val password = binding.passwordEdittext.text.toString()
            val confirmPassword = binding.confirmPasswordEdittext.text.toString()

            if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(),email)){
                binding.emailEdittext.setError("Email không hợp lệ")
                return@setOnClickListener
            }

            if (password.length < 6){
                binding.passwordEdittext.setError("Độ dài nên lớn hơn 6 kí tự")
                return@setOnClickListener
            }
            if (!password.equals(confirmPassword)){
                binding.confirmPasswordEdittext.setError("Mật khẩu không phù hợp")
                return@setOnClickListener
            }

            createAccountWithFireBase(email, password)

        }

        binding.gotoLoginBtn.setOnClickListener {
            finish()
        }

    }
    fun saveUserId(userId: String){
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        with(sharedPref.edit()){
            putString("userId", userId)
            apply()
        }
    }
    fun createAccountWithFireBase(email: String, password: String){
        setInProgress(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                setInProgress(false)
                val userId = it.user?.uid
                if (userId != null){
                    saveUserId(userId)
                }
                Toast.makeText(applicationContext,"Tạo tài khoản thành công", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                setInProgress(false)
                Toast.makeText(applicationContext,"Tạo tài khoản thất bại", Toast.LENGTH_SHORT).show()
            }
    }

    fun setInProgress(inProgress : Boolean){
        if(inProgress){
            binding.createAccountBtn.visibility = View.GONE
            binding.progressBar.visibility =View.VISIBLE
        }else{
            binding.createAccountBtn.visibility = View.VISIBLE
            binding.progressBar.visibility =View.GONE
        }
    }

}