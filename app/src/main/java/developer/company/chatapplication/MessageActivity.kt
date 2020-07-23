package developer.company.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import developer.company.chatapplication.Adapter.MessageAdapter
import developer.company.chatapplication.Model.Chat
import developer.company.chatapplication.Model.User

class MessageActivity : AppCompatActivity() {

    lateinit var imgProfile: CircleImageView
    lateinit var txtUsername: TextView

    lateinit var fUser: FirebaseUser
    lateinit var reference: DatabaseReference

    lateinit var btnSend: ImageButton
    lateinit var edtMessage: EditText

    lateinit var userId: String

    lateinit var adapter: MessageAdapter
    lateinit var chats: ArrayList<Chat>
    lateinit var recyclerView: RecyclerView

    lateinit var seenListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.message_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            //finish()
            startActivity(Intent(this,MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }

        imgProfile = findViewById(R.id.img_message_profile)
        txtUsername = findViewById(R.id.txt_message_username)
        btnSend = findViewById(R.id.btn_message_send)
        edtMessage = findViewById(R.id.edt_message_message)
        recyclerView = findViewById(R.id.rv_message_messageList)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        userId = intent.getStringExtra("userId")

        fUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                val user = datasnapshot.getValue(User::class.java)
                txtUsername.text = user!!.username
                if (user.image.equals("default")) {
                    imgProfile.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(applicationContext).load(user.image).into(imgProfile)
                }
                readMessage(fUser.uid, userId, user.image)
            }
        })

        btnSend.setOnClickListener {
            val msg = edtMessage.text.toString()
            if (msg != "") {
                sendMessage(fUser.uid, userId, msg)
            } else {
                Toast.makeText(this, "You can't send empty message!", Toast.LENGTH_LONG).show()
            }
        }

        seenMessage(userId)

    }

    private fun seenMessage(userId: String){
        reference = FirebaseDatabase.getInstance().getReference("Chats")

        seenListener = reference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children){
                    val chat = snapshot.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(fUser.uid) && chat.sender.equals(userId)){
                        val map = HashMap<String,Any>()
                        map.put("isseen",true)
                        snapshot.ref.updateChildren(map)
                    }
                }
            }

        })
    }

    private fun sendMessage(sender: String, receiver: String?, message: String) {
        val reference = FirebaseDatabase.getInstance().reference

        val hashMap = hashMapOf<String, Any>()
        hashMap.put("sender", sender)
        hashMap.put("receiver", receiver!!)
        hashMap.put("message", message)
        hashMap.put("isseen", false)

        reference.child("Chats").push().setValue(hashMap)

        val chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
            .child(fUser.uid)
            .child(userId)
        chatRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userId)
                }
            }

        })
    }

    private fun readMessage(myId: String, userId: String, imageURL: String?) {
        chats = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chats.clear()
                for (snapShot in dataSnapshot.children) {
                    val chat = snapShot.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(myId) && chat.sender.equals(userId) ||
                        chat.receiver.equals(userId) && chat.sender.equals(myId)
                    ) {
                        chats.add(chat)
                    }

                    adapter = MessageAdapter(this@MessageActivity, chats, imageURL!!)
                    recyclerView.adapter = adapter
                }
            }
        })
    }

    private fun status(status:String){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser!!.uid)

        val map = HashMap<String,Any>()
        map.put("status",status)
        reference!!.updateChildren(map)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        reference.removeEventListener(seenListener)
        status("offline")
    }
}
