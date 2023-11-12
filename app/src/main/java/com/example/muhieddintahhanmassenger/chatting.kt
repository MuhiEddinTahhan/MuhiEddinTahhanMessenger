package com.example.muhieddintahhanmassenger

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chatting.*
import kotlinx.android.synthetic.main.chat_texts.view.*
import kotlinx.android.synthetic.main.chat_texts_sending.view.*

class chatting : AppCompatActivity() {

    companion object {
        val TAG = "chatting"
    }

    val adapter = GroupAdapter<ViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)

        recycleView_chatLog.adapter = adapter

        val user = intent.getParcelableExtra<User>(New_Chat.USER_KEY)
        supportActionBar?.title = user?.username



        send_button.setOnClickListener {
            Log.i(TAG, "Send massage")

            sendMassage()
            showMessages()
        }

    }

    private fun showMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(New_Chat.USER_KEY)
        val toId = user?.uid ?: return

        if (fromId == null) return


        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMassage = snapshot.getValue(ChatMessage::class.java)

                if (chatMassage != null) {

                    if (chatMassage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatItemFrom(chatMassage.text))
                    } //if the massage id is equal to the the id of the sender then put it in the
                    //layout that is corrispond to
                    else {
                        adapter.add(ChatItemSend(chatMassage.text))
                    }// the opposite
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    class ChatMessage(
        val id: String,
        val text: String,
        val fromId: String,
        val toId: String,
        val timestamp: Long
    ) {
        constructor() : this("", "", "", "", -1)
    }// message class for the data base


    private fun sendMassage() {

        val text = enter_text.text.toString()

        //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        //save all messages in the database

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(New_Chat.USER_KEY)
        val toId = user?.uid ?: return

        if (fromId == null) return
        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        // the parameters of the class needed to construct the message branch in the database to save it
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMassage =
            ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

        reference.setValue(chatMassage)
            .addOnSuccessListener {
                Log.i(TAG, "saved massage")
                enter_text.text.clear()
                recycleView_chatLog.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMassage)

        val latestMessageRef= FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMassage)

        val latestMessageToRef= FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMassage)

    }

    class ChatItemFrom(val text: String) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.written_massage_send_from.text = text
        }

        override fun getLayout(): Int {
            return R.layout.chat_texts
        }

    }

    class ChatItemSend(val text: String) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.massage_to.text = text
        }

        override fun getLayout(): Int {
            return R.layout.chat_texts_sending
        }

    }
}