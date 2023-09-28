package com.example.pruebafirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InformeCocheActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String matricula;

    private String TAG="InformeCocheActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informe_coche);

        // Obtener la matrícula del Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("matricula")) {
            matricula = intent.getStringExtra("matricula");
            // Iniciar la búsqueda del coche
            buscarCochePorMatricula();
        } else {
            // Manejar caso en el que no hay matrícula
            // ...
        }
    }

    private void buscarCochePorMatricula() {
        // Inicializa Firebase Firestore
        db = FirebaseFirestore.getInstance();

        CollectionReference cochesCollectionRef = db.collection("TallerCarlos");

        // Buscar el coche por la matrícula
        cochesCollectionRef.whereEqualTo("matricula", matricula)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            Coche coche = documentSnapshot.toObject(Coche.class);
                            if (coche != null) {
                                // Aquí tienes el objeto Coche correspondiente a la matrícula
                                mostrarDatosCoche(coche);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al buscar coche por matrícula", e);
                });
    }

    private void mostrarDatosCoche(Coche coche) {
        // Aquí puedes mostrar los datos del coche en los TextView
        EditText matriculaEditText = findViewById(R.id.editTextMatricula);
        EditText nombreEditText=findViewById(R.id.editTextNombre);

        // Asignar los valores a los TextView
        matriculaEditText.setText(coche.getMatricula());
        nombreEditText.setText(coche.getNombre());

    }
}