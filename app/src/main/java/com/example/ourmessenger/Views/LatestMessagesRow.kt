package com.example.ourmessenger.Views

import com.example.ourmessenger.R
import com.example.ourmessenger.modules.Message
import com.example.ourmessenger.modules.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestMessagesRow(val message: Message) : Item<ViewHolder>() {

    lateinit var chatPartnerUser: User

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.message_textview_latest_message.text = message.text

        val chatPartnerId: String

        if (message.firstUser == FirebaseAuth.getInstance().uid) {
            chatPartnerId = message.secondUser
        } else {
            chatPartnerId = message.firstUser
        }


        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)!!
                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser.username
                Picasso.get().load(chatPartnerUser.selectedPhotoUrl)
                    .into(viewHolder.itemView.latest_messages_PP)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_messages_row
    }

}