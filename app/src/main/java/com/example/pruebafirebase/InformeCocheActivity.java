package com.example.pruebafirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

public class InformeCocheActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String matricula;
    private TableLayout tableLayout,postITVtabla,tableLayoutReparaciones,tablaRevisiones;
    private Button btnAddRow,btnAddRowPost,btnAddRowReparaciones;
    private ArrayList<List<String>> listaRepuestosPreITV,listaRepuestosPostITV,listaReparaciones,listaRevisiones;
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

        // Inicializa las listas y las llena con los datos de la BD
        listaRepuestosPreITV = new ArrayList<>();
        leerListaPreITV(matricula);

        listaRepuestosPostITV = new ArrayList<>();
        leerListaPostITV(matricula);

        listaReparaciones=new ArrayList<>();
        leerListaReparaciones(matricula);

        listaRevisiones=new ArrayList<>();
        leerListaRevisiones(matricula);

        // Inicializa las tablas y los botones para agregar filas
        tableLayout = findViewById(R.id.tableLayout);
        btnAddRow = findViewById(R.id.btnAddRow);
        btnAddRow.setOnClickListener(view -> agregarFila());

        postITVtabla=findViewById(R.id.tableLayoutPostITV);
        btnAddRowPost=findViewById(R.id.btnAddRowPost);
        btnAddRowPost.setOnClickListener(view -> agregarFilaPostITV());

        tableLayoutReparaciones=findViewById(R.id.tableLayoutReparaciones);
        btnAddRowReparaciones=findViewById(R.id.buttonAddRowReparaciones);
        btnAddRowReparaciones.setOnClickListener(view -> agregarFilaReparaciones());

        tablaRevisiones=findViewById(R.id.tablaRevision);


        // Llama a este método antes de llenar la tabla para crear lso encabezados
        crearEncabezadoTabla();

        // Se llenan las tablas dando dos segundos para la conexión
        ejecutarConRetraso();

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

    private void ejecutarConRetraso() {
        // Establece un retraso de 2000 milisegundos (2 segundos)
        int retrasoMilisegundos = 2000;

        // Handler para ejecutar el código después del retraso
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Llama al método que deseas ejecutar después del retraso
            llenarTabla();
            llenarTablaPostITV();
            llenarTablaReparaciones();
            //llenarTablaRevisiones();
        }, retrasoMilisegundos);
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

                Boolean lineaVacia=false;


                for (int j = 0; j < columnCount; j++) {
                    View cellView = row.getChildAt(j);

                    if (cellView instanceof EditText) {
                        EditText editText = (EditText) cellView;
                        String texto = editText.getText().toString();
                        if(texto.equals("")){
                            lineaVacia=true;
                        }
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
                if(!lineaVacia){
                    listaRepuestosPreITV.add(fila);
                }else{
                    View view2 = findViewById(android.R.id.content);
                    Snackbar.make(view2, "Rellena al menos Repuesto y precio antes de añadir otra fila", Snackbar.LENGTH_SHORT).show();
                }

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

                Boolean lineaVacia=false;

                for (int j = 0; j < columnCount; j++) {
                    View cellView = row.getChildAt(j);

                    if (cellView instanceof EditText) {
                        EditText editText = (EditText) cellView;
                        String texto = editText.getText().toString();
                        if(texto.equals("")){
                            lineaVacia=true;
                        }
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

                // Agregar la lista de elementos de esta fila al ArrayList principal{
                if(!lineaVacia){
                    listaRepuestosPostITV.add(fila);
                }else{
                    View view2 = findViewById(android.R.id.content);
                    Snackbar.make(view2, "Rellena al menos Repuesto y precio antes de añadir otra fila", Snackbar.LENGTH_SHORT).show();
                }
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

                Boolean lineaVacia=false;

                for (int j = 0; j < columnCount; j++) {
                    View cellView = row.getChildAt(j);

                    if (cellView instanceof EditText) {
                        EditText editText = (EditText) cellView;
                        String texto = editText.getText().toString();
                        if(texto.equals("")){
                            lineaVacia=true;
                        }
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
                if(!lineaVacia){
                    listaReparaciones.add(fila);
                }else if(lineaVacia){
                    View view2 = findViewById(android.R.id.content);
                    Snackbar.make(view2, "Rellena la reparación y el tiempo antes de añadir otra linea", Snackbar.LENGTH_SHORT).show();
                }
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

    //Método que lee la tabla de revisiones y la guarda como String en la BD
    private void guardarTablaRevisiones(String matricula) {
        System.out.println("----------La tabla revisiones tiene "+tablaRevisiones.getChildCount()+" filas");
        listaRevisiones.clear();
        for (int i = 1; i < tablaRevisiones.getChildCount(); i++) {
            TableRow row = (TableRow) tablaRevisiones.getChildAt(i);
            View segundoElemento = row.getChildAt(1);
            String segundoString;

            // Obtener los valores de cada fila
            String primerString = ((TextView) row.getChildAt(0)).getText().toString();

            // Modificar para almacenar booleanos como true/false en lugar de representación de CheckBox
            if (segundoElemento instanceof CheckBox) {
                boolean isChecked = ((CheckBox) segundoElemento).isChecked();
                segundoString = String.valueOf(isChecked);
            } else {
                segundoString = ((TextView) segundoElemento).getText().toString();
            }

            String tercerString = ((TextView) row.getChildAt(2)).getText().toString();

            // Crear una lista para almacenar los valores
            List<String> revision = new ArrayList<>();
            revision.add(primerString);
            revision.add(segundoString);
            revision.add(tercerString);

            // Agregar la lista a la lista de revisiones
            listaRevisiones.add(revision);
        }

        // Convertir la lista a un String
        String listaComoString = convertirListaAString(listaRevisiones);

        // Guardar la lista en Firebase
        guardarLista(matricula, listaComoString, "listaRevisiones");
    }

    public void guardarInformacion(View view) {
        // Obtener la matrícula y nombre de los EditText
        EditText editTextMatricula = findViewById(R.id.editTextMatricula);
        EditText editTextNombre = findViewById(R.id.editTextNombre);
        String matriculaNueva = editTextMatricula.getText().toString().trim();
        String nombre = editTextNombre.getText().toString().trim();

        // Verificar que haya datos en la matrícula y nombre
        if (matriculaNueva.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Por favor, complete al menos nombre y matricula", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!matriculaNueva.equals(matricula)){
            eliminarMatriculaAntigua();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cochesCollectionRef = db.collection("TallerCarlos");

        // Consultar si existe un coche con la matrícula
        cochesCollectionRef.whereEqualTo("matricula", matriculaNueva)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean existeCoche = !queryDocumentSnapshots.isEmpty();

                    if (existeCoche) {
                        // Si existe, actualizar los datos del coche
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        documentSnapshot.getReference().update("nombre", nombre)
                                .addOnSuccessListener(aVoid -> {
                                    guardarInformacion();
                                    Toast.makeText(this, "Coche actualizado con éxito", Toast.LENGTH_LONG).show();
                                    // Cerrar esta actividad y volver a MainActivity
                                    finish();
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al actualizar coche: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Si no existe, crear un nuevo coche
                        Coche coche = new Coche(matriculaNueva, nombre);
                        cochesCollectionRef.add(coche)
                                .addOnSuccessListener(documentReference -> {
                                    guardarInformacion();
                                    Toast.makeText(this, "Coche agregado con éxito", Toast.LENGTH_LONG).show();
                                    // Cerrar esta actividad y volver a MainActivity
                                    finish();
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al agregar coche: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al buscar coche por matrícula: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarInformacion(){
        EditText editTextMatricula = findViewById(R.id.editTextMatricula);
        String matriculaNueva = editTextMatricula.getText().toString().trim();
        rellenarListaPreITV();
        rellenarListaPostITV();
        rellenarListaReparaciones();


        guardarLista(matriculaNueva,convertirListaAString(listaRepuestosPostITV),"listaPostITV");
        guardarLista(matriculaNueva,convertirListaAString(listaReparaciones),"listaReparaciones");
        guardarLista(matriculaNueva,convertirListaAString(listaRepuestosPreITV),"listaPreITV");
        guardarTablaRevisiones(matriculaNueva);
    }

    //Método que guarda la lista en la base de datos bajo la matrícula y con el nombre de campo recibido
    private void guardarLista(String matricula, String lista,String tipoLista) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cochesCollectionRef = db.collection("TallerCarlos");

        // Realizar una consulta para buscar el documento con la matrícula específica
        cochesCollectionRef.whereEqualTo("matricula", matricula)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Encontrar el documento que coincide con la matrícula
                        String documentId = document.getId();

                        // Actualizar el documento con la nueva lista
                        DocumentReference cocheRef = cochesCollectionRef.document(documentId);
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put(tipoLista, lista);

                        cocheRef.update(updateData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Lista "+tipoLista+" con éxito para la matrícula: " + matricula);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error al guardar Lista Pre ITV para la matrícula " + matricula, e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar coche por matrícula: " + e.getMessage(), e);
                });

    }

    public String convertirListaAString(ArrayList<List<String>> listaRepuestos) {
        Gson gson = new Gson();
        Log.d("TAG", "Lista de revisiones antes de la conversión a JSON: " + listaRepuestos);
        String jsonString = gson.toJson(listaRepuestos);
        Log.d("TAG", "JSON: " + jsonString);  // Esto imprime en la consola las listas guardadas
        return jsonString;
    }

    public void eliminarMatriculaAntigua(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cochesCollectionRef = db.collection("TallerCarlos");

        // Consultar si existen coches con la matrícula a eliminar
        cochesCollectionRef.whereEqualTo("matricula", matricula).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Eliminar cada documento encontrado
                        documentSnapshot.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Coche eliminado con éxito");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error al eliminar coche: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar coche por matrícula para eliminar: " + e.getMessage());
                });
    }

    private void leerListaPreITV(String matricula) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cochesCollectionRef = db.collection("TallerCarlos");

        cochesCollectionRef.whereEqualTo("matricula", matricula)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Verificamos que exista un documento con la matrícula dada
                        if (documentSnapshot.exists()) {
                            String listaPreITVString = documentSnapshot.getString("listaPreITV");
                            if (listaPreITVString != null) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<List<String>>>() {}.getType();
                                listaRepuestosPreITV = gson.fromJson(listaPreITVString, type);
                                if (listaRepuestosPreITV != null) {
                                    for (int i = 0; i < listaRepuestosPreITV.size(); i++) {
                                        List<String> repuestoData = listaRepuestosPreITV.get(i);
                                        Log.d(TAG, "Repuesto " + i + ": " + repuestoData.toString());
                                    }
                                } else {
                                    Log.d(TAG, "La lista de repuestos Pre ITV está vacía o es nula.");
                                }
                            }
                        } else {
                            // Si no existe un documento con esa matrícula, mostrar un mensaje
                            Log.d(TAG, "No se encontró ningún coche con la matrícula: " + matricula);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar coche por matrícula: " + e.getMessage());
                });
    }

    private void leerListaPostITV(String matricula) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cochesCollectionRef = db.collection("TallerCarlos");

        cochesCollectionRef.whereEqualTo("matricula", matricula)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Verificamos que exista un documento con la matrícula dada
                        if (documentSnapshot.exists()) {
                            String listaPostITVString = documentSnapshot.getString("listaPostITV");
                            if (listaPostITVString != null) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<List<String>>>() {}.getType();
                                listaRepuestosPostITV = gson.fromJson(listaPostITVString, type);
                                if (listaRepuestosPostITV != null) {
                                    for (int i = 0; i < listaRepuestosPostITV.size(); i++) {
                                        List<String> repuestoData = listaRepuestosPostITV.get(i);
                                        Log.d(TAG, "Repuesto " + i + ": " + repuestoData.toString());
                                    }
                                } else {
                                    Log.d(TAG, "La lista de repuestos Post ITV está vacía o es nula.");
                                }
                            }
                        } else {
                            // Si no existe un documento con esa matrícula, mostrar un mensaje
                            Log.d(TAG, "No se encontró ningún coche con la matrícula: " + matricula);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar coche por matrícula: " + e.getMessage());
                });
    }

    private void leerListaReparaciones(String matricula) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cochesCollectionRef = db.collection("TallerCarlos");

        cochesCollectionRef.whereEqualTo("matricula", matricula)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Verificamos que exista un documento con la matrícula dada
                        if (documentSnapshot.exists()) {
                            String listaPostITVString = documentSnapshot.getString("listaReparaciones");
                            if (listaPostITVString != null) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<List<String>>>() {}.getType();
                                listaReparaciones = gson.fromJson(listaPostITVString, type);
                                if (listaReparaciones != null) {
                                    for (int i = 0; i < listaReparaciones.size(); i++) {
                                        List<String> repuestoData = listaReparaciones.get(i);
                                        Log.d(TAG, "Reparacion " + i + ": " + repuestoData.toString());
                                    }
                                } else {
                                    Log.d(TAG, "La lista de reparaciones está vacía o es nula.");
                                }
                            }
                        } else {
                            // Si no existe un documento con esa matrícula, mostrar un mensaje
                            Log.d(TAG, "No se encontró ningún coche con la matrícula: " + matricula);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar coche por matrícula: " + e.getMessage());
                });
    }

    private void leerListaRevisiones(String matricula) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cochesCollectionRef = db.collection("TallerCarlos");

        cochesCollectionRef.whereEqualTo("matricula", matricula)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.contains("listaRevisiones")) {
                                // El campo listaRevisiones existe en el documento
                                String listaRevisionesString = documentSnapshot.getString("listaRevisiones");
                                //Si existe el campo lo carga en la lista
                                if (listaRevisionesString != null) {
                                    // Convertir JSON a ArrayList<List<String>>
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
                                    listaRevisiones = gson.fromJson(listaRevisionesString, type);

                                    int retrasoMilisegundos = 2000;

                                    // Handler para ejecutar el código después del retraso
                                    Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        // Llama al método que deseas ejecutar después del retraso
                                        rellenarRevisiones();
                                    }, retrasoMilisegundos);
                                    // Llamar al método para vaciar y llenar la tabla

                                }
                            }
                        } else {
                            Log.d(TAG, "No se encontró ningún coche con la matrícula: " + matricula);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar coche por matrícula: " + e.getMessage());
                });
    }
    //Sigue aqui
    private void rellenarRevisiones() {
        int childCount = tablaRevisiones.getChildCount();

        // Comenzamos desde 1 para evitar borrar la primera fila (encabezados)
        for (int i = 1; i < childCount; i++) {
            TableRow row = (TableRow) tablaRevisiones.getChildAt(i);

            // Actualizar los valores en la fila
            View segundoElemento = row.getChildAt(1);
            if (segundoElemento instanceof CheckBox) {
                ((CheckBox) segundoElemento).setChecked(Boolean.parseBoolean(listaRevisiones.get(i - 1).get(1)));
            }

            View tercerElemento = row.getChildAt(2);
            if (tercerElemento instanceof EditText) {
                ((EditText) tercerElemento).setText(listaRevisiones.get(i - 1).get(2));
            }
        }
    }

}