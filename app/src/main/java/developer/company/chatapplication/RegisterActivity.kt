package developer.company.chatapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rengwuxian.materialedittext.MaterialEditText
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar = findViewById<Toolbar>(R.id.register_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()

        val edtUsername = findViewById<MaterialEditText>(R.id.edit_register_user)
        val edtEmail = findViewById<MaterialEditText>(R.id.edit_register_email)
        val edtPassword = findViewById<MaterialEditText>(R.id.edit_register_password)

        btn_register_register.setOnClickListener {
            val txt_username = edtUsername.text.toString()
            val txt_email = edtEmail.text.toString()
            val txt_password = edtPassword.text.toString()
            if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(
                    txt_password
                )
            ) {
                Toast.makeText(this, "All field are required", Toast.LENGTH_LONG).show()
            } else if (edtPassword.length() < 6) {
                Toast.makeText(this, "", Toast.LENGTH_LONG).show()
            } else {
                register(txt_username, txt_email, txt_password)
            }
        }

    }


    @SuppressLint("DefaultLocale")
    private fun register(username: String, email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val firebaseUser = mAuth!!.currentUser
                    val userId = firebaseUser!!.uid

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val userObject = HashMap<String, String>()
                    userObject.put("id", userId)
                    userObject.put("username", username)
                    userObject.put("image", "default")
                    userObject.put("status", "offline")
                    userObject.put("search", username.toLowerCase())

                    reference!!.setValue(userObject).addOnCompleteListener { task: Task<Void> ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "You can not register with email or password ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
