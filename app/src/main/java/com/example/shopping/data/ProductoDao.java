package com.example.shopping.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.shopping.data.Entity.Producto;

import java.util.List;


@Dao
public interface ProductoDao {
    @Insert
    void insert(Producto producto);

    @Delete
    void delete(Producto producto);

    @Query("SELECT * FROM productos")
    List<Producto> getAllProductos();
}
