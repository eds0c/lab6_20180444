package com.example.lab6_20180444.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab6_20180444.R;
import com.example.lab6_20180444.databinding.ItemUserBinding;
import com.example.lab6_20180444.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEditUser(User user);
        void onDeleteUser(User user);
        void onViewDetails(User user);
    }

    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        public UserViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(User user) {

            binding.userNameTextView.setText(user.getLastName() + ", " + user.getName());
            binding.userDetailsTextView.setText("Correo: " + user.getEmail() +
                    "\nDNI: " + user.getDni() +
                    "\nCódigo: " + user.getPucpCode());

            Glide.with(binding.getRoot().getContext())
                    .load(user.getPhoto())
                    .placeholder(R.drawable.ic_users_enabled)
                    .into(binding.userImageView);

            binding.editUserButton.setOnClickListener(v -> listener.onEditUser(user));
            binding.deleteUserButton.setOnClickListener(v -> listener.onDeleteUser(user));
            binding.getRoot().setOnClickListener(v -> listener.onViewDetails(user));
        }
    }
}

