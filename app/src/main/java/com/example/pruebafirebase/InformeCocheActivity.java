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

public class InformeCocheActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String matricula;

    private TableLayout tableLayout;
    private Button btnAddRow;
    private ArrayList<String> listaRepuestosPreITV;

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
        listaRepuestosPreITV.add("Tubo de escape muy tuning");
        listaRepuestosPreITV.add("Maletero de coche");
        // Agrega más repuestos si es necesario...

        tableLayout = findViewById(R.id.tableLayout);
        btnAddRow = findViewById(R.id.btnAddRow);
        btnAddRow.setOnClickListener(view -> agregarFila());


        // Llama a este método antes de llenar la tabla
        crearEncabezadoTabla();

        // Luego llenas la tabla
        llenarTabla();

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
        for (String repuesto : listaRepuestosPreITV) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Crear celdas
            EditText textViewArticulo = crearCelda(repuesto);
            CheckBox checkBoxComprado = crearCheckBox();
            CheckBox checkBoxPuesto = crearCheckBox();
            EditText textViewPrecio = crearCelda("$10");  // Puedes cambiarlo según tus datos

            // Añadir vistas a la fila
            row.addView(textViewArticulo);
            row.addView(checkBoxComprado);
            row.addView(checkBoxPuesto);
            row.addView(textViewPrecio);

            // Agregar la fila a la tabla
            tableLayout.addView(row);
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
        listaRepuestosPreITV.add("");
        llenarTabla();
        // Puedes implementar lógica para añadir una nueva fila a la tabla
        // Aquí se debería agregar una nueva fila con los datos que necesites
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