package com.fpis.money.views.fragments.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.databinding.ItemUserBinding
import com.fpis.money.models.User

class UsersAdapter(
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit,
    private val onRoleChange: (User, Boolean) -> Unit
) : ListAdapter<User, UsersAdapter.UserViewHolder>(DiffCallback()) {

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.apply {
                adminSwitch.setOnCheckedChangeListener(null)

                userName.text = user.username
                userEmail.text = user.email
                adminSwitch.isChecked = user.isAdmin

                editButton.setOnClickListener { onEditClick(user) }
                deleteButton.setOnClickListener { onDeleteClick(user) }

                adminSwitch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked != user.isAdmin) {
                        onRoleChange(user, isChecked)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}