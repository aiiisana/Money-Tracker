package com.fpis.money.views.fragments.admin

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fpis.money.R
import com.fpis.money.databinding.FragmentAdminUsersBinding
import com.fpis.money.models.User
import com.fpis.money.utils.showCustomToast
import com.fpis.money.utils.ToastType
import com.fpis.money.views.fragments.admin.adapter.UsersAdapter
import com.fpis.money.views.fragments.admin.viewmodels.AdminUsersViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AdminUsersFragment : Fragment() {
    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminUsersViewModel by viewModels()
    private lateinit var adapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
        viewModel.loadUsers()
    }

    private fun setupRecyclerView() {
        adapter = UsersAdapter(
            onEditClick = { user -> showEditUserDialog(user) },
            onDeleteClick = { user -> confirmDeleteUser(user) },
            onRoleChange = { user, isAdmin -> viewModel.updateUserRole(user.id, isAdmin) }
        )

        binding.usersRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AdminUsersFragment.adapter
        }
    }

    private fun setupListeners() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchUsers(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchUsers(newText)
                return true
            }
        })

        binding.addUserButton.setOnClickListener {
            showAddUserDialog()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.users.collect { users ->
                adapter.submitList(users.toList()) {
                    binding.usersRecycler.post {
                        if (binding.usersRecycler.computeVerticalScrollOffset() == 0) {
                            binding.usersRecycler.scrollToPosition(0)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.toastMessage.collect { message ->
                showCustomToast(requireContext(), message, ToastType.SUCCESS)
            }
        }
    }

    private fun showEditUserDialog(user: User) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_user, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit User")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newUsername = dialogView.findViewById<EditText>(R.id.etUsername)?.text.toString()
                val newEmail = dialogView.findViewById<EditText>(R.id.etEmail)?.text.toString()
                val newRole = if (dialogView.findViewById<SwitchCompat>(R.id.switchAdmin)?.isChecked == true) "admin" else "user"

                if (newUsername.isBlank() || newEmail.isBlank()) {
                    showCustomToast(requireContext(), "Fields cannot be empty", ToastType.ERROR)
                    return@setPositiveButton
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    showCustomToast(requireContext(), "Invalid email format", ToastType.ERROR)
                    return@setPositiveButton
                }

                val updatedUser = user.copy(
                    username = newUsername,
                    email = newEmail,
                    role = newRole
                )

                viewModel.updateUser(updatedUser)
                showCustomToast(requireContext(), "User updated successfully", ToastType.SUCCESS)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialogView.findViewById<EditText>(R.id.etUsername)?.setText(user.username)
        dialogView.findViewById<EditText>(R.id.etEmail)?.setText(user.email)
        dialogView.findViewById<SwitchCompat>(R.id.switchAdmin)?.isChecked =
            user.role.equals("admin", ignoreCase = true)
        dialog.show()
    }

    private fun confirmDeleteUser(user: User) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete ${user.email}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteUser(user.id)
                showCustomToast(requireContext(), "User deleted", ToastType.SUCCESS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAddUserDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_user, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.etEmail)
        val usernameEditText = dialogView.findViewById<EditText>(R.id.etUsername)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.etPassword)
        val adminSwitch = dialogView.findViewById<SwitchCompat>(R.id.switchAdmin)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New User")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val email = emailEditText.text.toString().trim()
                val username = usernameEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                val role = if (adminSwitch.isChecked) "admin" else "user"

                if (validateUserInput(email, username, password)) {
                    val newUser = User(
                        email = email,
                        username = username,
                        role = role
                    )
                    viewModel.addUser(newUser, password)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun validateUserInput(email: String, username: String, password: String): Boolean {
        return when {
            email.isBlank() -> {
                showCustomToast(requireContext(), "Email cannot be empty", ToastType.ERROR)
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showCustomToast(requireContext(), "Invalid email format", ToastType.ERROR)
                false
            }
            username.isBlank() -> {
                showCustomToast(requireContext(), "Username cannot be empty", ToastType.ERROR)
                false
            }
            password.isBlank() -> {
                showCustomToast(requireContext(), "Password cannot be empty", ToastType.ERROR)
                false
            }
            password.length < 6 -> {
                showCustomToast(requireContext(), "Password must be at least 6 characters", ToastType.ERROR)
                false
            }
            else -> true
        }
    }
}