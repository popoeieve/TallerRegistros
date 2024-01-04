package com.example.pruebafirebase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CocheFragmento#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CocheFragmento extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CocheFragmento() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CocheFragmento.
     */
    // TODO: Rename and change types and number of parameters
    public static CocheFragmento newInstance(String param1, String param2) {
        CocheFragmento fragment = new CocheFragmento();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coche_fragmento, container, false);

        // Obtener los argumentos (nombre y edad)
        Bundle args = getArguments();
        if (args != null) {
            String nombre = args.getString("nombre");
            String matricula = args.getString("matricula");

            // Asignar los valores a los TextView
            TextView nombreTextView = view.findViewById(R.id.nombre);
            TextView matriculaTextView = view.findViewById(R.id.matricula);

            nombreTextView.setText(nombre);
            matriculaTextView.setText(matricula);

            // Agregar OnLongClickListener al fragmento
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mostrarConfirmacionEliminar(matricula);
                    return true;
                }
            });

            // Agregar OnClickListener al fragmento
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crear un Intent para abrir la nueva Activity (InformeCocheActivity)
                    Intent intent = new Intent(getActivity(), InformeCocheActivity.class);

                    // Pasar la matricula como un extra en el Intent
                    intent.putExtra("matricula", matricula);

                    // Iniciar la nueva Activity
                    startActivity(intent);
                }
            });
        }
        return view;
    }

    private void mostrarConfirmacionEliminar(final String matricula) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("¿Deseas eliminar este coche?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Eliminar el coche de la base de datos
                        eliminarCoche(matricula);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarCoche(String matricula) {
        // Inicializar Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cochesCollectionRef = db.collection("Taller");//Es taller y el nombre del primo Camelcase hay 4 referencias

        // Borrar el coche si ya existe con esa matrícula
        cochesCollectionRef.whereEqualTo("matricula", matricula)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            db.collection("Taller").document(documentSnapshot.getId()).delete();//Es taller y el nombre del primo Camelcase
                            // Reinicia la actividad
                            Intent intent = getActivity().getIntent();
                            getActivity().finish();
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al buscar coche por matrícula: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}