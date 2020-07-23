package developer.company.chatapplication.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import developer.company.chatapplication.MessageActivity
import developer.company.chatapplication.Model.Chat
import developer.company.chatapplication.Model.User
import developer.company.chatapplication.R
import developer.company.chatapplication.StartActivity

class UserAdapter(
    private var context: Context?,
    private var users: ArrayList<User>?,
    private var isChat: Boolean
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var theLastMessage: String? = null

    public class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgProfile = itemView.findViewById<CircleImageView>(R.id.img_userItem_profile)
        var txtUsername = itemView.findViewById<TextView>(R.id.txt_userItem_username)
        var txt_msg = itemView.findViewById<TextView>(R.id.last_msg)
        var imgOn = itemView.findViewById<CircleImageView>(R.id.img_userItem_on)
        var imgOff = itemView.findViewById<CircleImageView>(R.id.img_userItem_off)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users!!.count()
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users!![position]

        holder.txtUsername.text = user.username
        if (user.image.equals("default")) {
            holder.imgProfile.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(context!!).load(user.image).into(holder.imgProfile)
        }

        if (isChat){
            lastMessage(user.id!!,holder.txt_msg)
        }else{
            holder.txt_msg.visibility = View.GONE
        }

        if (isChat) {
            if (user.status.equals("online")) {
                holder.imgOn.visibility = View.VISIBLE
                holder.imgOff.visibility = View.GONE
            } else {
                holder.imgOn.visibility = View.GONE
                holder.imgOff.visibility = View.VISIBLE
            }
        } else {
            holder.imgOn.visibility = View.GONE
            holder.imgOff.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            var intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("userId", user.id)
            context!!.startActivity(intent)
        }
    }

    private fun lastMessage(userId: String, last_msg: TextView) {
        theLastMessage = "default"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(firebaseUser!!.uid) && chat.sender!!.equals(userId)
                        || chat.receiver.equals(userId) && chat.sender!!.equals(firebaseUser.uid)) {
                        theLastMessage = chat.message
                    }
                }

                when (theLastMessage) {
                    "default" -> {
                        last_msg.text = "No message "
                    }
                    else -> {
                        last_msg.text = theLastMessage
                    }
                }

                theLastMessage = "default"
            }

        })
    }
}