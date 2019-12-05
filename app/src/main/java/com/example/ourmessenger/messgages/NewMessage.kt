package com.example.ourmessenger.messgages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmessenger.R
import com.example.ourmessenger.modules.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user.view.*


class NewMessage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "select user"
        fetchUsers()
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }


    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                dataSnapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    Log.d("NewMessage", it.toString())
                    adapter.add(UserItem(user!!))
                }
                list_users.adapter = adapter

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLog::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }
}

class UserItem(val user : User) : Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.username
        Picasso.get().load(user.selectedPhotoUrl).into(viewHolder.itemView.imageview_new_message)
    }

    override fun getLayout(): Int {
        return R.layout.user
    }

}
