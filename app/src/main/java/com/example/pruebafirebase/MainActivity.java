package com.example.pruebafirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SobrenombrePruebaFireba";
    private FirebaseFirestore db;

    public List<Coche> listaCoches=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseApp.initializeApp(this);
        // Inicializa Firebase Firestore
        db = FirebaseFirestore.getInstance();


        leerBaseDeDatosYAlmacenarEnLista();

        vaciarBaseDeDatosYAgregarDesdeLista();
    }

    private void leerBaseDeDatosYAlmacenarEnLista() {
        CollectionReference cochesCollectionRef = db.collection("SobrenombrePruebaFirebase");

        cochesCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaCoches.clear(); // Limpiar la lista antes de llenarla con los nuevos datos

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            Coche coche = documentSnapshot.toObject(Coche.class);
                            if (coche != null) {
                                listaCoches.add(coche);
                            }
                        }
                    }

                    Log.d(TAG, "Se han leído y almacenado en listaCoches " + listaCoches.size() + " coches.");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al obtener documentos", e);
                });
    }

    private void vaciarBaseDeDatosYAgregarDesdeLista() {
        CollectionReference cochesCollectionRef = db.collection("SobrenombrePruebaFirebase");

        // Borrar todos los documentos existentes
        cochesCollectionRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        db.collection("SobrenombrePruebaFirebase").document(document.getId()).delete();
                    }

                    // Agregar los coches desde la lista a la base de datos
                    for (Coche coche : listaCoches) {
                        cochesCollectionRef.add(coche)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d(TAG, "Documento agregado con ID: " + documentReference.getId());
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Error al agregar documento", e);
                                });
                    }

                    Log.d(TAG, "Base de datos vaciada y lista de coches agregada a la base de datos.");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al obtener documentos", e);
                });
    }
}