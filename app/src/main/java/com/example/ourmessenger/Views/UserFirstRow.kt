package com.example.ourmessenger.Views

import com.example.ourmessenger.R
import com.example.ourmessenger.modules.Message
import com.example.ourmessenger.modules.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_1_row.view.*


class UserFirstRow(private var message: Message, private var user: User) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.user_1_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.firstMessage.text = message.text
        Picasso.get().load(user.selectedPhotoUrl).into(viewHolder.itemView.firstIamge)
    }

}
