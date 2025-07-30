package Clientes;

import java.time.LocalDateTime;

public class Cliente {
    private int id;
    private String nombres;
    private String email;
    private String telf;
    private String direccion;
    private LocalDateTime creado;
    
    public Cliente(int id, String nombres, String email, String telf, String direccion, LocalDateTime creado) {
        this.id = id;
        this.nombres = nombres;
        this.email = email;
        this.telf = telf;
        this.direccion = direccion;
        this.creado = creado;
    }
    
    public Cliente(String nombres, String email, String telf, String direccion) {
        this.nombres = nombres;
        this.email = email;
        this.telf = telf;
        this.direccion = direccion;
        this.creado = LocalDateTime.now();
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelf() { return telf; }
    public void setTelf(String telf) { this.telf = telf; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public LocalDateTime getCreado() { return creado; }
    public void setCreado(LocalDateTime creado) { this.creado = creado; }
    
    @Override
    public String toString() {
        return nombres + " (" + email + ")";
    }
} 
