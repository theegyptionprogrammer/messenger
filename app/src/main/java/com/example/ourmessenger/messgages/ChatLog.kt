package com.example.ourmessenger.messgages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmessenger.R
import com.example.ourmessenger.modules.Message
import com.example.ourmessenger.modules.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
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
                        adapter.add(UserFirstRow(message.text, currentUser))
                    } else {
                        adapter.add(UserSecondRow(message.text, secondUser!!))
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildRemoved(p0: DataSnapshot) {}

        })
    }

    private fun sendMessage() {
        val user = intent.getParcelableExtra<User>(NewMessage.USER_KEY)
        val text = chatMessage.text.toString()
        val messageSU = user?.uid
        val messageFU = FirebaseAuth.getInstance().uid
        val refMessageFU =
            FirebaseDatabase.getInstance().getReference("/user_messages/$messageFU/$messageSU")
                .push()
        val refMessageSU =
            FirebaseDatabase.getInstance().getReference("/user_messages/$messageSU/$messageFU")
                .push()
        val message = Message(
            text,
            messageFU!!,
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

    }
}

class UserFirstRow(private var text: String, private var user: User) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.user_1_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        text = viewHolder.itemView.firstMessage.text as String
        Picasso.get().load(user.selectedPhotoUrl).into(viewHolder.itemView.firstIamge)
    }

}

class UserSecondRow(private var text: String, private var user: User) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.user_2_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        text = viewHolder.itemView.secondMessage.text as String
        Picasso.get().load(user.selectedPhotoUrl).into(viewHolder.itemView.secondImage)
    }

}