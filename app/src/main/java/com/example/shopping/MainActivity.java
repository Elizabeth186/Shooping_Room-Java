package com.example.shopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shopping.AppDatabase;
import com.example.shopping.ProductoAdapter;
import com.example.shopping.R;
import com.example.shopping.data.Entity.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements ProductoAdapter.OnDeleteItemClickListener {
    private boolean isCarrito;
    private List<Producto> productos = new ArrayList<>(); // Inicializar la lista de productos
    private List<Producto> productosEnCarrito;

    private EditText editTextNombre, editTextPrecio;
    private Button btnAgregarProducto;

    private AppDatabase database;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextPrecio = findViewById(R.id.editTextPrecio);
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);

        // Inicializar la base de datos
        database = AppDatabase.getInstance(this);

        // Inicializar ExecutorService con un solo hilo de ejecución
        executorService = Executors.newSingleThreadExecutor();
        productosEnCarrito = new ArrayList<>();

        // Manejar el clic del botón "Agregar Producto"
        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarProducto();
            }
        });

        mostrarProductos();
    }

    private void agregarProducto() {
        // Obtener el nombre y precio del producto
        final String nombre = editTextNombre.getText().toString();
        final double precio = Double.parseDouble(editTextPrecio.getText().toString());

        // Ejecutar la operación de inserción en segundo plano utilizando ExecutorService
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Insertar el producto en la base de datos
                Producto producto = new Producto(nombre, precio);
                database.productoDao().insert(producto);

                // Actualizar la interfaz de usuario en el hilo principal
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Limpiar los campos de texto después de agregar el producto
                        editTextNombre.setText("");
                        editTextPrecio.setText("");
                    }
                });
            }
        });

        mostrarProductos();
    }

    @Override
    public void onDeleteItemClick(int position) {
        // Obtener el producto a eliminar
        Producto productoAEliminar = productosEnCarrito.get(position);

        // Eliminar el producto de la lista del carrito
        productosEnCarrito.remove(position);


        // Eliminar el producto de la base de datos
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                database.productoDao().delete(productoAEliminar);
            }
        });

    }
    private void mostrarProductos() {
        // Ejecutar la operación de obtener productos de la base de datos en segundo plano utilizando ExecutorService
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Obtener la lista de productos desde la base de datos
                productos = database.productoDao().getAllProductos();

                // Actualizar la interfaz de usuario en el hilo principal
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Crear un adaptador para la lista de productos y configurar el RecyclerView
                        ProductoAdapter adapter = new ProductoAdapter(productos, isCarrito);
                        RecyclerView recyclerView = findViewById(R.id.recyclerViewProductos);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerView.setAdapter(adapter);

                        // Configurar el OnClickListener para agregar al carrito
                        adapter.setOnItemClickListener(new ProductoAdapter.OnItemClickListener() {
                            @Override
                            public void onAddToCartClick(int position) {
                                agregarAlCarrito(position);
                            }
                        });
                    }
                });
            }
        });

    }

    private void agregarAlCarrito(int position) {
        Producto productoSeleccionado = productos.get(position);
        productosEnCarrito.add(productoSeleccionado);
        mostrarProductosEnCarrito();
    }

    private void mostrarProductosEnCarrito() {
        // Actualizamos el adaptador con la lista actualizada de productos en el carrito
        ProductoAdapter adapter = new ProductoAdapter(productosEnCarrito, true);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProductosCar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        double total = 0;
        for (Producto producto : productosEnCarrito) {
            total += producto.getPrecio();
        }

        TextView textViewTotal = findViewById(R.id.textViewTotal);
        textViewTotal.setText("Total: $" + total);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener el ExecutorService cuando la actividad se destruye para evitar pérdidas de memoria
        if (executorService != null) {
            executorService.shutdown();
        }
    }



}