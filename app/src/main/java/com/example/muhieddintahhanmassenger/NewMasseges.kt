package com.example.muhieddintahhanmassenger

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_masseges.*

class NewMasseges : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_masseges)

        recyclerViewLatestMassages.adapter=adapter


        showRowsOfLatestMassages()


        userLogged()//check fo a user
        }

    private fun showRowsOfLatestMassages(){// here I tried to implement a page that fetures the
        val fromId=FirebaseAuth.getInstance().uid// the latest massages but I the program would crash every time for no reason
        val ref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage=snapshot.getValue(chatting.ChatMessage::class.java)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    class LatestMassageRow: Item<ViewHolder>(){
        override fun bind(viewHolder: ViewHolder, position: Int) {
            TODO("Not yet implemented")
        }

        override fun getLayout(): Int {
            return R.layout.activity_new_masseges
        }

    }
    val adapter=GroupAdapter<ViewHolder>()
/*    val adapter=GroupAdapter<ViewHolder>()
    private fun setUpDummyData(){

         val adapter=GroupAdapter<ViewHolder>()
        recyclerViewLatestMassages.adapter=adapter
        adapter.add(LatestMassageRow())
        adapter.add(LatestMassageRow())
        adapter.add(LatestMassageRow())


        //this function here was suppose to show how the latest messages notification would look like
        but it crashes every time I tried to use it

    }*/

    private fun userLogged(){
        val uid=FirebaseAuth.getInstance().uid
        if (uid==null){
            val intent= Intent(this, MainActivity::class.java)//if there  is no user logged in
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)// to clear out the activity stack
            startActivity(intent) //go back to the main activity
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.signOut->{
                FirebaseAuth.getInstance().signOut()//sign out firebase
                val intent= Intent(this, MainActivity::class.java)//go back to main
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)// to clear out the activity stack
                startActivity(intent) //go back to the main activity
            }
            R.id.newMassage->{
                val intent= Intent(this, New_Chat::class.java)//go to new chat
                startActivity(intent)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.newmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    }

