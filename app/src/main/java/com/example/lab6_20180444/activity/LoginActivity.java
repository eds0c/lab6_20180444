package com.example.lab6_20180444.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6_20180444.R;
import com.example.lab6_20180444.databinding.ActivityLoginBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    // Google Sign-In
    private GoogleSignInClient googleSignInClient;

    // Facebook Login
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        configureGoogleSignIn();

        configureFacebookLogin();

        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, ingresa tu correo", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email);
        });

        binding.registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AddUserActivity.class);
            startActivity(intent);
        });

        binding.googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1001);
        });

        binding.facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Inicio de sesión cancelado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Error en inicio de sesión: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_app_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void configureFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
    }

    private void loginUser(String email) {
        firestore.collection("user")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String name = document.getString("name");
                            Toast.makeText(LoginActivity.this, "Bienvenido " + name, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            return;
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Correo no registrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        checkIfUserExists(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Error al autenticar con Facebook", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfUserExists(FirebaseUser user) {
        String email = user.getEmail();
        firestore.collection("user")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        // Usuario nuevo, registrar
                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("email", email);
                        newUser.put("name", user.getDisplayName());
                        newUser.put("photo", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");

                        firestore.collection("user").document(user.getUid())
                                .set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                });
                    } else {
                        // Usuario existente
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google Sign-In
        if (requestCode == 1001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Error en inicio de sesión con Google", Toast.LENGTH_SHORT).show();
            }
        }

        // Facebook Login
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        checkIfUserExists(user);
                    } else {
                        Toast.makeText(this, "Error en la autenticación con Google", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
