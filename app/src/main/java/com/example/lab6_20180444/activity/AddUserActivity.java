package com.example.lab6_20180444.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6_20180444.databinding.ActivityAddUserBinding;
import com.example.lab6_20180444.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.UUID;

public class AddUserActivity extends AppCompatActivity {

    private ActivityAddUserBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    private Uri photoUri; // URI de la foto seleccionada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Configuración del botón de guardar usuario
        binding.saveUserButton.setOnClickListener(v -> saveUser());

        // Configuración del botón para subir foto
        binding.uploadPhotoButton.setOnClickListener(v -> selectPhoto());
    }

    private void saveUser() {
        String name = binding.nameEditText.getText().toString().trim();
        String lastName = binding.lastNameEditText.getText().toString().trim();
        String dni = binding.dniEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String pucpCode = binding.pucpCodeEditText.getText().toString().trim();
        String location = binding.locationEditText.getText().toString().trim();
        boolean isEnabled = binding.enabledSwitch.isChecked();

        if (name.isEmpty() || lastName.isEmpty() || dni.isEmpty() || email.isEmpty() || pucpCode.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Subir foto si se seleccionó una
        if (photoUri != null) {
            uploadPhotoAndSaveUser(name, lastName, dni, email, pucpCode, location, isEnabled);
        } else {
            saveUserToFirestore(name, lastName, dni, email, pucpCode, location, isEnabled, "");
        }
    }

    private void uploadPhotoAndSaveUser(String name, String lastName, String dni, String email, String pucpCode, String location, boolean isEnabled) {
        String photoId = UUID.randomUUID().toString(); // Generar un ID único para la foto
        storage.getReference("user_photos/" + photoId)
                .putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> storage.getReference("user_photos/" + photoId)
                        .getDownloadUrl()
                        .addOnSuccessListener(uri -> saveUserToFirestore(name, lastName, dni, email, pucpCode, location, isEnabled, uri.toString()))
                        .addOnFailureListener(e -> Toast.makeText(this, "Error al obtener URL de la foto", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(this, "Error al subir la foto", Toast.LENGTH_SHORT).show());
    }

    private void saveUserToFirestore(String name, String lastName, String dni, String email, String pucpCode, String location, boolean isEnabled, String photoUrl) {
        User user = new User();
        user.setName(name);
        user.setLastName(lastName);
        user.setDni(dni);
        user.setEmail(email);
        user.setPucpCode(pucpCode);
        user.setLocation(location);
        user.setEnabled(isEnabled);
        user.setPhoto(photoUrl);

        firestore.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Usuario guardado exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar el usuario", Toast.LENGTH_SHORT).show();
                });
    }

    // Selector de foto de la galería
    private final ActivityResultLauncher<Intent> photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    photoUri = result.getData().getData(); // Obtener URI de la foto seleccionada
                    binding.photoImageView.setImageURI(photoUri); // Mostrar la foto seleccionada
                } else {
                    Toast.makeText(this, "No se seleccionó ninguna foto", Toast.LENGTH_SHORT).show();
                }
            });

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        photoPickerLauncher.launch(intent);
    }
}