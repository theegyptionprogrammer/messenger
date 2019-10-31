package com.example.ourmessenger.RegisterLogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmessenger.R
import com.example.ourmessenger.messgages.LatestMessagesActivity
import com.example.ourmessenger.modules.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    companion object{
        val tag = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register2.setOnClickListener {
            register()
        }

        select_profile_photo_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent , 0)
        }

        already_have_an_account.setOnClickListener {
            Log.d(tag, "go to login activity")
            val intent = Intent(this , LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private var  selectedPhotoUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d(tag, "photo has selected")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver , selectedPhotoUri)
            select_profile_photo_imagerView.setImageBitmap(bitmap)
            select_profile_photo_button.alpha = 0f
        }
    }

    private fun register(){
        val mail = email2.text.toString()
        val password = password2.text.toString()

        if ( mail.isEmpty() || password.isEmpty()){
            Toast.makeText(this , "please fill all blanks" , Toast.LENGTH_SHORT).show()
        }

        Log.d(tag, "mail is: $mail")
        Log.d(tag, "password is: $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail , password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                Log.d(tag, "successfully created user with uid: ${it.result!!.user!!.uid}")
                saveSelectedPhotoToDatabase()
            }
            .addOnFailureListener{
                Log.d(tag, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }


    }

    private fun saveSelectedPhotoToDatabase(){

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(tag, "image uploaded successfully: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d(tag, "file location: $it")
                    saveUserToDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(tag, "failed to upload image to the storage: ${it.message}")
            }
    }

    private fun saveUserToDatabase(selectedPhotoUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user =
            User(uid, username2.text.toString(), selectedPhotoUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(tag, "successfully added the user to the database")
                Toast.makeText(this , "successfully added the user to the database" , Toast.LENGTH_SHORT).show()
                val intent = Intent(this , LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d(tag, "failed to save the user to the database: ${it.message}")
            }
    }


}


