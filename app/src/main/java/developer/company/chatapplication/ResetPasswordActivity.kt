package developer.company.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.rengwuxian.materialedittext.MaterialEditText
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var edtEmail: MaterialEditText
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        ////check
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.reset_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Reset Password"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        edtEmail = findViewById(R.id.edt_reset_email)

        firebaseAuth = FirebaseAuth.getInstance()

        btn_reset_reset.setOnClickListener {
            val email = edtEmail.text.toString()

            if (email == "") {
                Toast.makeText(this, "all fields are required !", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task: Task<Void> ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Please check your email ... ", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this, LoginActivity::class.java))
                        } else {
                            val error = task.exception!!.message
                            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
