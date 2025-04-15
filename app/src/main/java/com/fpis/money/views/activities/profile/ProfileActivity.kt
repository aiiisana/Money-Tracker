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
import android.text.TextUtils
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.*


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
            showEditProfileDialog()
        }

        changePasswordOption.setOnClickListener {
            showChangePasswordDialog()
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

    private fun showEditProfileDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        dialog.setContentView(view)
        dialog.show()

        val etUsername = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etUsername)
        val etEmail = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Pre-fill current data
        val user = auth.currentUser
        etUsername.setText(user?.displayName ?: "")
        etEmail.setText(user?.email ?: "")

        btnSave.setOnClickListener {
            val newUsername = etUsername.text.toString().trim()
            val newEmail = etEmail.text.toString().trim()

            if (TextUtils.isEmpty(newUsername)) {
                showCustomToast(this, "Please enter username", ToastType.ERROR)
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(newEmail)) {
                showCustomToast(this, "Please enter email", ToastType.ERROR)
                return@setOnClickListener
            }

            updateProfile(newUsername, newEmail, dialog)
        }
    }

    private fun updateProfile(username: String, email: String, dialog: BottomSheetDialog) {
        val user = auth.currentUser ?: return

        // Check if email changed
        if (user.email != email) {
            // Email changed - need to reauthenticate
            showReauthenticateDialog(email, username, dialog)
        } else {
            // Only username changed
            val profileUpdates = userProfileChangeRequest {
                displayName = username
            }

            user.updateProfile(profileUpdates)
                .addOnSuccessListener {
                    usernameText.text = username
                    showCustomToast(this, "Profile updated", ToastType.SUCCESS)
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    showCustomToast(this, "Failed to update profile: ${e.message}", ToastType.ERROR)
                }
        }
    }

    private fun showReauthenticateDialog(newEmail: String, username: String, profileDialog: BottomSheetDialog) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_reauthenticate, null)
        dialog.setContentView(view)
        dialog.show()

        val etPassword = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPassword)
        val btnAuthenticate = view.findViewById<Button>(R.id.btnAuthenticate)

        btnAuthenticate.setOnClickListener {
            val password = etPassword.text.toString().trim()

            if (TextUtils.isEmpty(password)) {
                showCustomToast(this, "Please enter your password", ToastType.ERROR)
                return@setOnClickListener
            }

            val user = auth.currentUser ?: return@setOnClickListener
            val credential = EmailAuthProvider.getCredential(user.email!!, password)

            user.reauthenticate(credential)
                .addOnSuccessListener {
                    // Now update email and profile
                    updateEmailAndProfile(newEmail, username, profileDialog, dialog)
                }
                .addOnFailureListener { e ->
                    when (e) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            showCustomToast(this, "Invalid password", ToastType.ERROR)
                        }
                        else -> {
                            showCustomToast(this, "Authentication failed: ${e.message}", ToastType.ERROR)
                        }
                    }
                }
        }
    }

    private fun updateEmailAndProfile(newEmail: String, username: String, profileDialog: BottomSheetDialog, authDialog: BottomSheetDialog) {
        val user = auth.currentUser ?: return

        user.updateEmail(newEmail)
            .addOnSuccessListener {
                val profileUpdates = userProfileChangeRequest {
                    displayName = username
                }

                user.updateProfile(profileUpdates)
                    .addOnSuccessListener {
                        emailText.text = newEmail
                        usernameText.text = username
                        showCustomToast(this, "Profile updated", ToastType.SUCCESS)
                        profileDialog.dismiss()
                        authDialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        showCustomToast(this, "Failed to update profile: ${e.message}", ToastType.ERROR)
                    }
            }
            .addOnFailureListener { e ->
                showCustomToast(this, "Failed to update email: ${e.message}", ToastType.ERROR)
            }
    }

    private fun showChangePasswordDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_change_password, null)
        dialog.setContentView(view)
        dialog.show()

        val etCurrentPassword = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etCurrentPassword)
        val etNewPassword = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNewPassword)
        val etConfirmPassword = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etConfirmPassword)
        val btnChangePassword = view.findViewById<Button>(R.id.btnChangePassword)

        btnChangePassword.setOnClickListener {
            val currentPassword = etCurrentPassword.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (TextUtils.isEmpty(currentPassword)) {
                showCustomToast(this, "Please enter current password", ToastType.ERROR)
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(newPassword)) {
                showCustomToast(this, "Please enter new password", ToastType.ERROR)
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                showCustomToast(this, "Password must be at least 6 characters", ToastType.ERROR)
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                showCustomToast(this, "Passwords don't match", ToastType.ERROR)
                return@setOnClickListener
            }

            changePassword(currentPassword, newPassword, dialog)
        }
    }

    private fun changePassword(currentPassword: String, newPassword: String, dialog: BottomSheetDialog) {
        val user = auth.currentUser ?: return
        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        showCustomToast(this, "Password changed successfully", ToastType.SUCCESS)
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        showCustomToast(this, "Failed to change password: ${e.message}", ToastType.ERROR)
                    }
            }
            .addOnFailureListener { e ->
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        showCustomToast(this, "Invalid current password", ToastType.ERROR)
                    }
                    else -> {
                        showCustomToast(this, "Authentication failed: ${e.message}", ToastType.ERROR)
                    }
                }
            }
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