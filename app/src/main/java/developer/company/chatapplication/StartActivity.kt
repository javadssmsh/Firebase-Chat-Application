package developer.company.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    var firebaseUser:FirebaseUser?=null

    override fun onStart() {
        super.onStart()

        firebaseUser=FirebaseAuth.getInstance().currentUser
        if (firebaseUser!=null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        btn_start_login.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        btn_start_register.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }
}
