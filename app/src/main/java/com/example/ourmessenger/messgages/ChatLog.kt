package com.example.ourmessenger.messgages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmessenger.R
import com.example.ourmessenger.modules.Message
import com.example.ourmessenger.modules.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.user_1_row.view.*
import kotlinx.android.synthetic.main.user_2_row.view.*

class ChatLog : AppCompatActivity() {

    companion object {
        const val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<User>(NewMessage.USER_KEY)

        supportActionBar?.title = user.username

        list_messages.adapter = adapter

        listenMessage()

        send.setOnClickListener { sendMessage() }
    }


    private fun listenMessage() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Message::class.java)

                if (message != null) Log.d(TAG, message.text)

                if (message?.firstUser == FirebaseAuth.getInstance().uid) {
                    adapter.add(UserFirstRow(message!!.text))
                } else {
                    adapter.add(UserSecondRow(message!!.text))
                }
            }

            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildRemoved(p0: DataSnapshot) {}

        })
    }

    private fun sendMessage() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages").push()
        val user = intent.getParcelableExtra<User>(NewMessage.USER_KEY)
        val text = chatMessage.text.toString()
        val firstMessage = user.uid
        val secondMessage = FirebaseAuth.getInstance().uid
        val message =
            Message(text, firstMessage, secondMessage!!, ref.key!!, System.currentTimeMillis())
        ref.setValue(message)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${ref.key}")
            }
    }
}

class UserFirstRow(private var text: String) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.user_1_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        text = viewHolder.itemView.firstMessage.text as String
    }

}

class UserSecondRow(private var text: String) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.user_2_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        text = viewHolder.itemView.secondMessage.text as String
    }

}