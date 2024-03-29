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

    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val chatPartnerId: String

        if (message.firstUser == FirebaseAuth.getInstance().uid) {
            chatPartnerId = message.secondUser
        } else {
            chatPartnerId = message.firstUser
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.message_textview_latest_message.text = message.text
                viewHolder.itemView.username_textview_latest_message.text =
                    chatPartnerUser?.username
                if (chatPartnerUser?.selectedPhotoUrl!!.isEmpty()) {
                    Picasso.get().load(R.drawable.ic_android_green_24dp)

                        .into(viewHolder.itemView.latest_messages_PP)
                } else {
                    Picasso
                        .get().load(chatPartnerUser?.selectedPhotoUrl)
                        .placeholder(R.drawable.ic_android_green_24dp)
                        .into(viewHolder.itemView.latest_messages_PP)
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_messages_row
    }

}