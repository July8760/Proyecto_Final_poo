package Clientes;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaClientes extends JFrame {
    private JTable tablaClientes;
    private DefaultTableModel modelo;
    private JTextField txtNombres, txtEmail, txtTelf, txtDireccion, txtBuscar;
    private JButton btnNuevo, btnGuardar, btnEditar, btnEliminar, btnCancelar, btnBuscar;
    private final ClienteDAO clienteDAO;
    private Cliente clienteSeleccionado;
    private boolean modoEdicion = false;
    
    public VentanaClientes() {
        clienteDAO = new ClienteDAO();
        inicializarComponentes();
        cargarClientes();
    }
    
    private void inicializarComponentes() {
        setTitle("Gestión de Clientes");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de búsqueda
        JPanel panelBusqueda = crearPanelBusqueda();
        panelPrincipal.add(panelBusqueda, BorderLayout.NORTH);
        
        // Panel de formulario
        JPanel panelFormulario = crearPanelFormulario();
        panelPrincipal.add(panelFormulario, BorderLayout.WEST);
        
        // Panel de tabla
        JPanel panelTabla = crearPanelTabla();
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Búsqueda de Clientes"));
        
        panel.add(new JLabel("Buscar por nombre:"));
        txtBuscar = new JTextField(20);
        panel.add(txtBuscar);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarClientes());
        panel.add(btnBuscar);
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarClientes();
        });
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        panel.setPreferredSize(new Dimension(300, 400));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombres
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombres:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombres = new JTextField(15);
        panel.add(txtNombres, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(15);
        panel.add(txtEmail, gbc);
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTelf = new JTextField(15);
        panel.add(txtTelf, gbc);
        
        // Dirección
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Dirección:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtDireccion = new JTextField(15);
        panel.add(txtDireccion, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Clientes"));
        
        // Crear tabla
        String[] columnas = {"ID", "Nombres", "Email", "Teléfono", "Dirección", "Fecha Creación"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaClientes = new JTable(modelo);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Configurar columnas
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombres
        tablaClientes.getColumnModel().getColumn(2).setPreferredWidth(150); // Email
        tablaClientes.getColumnModel().getColumn(3).setPreferredWidth(100); // Teléfono
        tablaClientes.getColumnModel().getColumn(4).setPreferredWidth(200); // Dirección
        tablaClientes.getColumnModel().getColumn(5).setPreferredWidth(120); // Fecha
        
        // Agregar listener para selección
        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaClientes.getSelectedRow();
                if (fila >= 0) {
                    cargarClienteSeleccionado(fila);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaClientes);
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
        btnNuevo.addActionListener(e -> nuevoCliente());
        btnGuardar.addActionListener(e -> guardarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnCancelar.addActionListener(e -> cancelarOperacion());
        
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnCancelar);
        
        return panel;
    }
    
    private void cargarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.obtenerClientes();
            modelo.setRowCount(0);
            
            for (Cliente cliente : clientes) {
                modelo.addRow(new Object[]{
                    cliente.getId(),
                    cliente.getNombres(),
                    cliente.getEmail(),
                    cliente.getTelf(),
                    cliente.getDireccion(),
                    cliente.getCreado().toString()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarClientes() {
        String busqueda = txtBuscar.getText().trim();
        if (busqueda.isEmpty()) {
            cargarClientes();
            return;
        }
        
        try {
            List<Cliente> clientes = clienteDAO.buscarClientesPorNombre(busqueda);
            modelo.setRowCount(0);
            
            for (Cliente cliente : clientes) {
                modelo.addRow(new Object[]{
                    cliente.getId(),
                    cliente.getNombres(),
                    cliente.getEmail(),
                    cliente.getTelf(),
                    cliente.getDireccion(),
                    cliente.getCreado().toString()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar clientes: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarClienteSeleccionado(int fila) {
        int id = (Integer) modelo.getValueAt(fila, 0);
        try {
            clienteSeleccionado = clienteDAO.buscarClientePorId(id);
            if (clienteSeleccionado != null) {
                txtNombres.setText(clienteSeleccionado.getNombres());
                txtEmail.setText(clienteSeleccionado.getEmail());
                txtTelf.setText(clienteSeleccionado.getTelf());
                txtDireccion.setText(clienteSeleccionado.getDireccion());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar cliente: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void nuevoCliente() {
        limpiarFormulario();
        habilitarFormulario(true);
        modoEdicion = false;
        clienteSeleccionado = null;
        
        btnNuevo.setEnabled(false);
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnCancelar.setEnabled(true);
        
        txtNombres.requestFocus();
    }
    
    private void guardarCliente() {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            if (modoEdicion) {
                // Actualizar cliente existente
                clienteSeleccionado.setNombres(txtNombres.getText().trim());
                clienteSeleccionado.setEmail(txtEmail.getText().trim());
                clienteSeleccionado.setTelf(txtTelf.getText().trim());
                clienteSeleccionado.setDireccion(txtDireccion.getText().trim());
                
                clienteDAO.actualizarCliente(clienteSeleccionado);
                JOptionPane.showMessageDialog(this, "Cliente actualizado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Crear nuevo cliente
                Cliente nuevoCliente = new Cliente(
                    txtNombres.getText().trim(),
                    txtEmail.getText().trim(),
                    txtTelf.getText().trim(),
                    txtDireccion.getText().trim()
                );
                
                clienteDAO.insertarCliente(nuevoCliente);
                JOptionPane.showMessageDialog(this, "Cliente creado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            
            cancelarOperacion();
            cargarClientes();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar cliente: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarCliente() {
        if (clienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente para editar", 
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
        
        txtNombres.requestFocus();
    }
    
    private void eliminarCliente() {
        if (clienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente para eliminar", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de que desea eliminar el cliente '" + clienteSeleccionado.getNombres() + "'?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                clienteDAO.eliminarCliente(clienteSeleccionado.getId());
                JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarClientes();
                limpiarFormulario();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cliente: " + e.getMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelarOperacion() {
        limpiarFormulario();
        habilitarFormulario(false);
        modoEdicion = false;
        clienteSeleccionado = null;
        
        btnNuevo.setEnabled(true);
        btnGuardar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnCancelar.setEnabled(false);
        
        tablaClientes.clearSelection();
    }
    
    private void limpiarFormulario() {
        txtNombres.setText("");
        txtEmail.setText("");
        txtTelf.setText("");
        txtDireccion.setText("");
    }
    
    private void habilitarFormulario(boolean habilitado) {
        txtNombres.setEnabled(habilitado);
        txtEmail.setEnabled(habilitado);
        txtTelf.setEnabled(habilitado);
        txtDireccion.setEnabled(habilitado);
    }
    
    private boolean validarFormulario() {
        if (txtNombres.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Los nombres son obligatorios", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            txtNombres.requestFocus();
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
        
        // Verificar email duplicado
        try {
            boolean emailExiste;
            if (modoEdicion && clienteSeleccionado != null) {
                emailExiste = clienteDAO.verificarEmailExisteExcluyendoId(email, clienteSeleccionado.getId());
            } else {
                emailExiste = clienteDAO.verificarEmailExiste(email);
            }
            
            if (emailExiste) {
                JOptionPane.showMessageDialog(this, "El email ya existe en la base de datos", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                txtEmail.requestFocus();
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar email: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
} 
