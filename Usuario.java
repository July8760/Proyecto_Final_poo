package Usuarios;
import java.time.LocalDateTime;

public class Usuario {
    private int id;
    private int rolId;
    private String nombre;
    private String password;
    private String email;
    private LocalDateTime creado;
    private Rol rol; // Para mostrar la descripción del rol
    
    public Usuario(int id, int rolId, String nombre, String password, String email, LocalDateTime creado) {
        this.id = id;
        this.rolId = rolId;
        this.nombre = nombre;
        this.password = password;
        this.email = email;
        this.creado = creado;
    }
    
    public Usuario(int rolId, String nombre, String password, String email) {
        this.rolId = rolId;
        this.nombre = nombre;
        this.password = password;
        this.email = email;
        this.creado = LocalDateTime.now();
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getRolId() { return rolId; }
    public void setRolId(int rolId) { this.rolId = rolId; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDateTime getCreado() { return creado; }
    public void setCreado(LocalDateTime creado) { this.creado = creado; }
    
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    
    @Override
    public String toString() {
        return nombre + " (" + email + ")";
    }
} 
