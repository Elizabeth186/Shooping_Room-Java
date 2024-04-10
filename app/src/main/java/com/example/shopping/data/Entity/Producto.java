package com.example.shopping.data.Entity;

import android.os.Parcel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "productos")
public class Producto {
        @PrimaryKey(autoGenerate = true)
        private int id;
        private String nombre;
        private double precio;

        public Producto(String nombre, double precio) {
                this.nombre = nombre;
                this.precio = precio;
        }

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public String getNombre() {
                return nombre;
        }

        public void setNombre(String nombre) {
                this.nombre = nombre;
        }

        public double getPrecio() {
                return precio;
        }

        public void setPrecio(double precio) {
                this.precio = precio;
        }
}