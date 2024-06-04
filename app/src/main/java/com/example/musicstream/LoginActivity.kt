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
import com.example.musicstream.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class  LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdittext.text.toString()
            val password = binding.passwordEdittext.text.toString()

            if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(),email)){
                binding.emailEdittext.setError("Email không hợp lệ")
                return@setOnClickListener
            }

            if (password.length < 6){
                binding.passwordEdittext.setError("Độ dài nên lớn hơn 6 kí tự")
                return@setOnClickListener
            }


            loginWithFireBase(email, password)
        }

        binding.gotoSignupBtn.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

    }

    fun saveUserId(userId: String){
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        with(sharedPref.edit()){
            putString("userId", userId)
            apply()
        }
    }
    fun loginWithFireBase(email: String, password: String){
        setInProgress(true)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                setInProgress(false)
                val userId = it.user?.uid
                if (userId != null){
                    saveUserId(userId)
                }
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }.addOnFailureListener {
                setInProgress(false)
                Toast.makeText(applicationContext,"Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() { //ktra neu dang nhap roi thi van giu nguyen neu thoat ra chu khong quay lai trang dang nhap
        super.onResume()
        FirebaseAuth.getInstance().currentUser?.apply {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    fun setInProgress(inProgress : Boolean){
        if(inProgress){
            binding.loginBtn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.loginBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}