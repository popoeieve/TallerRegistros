package com.example.pruebafirebase;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link finanzas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class finanzas extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public finanzas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment finanzas.
     */
    // TODO: Rename and change types and number of parameters
    public static finanzas newInstance(String param1, String param2) {
        finanzas fragment = new finanzas();
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
        View view = inflater.inflate(R.layout.fragment_finanzas, container, false);
        TextView sinVenderEnTaller = view.findViewById(R.id.totalPrecioTaller);

        // Realizar la consulta a la base de datos de Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tallerCarlosCollection = db.collection("TallerCarlos");

        tallerCarlosCollection
                .whereEqualTo("venta", false) // Filtrar coches con venta = false
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double totalPrecioTaller = 0.0;

                        QuerySnapshot queryDocumentSnapshots = task.getResult();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.contains("precioVenta")) {
                                String precioVentaStr = document.getString("precioVenta");
                                if (precioVentaStr != null && !precioVentaStr.isEmpty()) {
                                    // Convertir el precio de String a double
                                    try {
                                        double precioVenta = Double.parseDouble(precioVentaStr);
                                        totalPrecioTaller += precioVenta;
                                    } catch (NumberFormatException e) {
                                        // Manejar errores en la conversión
                                    }
                                }
                            }
                        }

                        // Actualizar el TextView con el resultado
                        sinVenderEnTaller.setText(" " + totalPrecioTaller+" €");
                    } else {
                        // Manejar errores, si es necesario
                    }
                });

        return view;
    }
}