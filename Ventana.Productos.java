package Ventas;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaProductos extends JFrame {
    private JTable tablaProductos;
    private DefaultTableModel modelo;
    private JTextField txtNombre, txtDescripcion, txtPrecio, txtStock, txtBuscar;
    private JButton btnNuevo, btnGuardar, btnEditar, btnEliminar, btnCancelar, btnBuscar;
    private final ProductoDAO productoDAO;
    private Producto productoSeleccionado;
    private boolean modoEdicion = false;
    
    public VentanaProductos() {
        productoDAO = new ProductoDAO();
        inicializarComponentes();
        cargarProductos();
    }
    
    private void inicializarComponentes() {
        setTitle("Gestión de Productos");
        setSize(1000, 700);
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
        panel.setBorder(BorderFactory.createTitledBorder("Búsqueda de Productos"));
        
        panel.add(new JLabel("Buscar por nombre:"));
        txtBuscar = new JTextField(20);
        panel.add(txtBuscar);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarProductos());
        panel.add(btnBuscar);
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarProductos();
        });
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Producto"));
        panel.setPreferredSize(new Dimension(350, 400));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(15);
        panel.add(txtNombre, gbc);
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtDescripcion = new JTextField(15);
        panel.add(txtDescripcion, gbc);
        
        // Precio
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Precio:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPrecio = new JTextField(15);
        panel.add(txtPrecio, gbc);
        
        // Stock
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Stock:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtStock = new JTextField(15);
        panel.add(txtStock, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Productos"));
        
        // Crear tabla
        String[] columnas = {"ID", "Nombre", "Descripción", "Precio", "Stock", "Fecha Creación"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProductos = new JTable(modelo);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Configurar columnas
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(150);  // Nombre
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(200);  // Descripción
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(100);  // Precio
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(80);   // Stock
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(120);  // Fecha
        
        // Agregar listener para selección
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaProductos.getSelectedRow();
                if (fila >= 0) {
                    cargarProductoSeleccionado(fila);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
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
        btnNuevo.addActionListener(e -> nuevoProducto());
        btnGuardar.addActionListener(e -> guardarProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnCancelar.addActionListener(e -> cancelarOperacion());
        
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnCancelar);
        
        return panel;
    }
    
    private void cargarProductos() {
        try {
            List<Producto> productos = productoDAO.obtenerProductos();
            modelo.setRowCount(0);
            
            for (Producto producto : productos) {
                modelo.addRow(new Object[]{
                    producto.getId(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    String.format("$%.2f", producto.getPrecio()),
                    producto.getStock(),
                    producto.getCreado() != null ? producto.getCreado().toString() : ""
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarProductos() {
        String busqueda = txtBuscar.getText().trim();
        if (busqueda.isEmpty()) {
            cargarProductos();
            return;
        }
        
        try {
            List<Producto> productos = productoDAO.buscarProductosPorNombre(busqueda);
            modelo.setRowCount(0);
            
            for (Producto producto : productos) {
                modelo.addRow(new Object[]{
                    producto.getId(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    String.format("$%.2f", producto.getPrecio()),
                    producto.getStock(),
                    producto.getCreado() != null ? producto.getCreado().toString() : ""
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar productos: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarProductoSeleccionado(int fila) {
        int id = (Integer) modelo.getValueAt(fila, 0);
        try {
            productoSeleccionado = productoDAO.buscarProductoPorId(id);
            if (productoSeleccionado != null) {
                txtNombre.setText(productoSeleccionado.getNombre());
                txtDescripcion.setText(productoSeleccionado.getDescripcion());
                txtPrecio.setText(String.valueOf(productoSeleccionado.getPrecio()));
                txtStock.setText(String.valueOf(productoSeleccionado.getStock()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar producto: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void nuevoProducto() {
        limpiarFormulario();
        habilitarFormulario(true);
        modoEdicion = false;
        productoSeleccionado = null;
        
        btnNuevo.setEnabled(false);
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnCancelar.setEnabled(true);
        
        txtNombre.requestFocus();
    }
    
    private void guardarProducto() {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            if (modoEdicion) {
                // Actualizar producto existente
                productoSeleccionado.setNombre(txtNombre.getText().trim());
                productoSeleccionado.setDescripcion(txtDescripcion.getText().trim());
                productoSeleccionado.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
                productoSeleccionado.setStock(Integer.parseInt(txtStock.getText().trim()));
                
                productoDAO.actualizarProducto(productoSeleccionado);
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Crear nuevo producto
                Producto nuevoProducto = new Producto(
                    txtNombre.getText().trim(),
                    txtDescripcion.getText().trim(),
                    Double.parseDouble(txtPrecio.getText().trim()),
                    Integer.parseInt(txtStock.getText().trim())
                );
                
                productoDAO.insertarProducto(nuevoProducto);
                JOptionPane.showMessageDialog(this, "Producto creado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            
            cancelarOperacion();
            cargarProductos();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar producto: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en el formato de precio o stock", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarProducto() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto para editar", 
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
    
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto para eliminar", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de que desea eliminar el producto '" + productoSeleccionado.getNombre() + "'?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                productoDAO.eliminarProducto(productoSeleccionado.getId());
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
                limpiarFormulario();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar producto: " + e.getMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelarOperacion() {
        limpiarFormulario();
        habilitarFormulario(false);
        modoEdicion = false;
        productoSeleccionado = null;
        
        btnNuevo.setEnabled(true);
        btnGuardar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnCancelar.setEnabled(false);
        
        tablaProductos.clearSelection();
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
    }
    
    private void habilitarFormulario(boolean habilitado) {
        txtNombre.setEnabled(habilitado);
        txtDescripcion.setEnabled(habilitado);
        txtPrecio.setEnabled(habilitado);
        txtStock.setEnabled(habilitado);
    }
    
    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtPrecio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El precio es obligatorio", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            txtPrecio.requestFocus();
            return false;
        }
        
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (precio <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                txtPrecio.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            txtPrecio.requestFocus();
            return false;
        }
        
        if (txtStock.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El stock es obligatorio", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            txtStock.requestFocus();
            return false;
        }
        
        try {
            int stock = Integer.parseInt(txtStock.getText().trim());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "El stock no puede ser negativo", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                txtStock.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El stock debe ser un número entero válido", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            txtStock.requestFocus();
            return false;
        }
        
        // Verificar nombre duplicado
        try {
            boolean nombreExiste;
            if (modoEdicion && productoSeleccionado != null) {
                nombreExiste = productoDAO.verificarNombreExisteExcluyendoId(txtNombre.getText().trim(), productoSeleccionado.getId());
            } else {
                nombreExiste = productoDAO.verificarNombreExiste(txtNombre.getText().trim());
            }
            
            if (nombreExiste) {
                JOptionPane.showMessageDialog(this, "El nombre ya existe en la base de datos", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar nombre: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            VentanaProductos ventana = new VentanaProductos();
            ventana.setVisible(true);
        });
    }
} 
