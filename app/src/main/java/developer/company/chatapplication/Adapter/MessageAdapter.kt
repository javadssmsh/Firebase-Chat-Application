package developer.company.chatapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import developer.company.chatapplication.Model.Chat
import developer.company.chatapplication.R


class MessageAdapter(
    val context: Context?,
    private val chats: ArrayList<Chat>,
    private val imageURL: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    companion object {
        val MSG_TYPE_LEFT: Int = 0
        val MSG_TYPE_RIGHT: Int = 1
    }

    lateinit var fUser: FirebaseUser

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgProfile = itemView.findViewById<CircleImageView>(R.id.image_profile)
        var txtShowText = itemView.findViewById<TextView>(R.id.txt_showMessage)
        var txtSeen = itemView.findViewById<TextView>(R.id.txt_seen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == MSG_TYPE_RIGHT) {
            val view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false)
            MessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false)
            MessageViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chats.count()
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val chat = chats[position]
        holder.txtShowText.text = chat.message
        if (imageURL == "default") {
            holder.imgProfile.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(context!!).load(imageURL).into(holder.imgProfile)
        }

        if (position == chats.size - 1) {
            if (chat.isseen!!){
                holder.txtSeen.text = "Seen"
            }else{
                holder.txtSeen.text = "Delivered"
            }
        }else{
            holder.txtSeen.visibility = View.GONE
        }
    }

    override fun getItemViewType(position: Int): Int {
        fUser = FirebaseAuth.getInstance().currentUser!!
        return if (chats[position].sender.equals(fUser.uid)) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }
}