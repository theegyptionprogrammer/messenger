package com.example.ourmessenger.Views

import com.example.ourmessenger.R
import com.example.ourmessenger.modules.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_2_row.view.*


class UserSecondRow(private var text: String, private var user: User) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.user_2_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        text = viewHolder.itemView.secondMessage.text as String
        Picasso.get().load(user.selectedPhotoUrl).into(viewHolder.itemView.secondImage)
    }

}