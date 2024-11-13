package com.example.lab6_20180444.models;

public class User {
    private String id; // ID del documento en Firestore
    private String name;
    private String lastName;
    private String email;
    private String dni;
    private String pucpCode;
    private String photo; // URL de la foto en Firebase Storage
    private String location; // Dirección o coordenadas
    private boolean enabled;

    // Constructor vacío requerido por Firestore
    public User() {}

    // Constructor con todos los campos
    public User(String id, String name, String lastName, String email, String dni, String pucpCode, String photo, String location, boolean enabled) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.dni = dni;
        this.pucpCode = pucpCode;
        this.photo = photo;
        this.location = location;
        this.enabled = enabled;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPucpCode() {
        return pucpCode;
    }

    public void setPucpCode(String pucpCode) {
        this.pucpCode = pucpCode;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

