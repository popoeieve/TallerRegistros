package com.example.pruebafirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa Firebase Firestore
        db = FirebaseFirestore.getInstance();

        leerBaseDeDatosYAlmacenarEnLista();

    }

    private void leerBaseDeDatosYAlmacenarEnLista() {
        CocheManager listaCoches = CocheManager.getInstance();
        CollectionReference cochesCollectionRef = db.collection("TallerCarlos");

        cochesCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaCoches.getListaCoches().clear(); // Limpiar la lista antes de llenarla con los nuevos datos

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            Coche coche = documentSnapshot.toObject(Coche.class);
                            if (coche != null) {
                                listaCoches.getListaCoches().add(coche);
                            }
                        }
                    }

                    Log.d(TAG, "Se han leído y almacenado en listaCoches " + listaCoches.getListaCoches().size() + " coches.");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al obtener documentos", e);
                });
    }

    private void vaciarBaseDeDatosYAgregarDesdeLista() {
        CocheManager listaCoches = CocheManager.getInstance();
        CollectionReference cochesCollectionRef = db.collection("TallerCarlos");

        // Borrar todos los documentos existentes
        cochesCollectionRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        db.collection("TallerCarlos").document(document.getId()).delete();
                    }

                    // Agregar los coches desde la lista a la base de datos
                    for (Coche coche : listaCoches.getListaCoches()) {
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

    private void mostrarListaCoches() {
        CocheManager cocheManager = CocheManager.getInstance();
        LinearLayout listaCochesLayout = findViewById(R.id.listaCochesLayout);  // Asumiendo que tienes un LinearLayout con el ID listaCochesLayout en tu XML

        // Limpiar cualquier vista previa
        listaCochesLayout.removeAllViews();

        for (Coche coche : cocheManager.getListaCoches()) {
            CocheFragmento cocheFragmento = new CocheFragmento();

            // Configurar los argumentos (nombre y edad) para el fragmento
            Bundle args = new Bundle();
            args.putString("nombre", coche.getNombre());
            args.putString("matricula", String.valueOf(coche.getMatricula()));
            cocheFragmento.setArguments(args);

            // Agregar el fragmento al contenedor (listaCochesLayout)
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(listaCochesLayout.getId(), cocheFragmento);
            transaction.commit();
        }
    }

    public void MostrarListaCochesTaller(View vista) {
        CocheManager listaCoches = CocheManager.getInstance();
        //Log.d(TAG, "Agregando coche...:");
        //Coche nuevoCoche = new Coche("Bibidaiabidubu", 22);
        //Log.d(TAG, "Agregando coche manualmente: "+nuevoCoche.getNombre());
        //listaCoches.agregarCoche(nuevoCoche);
        mostrarListaCoches();
        vaciarBaseDeDatosYAgregarDesdeLista();
    }

    public void AgregarNuevoCoche(View vista) {
        Intent intent = new Intent(this, InformeCocheActivity.class);

        startActivity(intent);
    }
}