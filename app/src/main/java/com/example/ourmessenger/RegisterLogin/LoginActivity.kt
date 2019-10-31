package com.example.ourmessenger.RegisterLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmessenger.R
import com.example.ourmessenger.messgages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){

    companion object{
        val tag = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        register1.setOnClickListener {
            Log.d(tag, "go to Register Activity")
            val intent = Intent(this , RegisterActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener { loginProcess() }
    }

    fun loginProcess(){
        val username = username1.text.toString()
        val password = password1.text.toString()

        if (username.isEmpty() || password.isEmpty()){
            Toast.makeText(this , "please fill all the blanks" , Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(username , password)
            .addOnCompleteListener {
                if (!it.isSuccessful)return@addOnCompleteListener
                Log.d(tag, "client logined successfuly")
                val intent = Intent(this , LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this , "failed to login, please check your login and password", Toast.LENGTH_LONG).show()
                Log.d(tag, "failed to login")
            }
    }
}