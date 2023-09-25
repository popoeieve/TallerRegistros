package com.example.pruebafirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SobrenombrePruebaFireba";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseApp.initializeApp(this);
        // Inicializa Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Escribir un documento
        escribirDocumento();

        // Leer un documento
        leerDocumento();
    }

    private void escribirDocumento() {
        // Obtén una referencia a la colección
        CollectionReference collectionRef = db.collection("SobrenombrePruebaFirebase");

        // Crea un objeto con los datos
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", "Ejemplo");
        data.put("edad", 25);

        // Añade el documento a la colección
        collectionRef.add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Documento agregado con ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al agregar documento", e);
                });
    }

    private void leerDocumento() {
        // Obtén una referencia al documento que deseas leer
        DocumentReference docRef = db.collection("usuarios").document("ID_DEL_DOCUMENTO_A_LEER");

        // Leer el documento
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Datos del documento: " + documentSnapshot.getData());
                    } else {
                        Log.d(TAG, "El documento no existe");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al obtener documento", e);
                });
    }
}