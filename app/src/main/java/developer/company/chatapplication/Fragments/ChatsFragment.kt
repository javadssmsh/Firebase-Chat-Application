package developer.company.chatapplication.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import developer.company.chatapplication.Adapter.UserAdapter
import developer.company.chatapplication.Model.Chat
import developer.company.chatapplication.Model.ChatList
import developer.company.chatapplication.Model.User

import developer.company.chatapplication.R

class ChatsFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var users: ArrayList<User>

    lateinit var fUser: FirebaseUser
    lateinit var reference: DatabaseReference

    lateinit var userList: ArrayList<ChatList>
    var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        recyclerView = view.findViewById(R.id.rv_chatsFragment_chatList)
        recyclerView.layoutManager = LinearLayoutManager(context)

        fUser = FirebaseAuth.getInstance().currentUser!!
        userList = ArrayList()

        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(fUser.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val chatList = snapshot.getValue(ChatList::class.java)

                    userList.add(chatList!!)
                }

                chatList()
            }

        })

        return view
    }

    private fun chatList() {
        users = ArrayList()

        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()

                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)

                    for (chatList in userList){
                        if (user!!.id.equals(chatList.id)){
                            users.add(user)
                        }
                    }
                }

                userAdapter = UserAdapter(context,users,true)
                recyclerView.adapter = userAdapter
            }

        })
    }
}
