package Usuarios;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaUsuarios extends JFrame {
    private JTable tablaUsuarios;
    private DefaultTableModel modelo;
    private JTextField txtNombre, txtPassword, txtEmail;
    private JComboBox<Rol> comboRoles;
    private JButton btnNuevo, btnGuardar, btnEditar, btnEliminar, btnCancelar;
    private UsuarioDAO usuarioDAO;
    private List<Rol> roles;
    private Usuario usuarioSeleccionado;
    private boolean modoEdicion = false;
    
    public VentanaUsuarios() {
        usuarioDAO = new UsuarioDAO();
        inicializarComponentes();
        cargarRoles();
        cargarUsuarios();
    }
    
    private void inicializarComponentes() {
        setTitle("Gestión de Usuarios");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de formulario
        JPanel panelFormulario = crearPanelFormulario();
        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);
        
        // Panel de tabla
        JPanel panelTabla = crearPanelTabla();
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        panel.add(txtNombre, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPassword = new JTextField(20);
        panel.add(txtPassword, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);
        
        // Rol
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Rol:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        comboRoles = new JComboBox<>();
        panel.add(comboRoles, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Usuarios"));
        
        // Crear tabla
        String[] columnas = {"ID", "Nombre", "Password","Email", "Rol", "Fecha Creación"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaUsuarios = new JTable(modelo);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Agregar listener para selección
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaUsuarios.getSelectedRow();
                if (fila >= 0) {
                    cargarUsuarioSeleccionado(fila);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnCancelar = new JButton("Cancelar");
        
        // Configurar botones
        btnGuardar.setEnabled(false);
        btnCancelar.setEnabled(false);
        
        // Agregar listeners
        btnNuevo.addActionListener(e -> nuevoUsuario());
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnCancelar.addActionListener(e -> cancelarOperacion());
        
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnCancelar);
        
        return panel;
    }
    
    private void cargarRoles() {
        try {
            roles = usuarioDAO.obtenerRoles();
            comboRoles.removeAllItems();
            for (Rol rol : roles) {
                comboRoles.addItem(rol);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar roles: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerUsuarios();
            modelo.setRowCount(0);
            
            for (Usuario usuario : usuarios) {
                String rolDescripcion = usuario.getRol() != null ? usuario.getRol().getDescripcion() : "Sin rol";
                modelo.addRow(new Object[]{
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getPassword(),
                    usuario.getEmail(),
                    rolDescripcion,
                    usuario.getCreado().toString()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarUsuarioSeleccionado(int fila) {
        int id = (Integer) modelo.getValueAt(fila, 0);
        try {
            usuarioSeleccionado = usuarioDAO.buscarUsuarioPorId(id);
            if (usuarioSeleccionado != null) {
                txtNombre.setText(usuarioSeleccionado.getNombre());
                txtPassword.setText(usuarioSeleccionado.getPassword());
                txtEmail.setText(usuarioSeleccionado.getEmail());

                // Seleccionar el rol correspondiente
                for (int i = 0; i < comboRoles.getItemCount(); i++) {
                    Rol rol = comboRoles.getItemAt(i);
                    if (rol.getId() == usuarioSeleccionado.getRolId()) {
                        comboRoles.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuario: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void nuevoUsuario() {
        limpiarFormulario();
        habilitarFormulario(true);
        modoEdicion = false;
        usuarioSeleccionado = null;
        
        btnNuevo.setEnabled(false);
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnCancelar.setEnabled(true);
        
        txtNombre.requestFocus();
    }
    
    private void guardarUsuario() {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            Rol rolSeleccionado = (Rol) comboRoles.getSelectedItem();
            if (rolSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un rol", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (modoEdicion) {
                // Actualizar usuario existente
                usuarioSeleccionado.setNombre(txtNombre.getText().trim());
                usuarioSeleccionado.setPassword(txtPassword.getText().trim());
                usuarioSeleccionado.setEmail(txtEmail.getText().trim());
                usuarioSeleccionado.setRolId(rolSeleccionado.getId());
                
                usuarioDAO.actualizarUsuario(usuarioSeleccionado);
                JOptionPane.showMessageDialog(this, "Usuario actualizado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Crear nuevo usuario
                Usuario nuevoUsuario = new Usuario(
                    rolSeleccionado.getId(),
                    txtNombre.getText().trim(),
                    txtPassword.getText().trim(),
                    txtEmail.getText().trim()
                );
                
                usuarioDAO.insertarUsuario(nuevoUsuario);
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            
            cancelarOperacion();
            cargarUsuarios();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar usuario: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarUsuario() {
        if (usuarioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario para editar", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        habilitarFormulario(true);
        modoEdicion = true;
        
        btnNuevo.setEnabled(false);
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnCancelar.setEnabled(true);
        
        txtNombre.requestFocus();
    }
    
    private void eliminarUsuario() {
        if (usuarioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario para eliminar", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de que desea eliminar el usuario '" + usuarioSeleccionado.getNombre() + "'?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                usuarioDAO.eliminarUsuario(usuarioSeleccionado.getId());
                JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
                limpiarFormulario();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar usuario: " + e.getMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelarOperacion() {
        limpiarFormulario();
        habilitarFormulario(false);
        modoEdicion = false;
        usuarioSeleccionado = null;
        
        btnNuevo.setEnabled(true);
        btnGuardar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnCancelar.setEnabled(false);
        
        tablaUsuarios.clearSelection();
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        if (comboRoles.getItemCount() > 0) {
            comboRoles.setSelectedIndex(0);
        }
    }
    
    private void habilitarFormulario(boolean habilitado) {
        txtNombre.setEnabled(habilitado);
        txtEmail.setEnabled(habilitado);
        txtPassword.setEnabled(habilitado);
        comboRoles.setEnabled(habilitado);
    }
    
    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }

        if (txtPassword.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El password es obligatorio",
                    "Error", JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocus();
            return false;
        }

        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El email es obligatorio", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        // Validación básica de email
        String email = txtEmail.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "El formato del email no es válido", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        return true;
    }

    public void setTxtPassword(JTextField txtPassword) {
        this.txtPassword = txtPassword;
    }
}
