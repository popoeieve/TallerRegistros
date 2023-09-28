package com.example.pruebafirebase;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
}