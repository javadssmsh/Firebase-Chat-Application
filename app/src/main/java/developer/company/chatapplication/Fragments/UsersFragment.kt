package developer.company.chatapplication.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import developer.company.chatapplication.Adapter.UserAdapter
import developer.company.chatapplication.Model.User

import developer.company.chatapplication.R


class UsersFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var users: ArrayList<User>
    lateinit var adapter: UserAdapter
    lateinit var edtSearch: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_users, container, false)

        recyclerView = view.findViewById(R.id.rv_usersFragment_usersList)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        users = ArrayList()

        edtSearch = view.findViewById(R.id.edt_userFragment_search)
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            @SuppressLint("DefaultLocale")
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchUser(p0.toString().toLowerCase())
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        readUser()

        return view
    }

    private fun searchUser(s: String) {
        val fUser = FirebaseAuth.getInstance().currentUser
        val query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
            .startAt(s)
            .endAt(s + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (edtSearch.text.toString().isNotEmpty()){
                    users.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if (!user!!.id.equals(fUser!!.uid)) {
                            users.add(user)
                        }
                    }

                    adapter = UserAdapter(context, users, false)
                    recyclerView.adapter = adapter
                }else{
                    readUser()
                }
            }

        })
    }


    private fun readUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (edtSearch.text.toString() == "") {
                    users.clear()

                    for (snapShot in dataSnapshot.children) {
                        val user = snapShot.getValue(User::class.java)
                        if (!user!!.id.equals(firebaseUser!!.uid)) {
                            users.add(user)
                        }
                    }
                    adapter = UserAdapter(context, users, false)
                    recyclerView.adapter = adapter
                }
            }
        })

    }
}
