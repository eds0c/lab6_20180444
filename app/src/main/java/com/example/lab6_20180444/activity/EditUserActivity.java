package com.example.lab6_20180444.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lab6_20180444.R;
import com.example.lab6_20180444.databinding.ActivityEditUserBinding;
import com.example.lab6_20180444.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditUserActivity extends AppCompatActivity {

    private ActivityEditUserBinding binding;
    private FirebaseFirestore firestore;
    private String userId;
    private String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        userId = getIntent().getStringExtra("userId");

        loadUserData();

        binding.saveChangesButton.setOnClickListener(v -> updateUser());

        binding.uploadPhotoButton.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad de subir foto aún no implementada", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        binding.nameEditText.setText(user.getName());
                        binding.lastNameEditText.setText(user.getLastName());
                        binding.locationEditText.setText(user.getLocation());

                        photoUrl = user.getPhoto();
                        Glide.with(this)
                                .load(photoUrl)
                                .placeholder(R.drawable.ic_logo)
                                .into(binding.photoImageView);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show());
    }

    private void updateUser() {
        String name = binding.nameEditText.getText().toString().trim();
        String lastName = binding.lastNameEditText.getText().toString().trim();
        String location = binding.locationEditText.getText().toString().trim();

        if (name.isEmpty() || lastName.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("users")
                .document(userId)
                .update("name", name,
                        "lastName", lastName,
                        "location", location,
                        "photo", photoUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show());
    }
}