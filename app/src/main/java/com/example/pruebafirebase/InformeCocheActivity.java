package com.example.pruebafirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

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

        //Para que si se hace focus fuera de un editText se cierre teclado
        View mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            focusedView.clearFocus();
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

    public void volverAMainActivity(View view) {
        finish();  // Cierra esta Activity y vuelve a la MainActivity si es la anterior en la pila
    }

    public void guardarInformacion(View view) {
        // Obtener la matrícula y nombre de los EditText
        EditText editTextMatricula = findViewById(R.id.editTextMatricula);
        EditText editTextNombre = findViewById(R.id.editTextNombre);
        String matricula = editTextMatricula.getText().toString().trim();
        String nombre = editTextNombre.getText().toString().trim();

        // Verificar que haya datos en la matrícula y nombre
        if (matricula.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inicializar Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cochesCollectionRef = db.collection("TallerCarlos");

        // Borrar el coche si ya existe con esa matrícula
        cochesCollectionRef.whereEqualTo("matricula", matricula)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            db.collection("TallerCarlos").document(documentSnapshot.getId()).delete();
                        }
                    }

                    // Crear un nuevo coche con los datos
                    Coche coche = new Coche(matricula, nombre);

                    // Agregar el nuevo coche a la base de datos
                    cochesCollectionRef.add(coche)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(this, "Coche agregado con éxito", Toast.LENGTH_SHORT).show();

                                // Cerrar esta actividad y volver a MainActivity
                                finish();

                                // Iniciar la MainActivity
                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al agregar coche: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al buscar coche por matrícula: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}