package developer.company.chatapplication.Fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import developer.company.chatapplication.Model.User

import developer.company.chatapplication.R
import java.lang.Exception


class ProfileFragment : Fragment() {

    lateinit var imgProfile: CircleImageView
    lateinit var txtUsername: TextView

    lateinit var fUser: FirebaseUser
    lateinit var reference: DatabaseReference

    lateinit var storageReference: StorageReference
    private val IMAGE_REQUEST = 1
    lateinit var imageUri: Uri
    var uploadTask: UploadTask? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        imgProfile = view.findViewById(R.id.img_profileFragment_profile)
        txtUsername = view.findViewById(R.id.txt_profileFragment_username)

        fUser = FirebaseAuth.getInstance().currentUser!!
        storageReference  = FirebaseStorage.getInstance().getReference("uploads")

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.uid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)

                txtUsername.text = user!!.username

                if (user.image.equals("default")) {
                    imgProfile.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(context!!).load(user.image).into(imgProfile)
                }
            }

        })

        imgProfile.setOnClickListener {
            openGallery()
        }

        return view
    }

    private fun openGallery() {
        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(galleryIntent,IMAGE_REQUEST)
    }

    private fun getFileExtension(uri:Uri):String?{
        val contentResolver = context!!.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage(){
        val pd = ProgressDialog(context)
        pd.setMessage("uploading ...")
        pd.show()
        if (imageUri!=null){
            val fileReference =
                storageReference.child("${System.currentTimeMillis()} . ${getFileExtension(imageUri)}")

            uploadTask = fileReference.putFile(imageUri)
            uploadTask!!.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                if (!task.isSuccessful){
                    throw task.exception!!
                }
                return@continueWithTask fileReference.downloadUrl
            }.addOnCompleteListener { task: Task<Uri> ->
                if (task.isSuccessful){
                    val downloadUri = task.result
                    val mUri = downloadUri.toString()

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.uid)

                    val map = HashMap<String,Any>()
                    map.put("image",mUri)
                    reference.updateChildren(map)

                    pd.dismiss()
                }else{
                    Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
                    pd.dismiss()
                }
            }.addOnFailureListener { exception: Exception ->
                Toast.makeText(context,exception.message,Toast.LENGTH_LONG).show()
                pd.dismiss()
            }
        }else{
            Toast.makeText(context,"No image selected",Toast.LENGTH_SHORT).show()
            pd.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data !=null){
            imageUri = data.data!!
            if (uploadTask!=null && uploadTask!!.isInProgress){
                Toast.makeText(context,"Upload is progressing ...",Toast.LENGTH_SHORT).show()
            }else{
                uploadImage()
            }
        }
    }
}
