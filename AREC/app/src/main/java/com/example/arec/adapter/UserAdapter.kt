package com.example.arec.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.arec.ChatActivity
import com.example.arec.R
import com.example.arec.databinding.ItemProfileBinding
import com.example.arec.model.User
import androidx.navigation.fragment.findNavController

class UserAdapter(var context:Context,
                  var userList:ArrayList<User>,
                  private val navController: NavController
                  ): RecyclerView.Adapter<UserAdapter.UserViewHolder>()
{
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding : ItemProfileBinding = ItemProfileBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false)
        return UserViewHolder(v)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.username.text = user.name
        Glide.with(context).load(user.profileImage)
            .placeholder(R.drawable.avatar)
            .into(holder.binding.profile)
        holder.itemView.setOnClickListener {


            val bundleUserChat = Bundle()
            bundleUserChat.putString("name", user.name)
            bundleUserChat.putString("image", user.profileImage)
            bundleUserChat.putString("uid", user.uid)
            navController.navigate(R.id.action_usersActivity_to_chatActivity, bundleUserChat)

        }
    }
}