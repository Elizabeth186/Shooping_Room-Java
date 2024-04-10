package com.example.shopping;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.data.Entity.Producto;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {
    private List<Producto> productos;
    private boolean isCarrito; // Variable para indicar si es la lista del carrito
    private OnItemClickListener clickListener;
    private OnDeleteItemClickListener deleteListener;


    // Constructor
    public ProductoAdapter(List<Producto> productos, boolean isCarrito) {
        this.productos = productos;
        this.isCarrito = isCarrito;
    }

    // Métodos para gestionar clics en los elementos de la lista
    public interface OnItemClickListener {
        void onAddToCartClick(int position);
    }

    public interface OnDeleteItemClickListener {
        void onDeleteItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    // Método para establecer el listener de eliminación
    public void setOnDeleteItemClickListener(OnDeleteItemClickListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.bind(producto);

        if (isCarrito) {
            // En el caso de estar en la lista del carrito, ocultamos el botón "Agregar al carrito"
            holder.btnAddToCart.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.VISIBLE); // Mostrar el botón "Eliminar"

            // Configuramos el OnClickListener para el botón "Eliminar"
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteListener != null) {
                        deleteListener.onDeleteItemClick(holder.getAdapterPosition());
                        // Eliminar el producto de la lista
                        productos.remove(holder.getAdapterPosition());
                        // Notificar al RecyclerView que se eliminó un elemento
                        notifyItemRemoved(holder.getAdapterPosition());
                        Toast.makeText(v.getContext(), "Producto eliminado del carrito", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // En caso contrario, mostramos el botón "Agregar al carrito" y ocultamos el botón "Eliminar"
            holder.btnAddToCart.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.GONE); // Ocultar el botón "Eliminar"

            // Configuramos el OnClickListener para el botón "Agregar al carrito"
            holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onAddToCartClick(holder.getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        public Button btnAddToCart;
        public Button btnDelete; // Botón para eliminar el producto del carrito
        private TextView textViewNombre;
        private TextView textViewPrecio;
        // Interface para manejar el clic en el botón de eliminar
        public interface OnDeleteItemClickListener {
            void onDeleteItemClick(int position);
        }
        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializa los botones
            btnAddToCart = itemView.findViewById(R.id.btnAgregarAlCarrito);
            btnDelete = itemView.findViewById(R.id.btndeletecar);
            // Inicializa los TextViews
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecio);


        }

        public void bind(Producto producto) {
            textViewNombre.setText(producto.getNombre());
            textViewPrecio.setText("$" + String.valueOf(producto.getPrecio()));
        }
    }
}