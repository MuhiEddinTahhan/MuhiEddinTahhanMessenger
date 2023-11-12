package com.example.muhieddintahhanmassenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.email
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {
            val emailView=email.text.toString()
            val passwordCheck=password.text.toString()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailView,passwordCheck)
                .addOnCompleteListener {
                    if (!it.isSuccessful)return@addOnCompleteListener
                    val intent= Intent(this, NewMasseges::class.java)// go to new massages
                    intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)// to clear out the activity stack
                    startActivity(intent)
                }
                .addOnFailureListener {
                    //massage show user he failed
                    Toast.makeText(this, "email or pw is incorrect", Toast.LENGTH_SHORT).show()
                    Log.d("Main","failed")
                }
        }
        backReg.setOnClickListener {
            finish()                    //go back to the first activity
        }

    }
}