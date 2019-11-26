package com.example.ourmessenger.messgages

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmessenger.R
import com.example.ourmessenger.RegisterLogin.RegisterActivity
import com.example.ourmessenger.Views.LatestMessagesRow
import com.example.ourmessenger.modules.Message
import com.example.ourmessenger.modules.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        list_latest_messages.adapter = adapter
        fetchLatestMessages()
        fetchCurrentUser()
        checkIfUserLogined()
    }

    val adapter = GroupAdapter<ViewHolder>()
    val latestMessageMap = HashMap<String?, Message>()


    private fun refreshRecyclerView() {
        adapter.clear()
        latestMessageMap.values.forEach {
            adapter.add(LatestMessagesRow(it))
        }
    }
    private fun fetchLatestMessages() {
        val latestMessageUid = FirebaseAuth.getInstance().uid
        val refLatestMessages =
            FirebaseDatabase.getInstance().getReference("/latest_message$latestMessageUid")
        refLatestMessages.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Message::class.java) ?: return
                val user = p0.getValue(User::class.java) ?: return
                latestMessageMap[p0.key] = message
                refreshRecyclerView()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Message::class.java) ?: return
                val user = p0.getValue(User::class.java) ?: return
                latestMessageMap[p0.key] = message
                refreshRecyclerView()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
            override fun onCancelled(p0: DatabaseError) {}

        })
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun checkIfUserLogined() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this , RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.newmessage -> {
                val intent = Intent(this , NewMessage::class.java)
                startActivity(intent)
            }
            R.id.signout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this , RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_latest_messages, menu)
        return super.onCreateOptionsMenu(menu)
    }
}


