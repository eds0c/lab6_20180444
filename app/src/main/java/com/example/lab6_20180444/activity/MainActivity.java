package com.example.lab6_20180444.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6_20180444.R;
import com.example.lab6_20180444.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_users_enabled) {
                // Navegar a Usuarios Habilitados
                Intent intent = new Intent(this, UserListActivity.class);
                intent.putExtra("filter", "enabled");
                startActivity(intent);
                return true;
            } else if (id == R.id.action_users_banned) {
                // Navegar a Usuarios Baneados
                Intent intent = new Intent(this, UserListActivity.class);
                intent.putExtra("filter", "banned");
                startActivity(intent);
                return true;
            } else if (id == R.id.action_logout) {
                // Cerrar sesión
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });

        binding.bottomNavigationView.setSelectedItemId(R.id.action_users_enabled);
    }
}
