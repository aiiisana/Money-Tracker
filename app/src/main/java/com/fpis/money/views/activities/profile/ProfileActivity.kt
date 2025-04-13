package com.fpis.money.views.activities.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fpis.money.R
import com.bumptech.glide.Glide
import com.fpis.money.utils.preferences.SharedPreferencesManager
import com.fpis.money.views.activities.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.fpis.money.utils.showCustomToast
import com.fpis.money.utils.ToastType
import de.hdodenhof.circleimageview.CircleImageView
import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts


class ProfileActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferencesManager
    private lateinit var auth: FirebaseAuth

    private lateinit var profileImage: CircleImageView
    private lateinit var btnChangePhoto: ImageView
    private lateinit var usernameText: TextView
    private lateinit var emailText: TextView
    private lateinit var changePasswordOption: LinearLayout
    private lateinit var editProfileOption: LinearLayout
    private lateinit var notificationOption: LinearLayout
    private lateinit var logoutOption: LinearLayout
    private lateinit var logoutButton: Button
    private lateinit var btnBack: ImageView

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let { uri ->
                uploadImageToFirebase(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPrefs = SharedPreferencesManager(this)
        auth = FirebaseAuth.getInstance()
        // storage = FirebaseStorage.getInstance()

        initViews()
        setupUserData()
        setupListeners()
    }

    private fun initViews() {
        profileImage = findViewById(R.id.profile_image)
        btnChangePhoto = findViewById(R.id.btn_change_photo)
        usernameText = findViewById(R.id.usernameText)
        emailText = findViewById(R.id.emailText)
        changePasswordOption = findViewById(R.id.change_password_option)
        editProfileOption = findViewById(R.id.edit_profile_option)
        notificationOption = findViewById(R.id.notification_option)
        logoutOption = findViewById(R.id.logout_option)
        logoutButton = findViewById(R.id.logoutButton)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun setupUserData() {
        val user = auth.currentUser

        user?.let {
            usernameText.text = it.displayName ?: "No username"
            emailText.text = it.email ?: "No email"

            // Load profile image if exists
            it.photoUrl?.let { photoUrl ->
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(profileImage)
            }
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnChangePhoto.setOnClickListener {
            openImagePicker()
        }

        editProfileOption.setOnClickListener {
            showCustomToast(this, "Edit profile feature coming soon", ToastType.INFO)
        }

        changePasswordOption.setOnClickListener {
            sendPasswordResetEmail()
        }

        notificationOption.setOnClickListener {
            showCustomToast(this, "Notification settings coming soon", ToastType.INFO)
        }

        val logoutAction = {
            auth.signOut()
            sharedPrefs.setUserLoggedIn(false)

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        logoutOption.setOnClickListener { logoutAction() }
        logoutButton.setOnClickListener { logoutAction() }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val user = auth.currentUser ?: return

        // Show loading indicator
        showCustomToast(this, "Image selected locally", ToastType.INFO)
        val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
            photoUri = imageUri
        }
        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                // Display locally selected image
                Glide.with(this)
                    .load(imageUri)
                    .into(profileImage)

                showCustomToast(this, "Profile image updated locally", ToastType.SUCCESS)
            }
            .addOnFailureListener {
                showCustomToast(this, "Failed to update profile", ToastType.ERROR)
            }
    }

        // val storageRef = storage.reference
       // val profileImagesRef = storageRef.child("profile_images/${user.uid}/${UUID.randomUUID()}")

//        profileImagesRef.putFile(imageUri)
//            .addOnSuccessListener {
//                profileImagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                    updateUserProfileImage(downloadUri)
//                }
//            }
//            .addOnFailureListener {
//                showCustomToast(this, "Failed to upload image", ToastType.ERROR)
//            }
//    }

    private fun updateUserProfileImage(imageUri: Uri) {
        val user = auth.currentUser ?: return

        val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
            photoUri = imageUri
        }

        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                // Update UI with new image
                Glide.with(this)
                    .load(imageUri)
                    .into(profileImage)

                showCustomToast(this, "Profile image updated", ToastType.SUCCESS)
            }
            .addOnFailureListener {
                showCustomToast(this, "Image upload failed", ToastType.ERROR)
            }
    }

    private fun sendPasswordResetEmail() {
        val email = auth.currentUser?.email ?: return

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                showCustomToast(this, "Profile image updated", ToastType.SUCCESS)
            }
            .addOnFailureListener {
                showCustomToast(this, "Failed to update profile", ToastType.ERROR)
            }
    }
}