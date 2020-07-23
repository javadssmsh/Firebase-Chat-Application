package developer.company.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.rengwuxian.materialedittext.MaterialEditText
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar = findViewById<Toolbar>(R.id.login_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val edtEmail = findViewById<MaterialEditText>(R.id.edit_login_email)
        val edtPassword = findViewById<MaterialEditText>(R.id.edit_login_password)

        txt_login_forgotPassword.setOnClickListener {
            startActivity(Intent(this,ResetPasswordActivity::class.java))
        }

        mAuth = FirebaseAuth.getInstance()
        btn_login_login.setOnClickListener {
            val txt_email = edtEmail.text.toString()
            val txt_password = edtPassword.text.toString()
            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(this,"All field are required",Toast.LENGTH_LONG).show()
            }else{
                mAuth!!.signInWithEmailAndPassword(txt_email,txt_password)
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful){
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this,"Login failed",Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}
