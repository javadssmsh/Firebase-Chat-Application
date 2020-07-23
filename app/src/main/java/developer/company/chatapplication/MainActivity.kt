package developer.company.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import developer.company.chatapplication.Adapter.ViewPagerAdapter
import developer.company.chatapplication.Fragments.ChatsFragment
import developer.company.chatapplication.Fragments.ProfileFragment
import developer.company.chatapplication.Fragments.UsersFragment
import developer.company.chatapplication.Model.User

class MainActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        val imgProfile = findViewById<CircleImageView>(R.id.img_main_profile)
        var txtUsername = findViewById<TextView>(R.id.txt_main_username)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                user = dataSnapShot.getValue(User::class.java)
                txtUsername.text = user!!.username
                if (user!!.image.equals("default")){
                    imgProfile.setImageResource(R.mipmap.ic_launcher)
                }else{
                    Glide.with(applicationContext).load(user!!.image).into(imgProfile)
                }
            }

        })

        val tabLayout = findViewById<TabLayout>(R.id.tablayout)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)

        var adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ChatsFragment(),"Chats")
        adapter.addFragment(UsersFragment(),"Users")
        adapter.addFragment(ProfileFragment(),"Profile")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout ->{
                FirebaseAuth.getInstance().signOut()
                //startActivity(Intent(this,StartActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                startActivity(Intent(this,StartActivity::class.java))
                //finish()
                return true
            }
        }
        return false
    }

    private fun status(status:String){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)

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
        status("offline")
    }
}
