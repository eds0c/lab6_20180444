package com.example.lab6_20180444.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab6_20180444.adapter.UserAdapter;
import com.example.lab6_20180444.databinding.ActivityUserListBinding;
import com.example.lab6_20180444.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private ActivityUserListBinding binding; // ViewBinding para el layout
    private UserAdapter userAdapter;
    private FirebaseFirestore firestore;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.usersRecyclerView.setAdapter(userAdapter);

        binding.addUserFab.setOnClickListener(v -> {
            Intent intent = new Intent(UserListActivity.this, AddUserActivity.class);
            startActivity(intent);
        });

        loadUsers();
    }

    private void loadUsers() {
        String filter = getIntent().getStringExtra("filter");

        firestore.collection("users")
                .whereEqualTo("enabled", "enabled".equals(filter)) // Filtrar por estado
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            user.setId(document.getId());
                            userList.add(user);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public void onEditUser(User user) {
        Intent intent = new Intent(this, EditUserActivity.class);
        intent.putExtra("userId", user.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteUser(User user) {
        firestore.collection("users")
                .document(user.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    loadUsers(); // Recargar la lista de usuarios
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onViewDetails(User user) {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra("userId", user.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers(); // Actualizar la lista de usuarios al regresar a esta actividad
    }
}
