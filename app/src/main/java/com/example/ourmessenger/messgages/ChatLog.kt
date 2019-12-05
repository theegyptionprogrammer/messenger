package com.example.ourmessenger.messgages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmessenger.R
import com.example.ourmessenger.Views.UserFirstRow
import com.example.ourmessenger.Views.UserSecondRow
import com.example.ourmessenger.modules.Message
import com.example.ourmessenger.modules.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLog : AppCompatActivity() {

    companion object {
        const val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()
    private var secondUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        FirebaseApp.initializeApp(this)

        secondUser = intent.getParcelableExtra(NewMessage.USER_KEY)

        supportActionBar?.title = secondUser?.username

        list_messages.adapter = adapter

        listenMessage()

        send.setOnClickListener { sendMessage() }
    }


    private fun listenMessage() {
        val messageSU = secondUser?.uid
        val messageFU = FirebaseAuth.getInstance().uid
        val ref =
            FirebaseDatabase.getInstance().getReference("/user_messages/$messageFU/$messageSU")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Message::class.java)
                if (message != null) {
                    Log.d(TAG, message.text)

                    if (message.firstUser == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(UserFirstRow(message, currentUser))
                    } else {
                        adapter.add(UserSecondRow(message, secondUser!!))
                    }
                }
                list_messages.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    private fun sendMessage() {
        val user = intent.getParcelableExtra<User>(NewMessage.USER_KEY)
        val text = chatMessage.text.toString()
        val messageSU = user?.uid
        val messageFU = FirebaseAuth.getInstance().currentUser!!.uid

        val refMessageFU =
            FirebaseDatabase.getInstance().getReference("/user_messages/$messageFU/$messageSU")
                .push()
        val refMessageSU =
            FirebaseDatabase.getInstance().getReference("/user_messages/$messageSU/$messageFU")
                .push()

        val message = Message(
            text,
            messageFU,
            messageSU!!,
            refMessageFU.key!!,
            System.currentTimeMillis() / 1000
        )

        refMessageFU.setValue(message)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${refMessageFU.key}")
                chatMessage.text.clear()
                list_messages.scrollToPosition(adapter.itemCount - 1)
            }

        refMessageSU.setValue(message)

        val refLatestMessageFU =
            FirebaseDatabase.getInstance().getReference("/latest_message/$messageFU/$messageSU")
                .push()
        refLatestMessageFU.setValue(message)

        val refLatestMessageSU =
            FirebaseDatabase.getInstance().getReference("/latest_message/$messageSU/$messageFU")
                .push()
        refLatestMessageSU.setValue(message)
    }
}
