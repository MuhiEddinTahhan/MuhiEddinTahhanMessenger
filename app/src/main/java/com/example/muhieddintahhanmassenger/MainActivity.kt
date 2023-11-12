package com.example.muhieddintahhanmassenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerClient()

        gotAccount.setOnClickListener {
            //going to login activity
            val intent=Intent(this, LoginActivity::class.java) //go to log in activity
            startActivity(intent)
        }
        photoSelect.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK)   // function to pick profile photo
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }

     private var photoUri: Uri?=null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode== Activity.RESULT_OK && data!=null){
            Log.d("MainActivity","photo")

            val photoUri=data.data
            val bitmap=MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            photCircle.setImageBitmap(bitmap)//I did some research and got an outside dependencies
            photCircle.alpha=0f // put the image in front of the button

        //val bitmapDrawable=BitmapDrawable(bitmap)
            //photoSelect.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun registerClient() {
        registerButton.setOnClickListener {
            val emailView=email.text.toString()  //get an email and password as string
            val passwordCheck=password.text.toString()

            if (emailView.isEmpty()||passwordCheck.isEmpty()) {
                Toast.makeText(this, "please enter e/pw", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } //to avoid crashing when not filling
            // the email and password

            // firebase authentication
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailView, passwordCheck)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    firebaseUploadPhoto()
                    saveUserInDatabase(null.toString())

                }
                .addOnFailureListener(){
                    //massage show user he failed
                    Toast.makeText(this, "email is invalid", Toast.LENGTH_SHORT).show()
                    Log.d("Main","failed")
                }

        }
    }
    private fun firebaseUploadPhoto(){
        if (photoUri==null)return
        val name= UUID.randomUUID().toString()  //giving a photo a unique random ID
        val ref= FirebaseStorage.getInstance().getReference("/image/$name")// upload to images
        ref.putFile(photoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    it.toString() //download the photo when success
                    saveUserInDatabase(it.toString())//
                }
            }

    }
    private fun saveUserInDatabase(profileImage: String){
        val uid=FirebaseAuth.getInstance().uid?: ""
        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid") //sending info to database
        val user=User(uid, username.text.toString(),profileImage)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("MainActivity","profile is saved in database") //check work in debug
                val intent=Intent(this, NewMasseges::class.java)// go to new massages
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)// to clear out the activity stack
                startActivity(intent)
            }
    }
}
@Parcelize
class User(val uid: String,val username: String, val profileImage:String): Parcelable{
    constructor() : this("","","")
}//class describes the user attributes in database