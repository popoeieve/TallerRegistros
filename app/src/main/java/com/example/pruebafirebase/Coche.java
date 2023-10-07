package com.example.pruebafirebase;

public class Coche {
    private String nombre,matricula;

    public Coche() {
        // Constructor vacío necesario para Firebase
    }

    public Coche(String matricula, String nombre) {
        this.nombre = nombre;
        this.matricula = matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}