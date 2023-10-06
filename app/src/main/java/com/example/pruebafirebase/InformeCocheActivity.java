package com.example.pruebafirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InformeCocheActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String matricula;
    private TableLayout tableLayout,postITVtabla,tableLayoutReparaciones;
    private Button btnAddRow,btnAddRowPost,btnAddRowReparaciones;
    private ArrayList<List> listaRepuestosPreITV;
    private ArrayList<List> listaRepuestosPostITV;
    private ArrayList<List> listaReparaciones;
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

        // Inicializa la lista de repuestos (puedes llenarla con tus datos)
        listaRepuestosPreITV = new ArrayList<>();
        // Crear la lista a agregar
        ArrayList<String> listaAgregarPre = new ArrayList<>();
        listaAgregarPre.add("Tubo de escape");
        listaAgregarPre.add("true");
        listaAgregarPre.add("true");
        listaAgregarPre.add("20€");
        // Agregar la lista al ArrayList principal
        listaRepuestosPreITV.add(listaAgregarPre);

        listaRepuestosPostITV = new ArrayList<>();
        ArrayList<String> listaAgregarPost = new ArrayList<>();
        listaAgregarPost.add("Tubo de escape");
        listaAgregarPost.add("true");
        listaAgregarPost.add("true");
        listaAgregarPost.add("20€");
        // Agregar la lista al ArrayList principal
        listaRepuestosPostITV.add(listaAgregarPost);

        listaReparaciones=new ArrayList<>();
        ArrayList<String> listaAgregarReparaciones = new ArrayList<>();
        listaAgregarReparaciones.add("Tubo de escape");
        listaAgregarReparaciones.add("3 horas");
        // Agregar la lista al ArrayList principal
        listaReparaciones.add(listaAgregarReparaciones);

        postITVtabla=findViewById(R.id.tableLayoutPostITV);
        btnAddRowPost=findViewById(R.id.btnAddRowPost);
        btnAddRowPost.setOnClickListener(view -> agregarFilaPostITV());

        tableLayout = findViewById(R.id.tableLayout);
        btnAddRow = findViewById(R.id.btnAddRow);
        btnAddRow.setOnClickListener(view -> agregarFila());

        tableLayoutReparaciones=findViewById(R.id.tableLayoutReparaciones);
        btnAddRowReparaciones=findViewById(R.id.buttonAddRowReparaciones);
        btnAddRowReparaciones.setOnClickListener(view -> agregarFilaReparaciones());


        // Llama a este método antes de llenar la tabla
        crearEncabezadoTabla();

        // Luego llenas la tabla
        llenarTabla();
        llenarTablaPostITV();
        llenarTablaReparaciones();

    }

    private void crearEncabezadoTabla() {
        TableRow encabezado = new TableRow(this);

        TextView textViewArticulo = crearCeldaEncabezado("ARTICULO");
        TextView textViewComprado = crearCeldaEncabezado("COMPRADO");
        TextView textViewPuesto = crearCeldaEncabezado("PUESTO");
        TextView textViewPrecio = crearCeldaEncabezado("PRECIO");

        encabezado.addView(textViewArticulo);
        encabezado.addView(textViewComprado);
        encabezado.addView(textViewPuesto);
        encabezado.addView(textViewPrecio);

        tableLayout.addView(encabezado);

        // Crear encabezado para la segunda tabla (postITVtabla)
        TableRow encabezado2 = new TableRow(this);

        TextView textViewArticulo2 = crearCeldaEncabezado("ARTICULO");
        TextView textViewComprado2 = crearCeldaEncabezado("COMPRADO");
        TextView textViewPuesto2 = crearCeldaEncabezado("PUESTO");
        TextView textViewPrecio2 = crearCeldaEncabezado("PRECIO");

        encabezado2.addView(textViewArticulo2);
        encabezado2.addView(textViewComprado2);
        encabezado2.addView(textViewPuesto2);
        encabezado2.addView(textViewPrecio2);

        postITVtabla.addView(encabezado2);

        // Crear encabezado para la tercera tabla (tableLayoutReparaciones)
        TableRow encabezado3 = new TableRow(this);

        TextView textViewReparaciones = crearCeldaEncabezado("REPARACIONES");
        TextView textViewReparacionesTiempo = crearCeldaEncabezado("TIEMPO");

        encabezado3.addView(textViewReparaciones);
        encabezado3.addView(textViewReparacionesTiempo);

        tableLayoutReparaciones.addView(encabezado3);
    }

    private TextView crearCeldaEncabezado(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setPadding(8, 8, 8, 8);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.LEFT);
        textView.setTextColor(getResources().getColor(android.R.color.black)); // Color de texto negro

        return textView;
    }

    private void llenarTabla() {
        // Eliminar filas existentes a partir de la segunda
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {  // Asegurar que hay más de una fila
            tableLayout.removeViews(1, childCount - 1);  // Eliminar filas a partir de la segunda
        }
        for (List repuesto : listaRepuestosPreITV) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Crear celdas
            EditText textViewArticulo = crearCelda(repuesto.get(0).toString());
            CheckBox checkBoxComprado = crearCheckBox();
            if (repuesto.get(1).toString()=="true")
            {
                checkBoxComprado.setChecked(true);
            }
            CheckBox checkBoxPuesto = crearCheckBox();
            if (repuesto.get(2).toString()=="true")
            {
                checkBoxPuesto.setChecked(true);
            }
            EditText textViewPrecio = crearCelda(repuesto.get(3).toString());

            // Añadir vistas a la fila
            row.addView(textViewArticulo);
            row.addView(checkBoxComprado);
            row.addView(checkBoxPuesto);
            row.addView(textViewPrecio);

            // Agregar la fila a la tabla
            tableLayout.addView(row);
        }
    }

    private void llenarTablaPostITV() {
        // Eliminar filas existentes a partir de la segunda
        int childCount = postITVtabla.getChildCount();
        if (childCount > 1) {  // Asegurar que hay más de una fila
            postITVtabla.removeViews(1, childCount - 1);  // Eliminar filas a partir de la segunda
        }
        for (List repuesto : listaRepuestosPostITV) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Crear celdas
            EditText textViewArticulo = crearCelda(repuesto.get(0).toString());
            CheckBox checkBoxComprado = crearCheckBox();
            if (repuesto.get(1).toString()=="true")
            {
                checkBoxComprado.setChecked(true);
            }
            CheckBox checkBoxPuesto = crearCheckBox();
            if (repuesto.get(2).toString()=="true")
            {
                checkBoxPuesto.setChecked(true);
            }
            EditText textViewPrecio = crearCelda(repuesto.get(3).toString());

            // Añadir vistas a la fila
            row.addView(textViewArticulo);
            row.addView(checkBoxComprado);
            row.addView(checkBoxPuesto);
            row.addView(textViewPrecio);

            // Agregar la fila a la tabla
            postITVtabla.addView(row);
        }
    }

    private void llenarTablaReparaciones() {
        // Eliminar filas existentes a partir de la segunda
        int childCount = tableLayoutReparaciones.getChildCount();
        if (childCount > 1) {  // Asegurar que hay más de una fila
            tableLayoutReparaciones.removeViews(1, childCount - 1);  // Eliminar filas a partir de la segunda
        }
        for (List reparacion : listaReparaciones) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Crear celdas
            EditText editTextReparacion = crearCelda(reparacion.get(0).toString());
            EditText editTextReparacionTiempo = crearCelda(reparacion.get(1).toString());

            // Añadir vistas a la fila
            row.addView(editTextReparacion);
            row.addView(editTextReparacionTiempo);

            // Agregar la fila a la tabla
            tableLayoutReparaciones.addView(row);
        }
    }

    private CheckBox crearCheckBox() {
        CheckBox checkBox = new CheckBox(this);

        // Aplicar propiedades de diseño para centrar horizontal y verticalmente
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        // Aplicar los parámetros de diseño al CheckBox
        checkBox.setLayoutParams(params);

        return checkBox;
    }

    private EditText crearCelda(String texto) {
        EditText textoEnCelda = new EditText(this);
        textoEnCelda.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        textoEnCelda.setMaxLines(5);
        textoEnCelda.setEllipsize(TextUtils.TruncateAt.END);
        textoEnCelda.setGravity(Gravity.CENTER_VERTICAL);

        // Dividir el texto en líneas de máximo 13 caracteres
        String textoFormateado = formatString(texto, 18);
        float textSizeInSp = 14;
        textoEnCelda.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp);
        textoEnCelda.setText(textoFormateado);

        return textoEnCelda;
    }

    private String formatString(String text, int maxLengthPerLine) {
        StringBuilder result = new StringBuilder();
        int index = 0;
        while (index < text.length()) {
            int endIndex = Math.min(index + maxLengthPerLine, text.length());
            result.append(text.substring(index, endIndex)).append("\n");
            index += maxLengthPerLine;
        }
        return result.toString().trim();
    }

    private void agregarFila() {
        rellenarListaPreITV();
        ArrayList<String> listaNueva = new ArrayList<>();
        listaNueva.add("");
        listaNueva.add("");
        listaNueva.add("");
        listaNueva.add("");
        listaRepuestosPreITV.add(listaNueva);
        llenarTabla();
        // Puedes implementar lógica para añadir una nueva fila a la tabla
        // Aquí se debería agregar una nueva fila con los datos que necesites
    }

    private void rellenarListaPreITV() {
        listaRepuestosPreITV.clear();

        int rowCount = tableLayout.getChildCount();

        for (int i = 1; i < rowCount; i++) {
            View view = tableLayout.getChildAt(i);

            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                int columnCount = row.getChildCount();

                // Crear una lista para almacenar los elementos de esta fila
                List<String> fila = new ArrayList<>();

                for (int j = 0; j < columnCount; j++) {
                    View cellView = row.getChildAt(j);

                    if (cellView instanceof EditText) {
                        EditText editText = (EditText) cellView;
                        String texto = editText.getText().toString();

                        fila.add(texto);
                        Log.d(TAG,"se ha añadido a la columna "+j+" el texto "+texto);

                    } else if (cellView instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) cellView;
                        String texto = checkBox.isChecked() ? "true" : "false";
                        fila.add(texto);
                    } else if (cellView instanceof TextView) {
                        TextView textView = (TextView) cellView;
                        String texto = textView.getText().toString();
                        fila.add(texto);
                    }
                }

                // Agregar la lista de elementos de esta fila al ArrayList principal
                listaRepuestosPreITV.add(fila);
            }
        }
    }

    private void agregarFilaPostITV() {
        rellenarListaPostITV();
        ArrayList<String> listaNueva = new ArrayList<>();
        listaNueva.add("");
        listaNueva.add("");
        listaNueva.add("");
        listaNueva.add("");
        listaRepuestosPostITV.add(listaNueva);
        llenarTablaPostITV();
    }

    private void rellenarListaPostITV(){
        listaRepuestosPostITV.clear();

        int rowCount = postITVtabla.getChildCount();

        for (int i = 1; i < rowCount; i++) {
            View view = postITVtabla.getChildAt(i);

            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                int columnCount = row.getChildCount();

                // Crear una lista para almacenar los elementos de esta fila
                List<String> fila = new ArrayList<>();

                for (int j = 0; j < columnCount; j++) {
                    View cellView = row.getChildAt(j);

                    if (cellView instanceof EditText) {
                        EditText editText = (EditText) cellView;
                        String texto = editText.getText().toString();

                        fila.add(texto);
                        Log.d(TAG,"se ha añadido a la columna "+j+" el texto "+texto);

                    } else if (cellView instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) cellView;
                        String texto = checkBox.isChecked() ? "true" : "false";
                        fila.add(texto);
                    } else if (cellView instanceof TextView) {
                        TextView textView = (TextView) cellView;
                        String texto = textView.getText().toString();
                        fila.add(texto);
                    }
                }

                // Agregar la lista de elementos de esta fila al ArrayList principal
                listaRepuestosPostITV.add(fila);
            }
        }
    }

    private void agregarFilaReparaciones() {
        rellenarListaReparaciones();
        ArrayList<String> listaNueva = new ArrayList<>();
        listaNueva.add("");
        listaNueva.add("");
        listaReparaciones.add(listaNueva);
        llenarTablaReparaciones();
        // Puedes implementar lógica para añadir una nueva fila a la tabla
        // Aquí se debería agregar una nueva fila con los datos que necesites
    }

    private void rellenarListaReparaciones(){
        listaReparaciones.clear();

        int rowCount = tableLayoutReparaciones.getChildCount();

        for (int i = 1; i < rowCount; i++) {
            View view = tableLayoutReparaciones.getChildAt(i);

            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                int columnCount = row.getChildCount();

                // Crear una lista para almacenar los elementos de esta fila
                List<String> fila = new ArrayList<>();

                for (int j = 0; j < columnCount; j++) {
                    View cellView = row.getChildAt(j);

                    if (cellView instanceof EditText) {
                        EditText editText = (EditText) cellView;
                        String texto = editText.getText().toString();

                        fila.add(texto);
                        Log.d(TAG,"se ha añadido a la columna "+j+" el texto "+texto);

                    } else if (cellView instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) cellView;
                        String texto = checkBox.isChecked() ? "true" : "false";
                        fila.add(texto);
                    } else if (cellView instanceof TextView) {
                        TextView textView = (TextView) cellView;
                        String texto = textView.getText().toString();
                        fila.add(texto);
                    }
                }

                // Agregar la lista de elementos de esta fila al ArrayList principal
                listaReparaciones.add(fila);
            }
        }
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
                                // Objeto Coche correspondiente a la matrícula
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
        String matriculaNueva = editTextMatricula.getText().toString().trim();
        String nombre = editTextNombre.getText().toString().trim();

        // Verificar que haya datos en la matrícula y nombre
        if (matriculaNueva.isEmpty() || nombre.isEmpty()) {
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
                    Coche coche = new Coche(matriculaNueva, nombre);

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