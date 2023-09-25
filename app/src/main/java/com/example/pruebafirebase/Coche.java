package com.example.pruebafirebase;

public class Coche {
    private String nombre;
    private int edad;

    public Coche() {
        // Constructor vacío necesario para Firebase
    }

    public Coche(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    // Puedes agregar más getters si es necesario
}