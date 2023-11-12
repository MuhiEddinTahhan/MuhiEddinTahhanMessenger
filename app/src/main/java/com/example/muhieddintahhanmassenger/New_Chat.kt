package com.example.muhieddintahhanmassenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_chat.*
import kotlinx.android.synthetic.main.user_contact.view.*

class New_Chat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)

        supportActionBar?.title = "users names"


        populateUsers()

    }
    companion object{
        val USER_KEY="USER_KEY"
   }

    private fun populateUsers() {
        val ref= FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter= GroupAdapter<ViewHolder>()

                snapshot.children.forEach{
                    Log.d("new_massage",it.toString())
                    val user=it.getValue(User::class.java)
                    if (user!=null){
                        adapter.add(usersChat(user))
                    }

                }
                adapter.setOnItemClickListener { item, view ->
                    var userItem=item as usersChat
                    val intent=Intent(view.context, chatting::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }
                newChat.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}
class usersChat(val user:User): Item<ViewHolder>(){ //used a 3rd party dependency for recyclerView called Groupie

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textView.text=user.username
        //Picasso.get().load(user.profileImage).into(viewHolder.itemView.imageView_new)
        //suppose to load a profile pic but it is crashing every time

    }

    override fun getLayout(): Int {
        return R.layout.user_contact
    }
}
