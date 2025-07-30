package Ventas;

import java.time.LocalDateTime;

public class Producto {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private LocalDateTime creado;
    private int cantidad; // Para el carrito de compras

    // Constructor para crear nuevos productos
    public Producto(String nombre, String descripcion, double precio, int stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.cantidad = 1;
    }

    // Constructor para productos existentes
    public Producto(int id, String nombre, String descripcion, double precio, int stock, LocalDateTime creado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.creado = creado;
        this.cantidad = 1;
    }

    // Constructor para compatibilidad con el código existente
    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.cantidad = 1;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public LocalDateTime getCreado() { return creado; }
    public int getCantidad() { return cantidad; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setStock(int stock) { this.stock = stock; }
    public void setCreado(LocalDateTime creado) { this.creado = creado; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
