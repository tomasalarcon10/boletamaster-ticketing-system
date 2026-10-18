package gui;

import sistema.SistemaVentaTiquetes;

import usuarios.Cliente;
import usuarios.Usuario;
import tiquetes.Tiquete;
import tiquetes.TiqueteBasico;
import eventos.Evento;
import marketplace.OfertaReventa;
import eventos.Localidad;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel de interfaz gráfica para clientes - VERSIÓN CORREGIDA
 */
public class ClientePanel extends UserPanel {
    private Cliente cliente;
    
    // Componentes de la interfaz
    private JLabel lblSaldo;
    private JLabel lblTiquetesCount;
    private JTable tableTiquetes;
    private JTable tableEventos;
    private JTable tableOfertasMarketplace;
    private DefaultTableModel modelTiquetes;
    private DefaultTableModel modelEventos;
    private DefaultTableModel modelOfertas;
    
    // Botones principales
    private JButton btnRecargarSaldo;
    private JButton btnComprarTiquete;
    private JButton btnTransferirTiquete;
    private JButton btnPublicarOferta;
    private JButton btnHacerContraOferta;
    private JButton btnImprimirTiquete;
    
    public ClientePanel(MainGUI mainGUI, SistemaVentaTiquetes sistema) {
        super(mainGUI, sistema);
        initializePanel();
    }
    
    // Setter para el cliente actual
    public void setClienteActual(Cliente cliente) {
        this.cliente = cliente;
        this.usuarioActual = cliente;
    }
    
    private void initializePanel() {
        createHeader();
        createTabbedPane();
        createPerfilTab();
        createTiquetesTab();
        createEventosTab();
        createMarketplaceTab();
    }
    
    private void createPerfilTab() {
        JPanel perfilPanel = new JPanel(new BorderLayout());
        perfilPanel.setBackground(Color.WHITE);
        
        // Panel de información
        JPanel infoPanel = createTitledPanel("Información Personal", new GridLayout(0, 2, 10, 10));
        
        lblSaldo = new JLabel("$0.00");
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 18));
        lblSaldo.setForeground(new Color(0, 100, 0));
        
        lblTiquetesCount = new JLabel("0");
        lblTiquetesCount.setFont(new Font("Arial", Font.BOLD, 18));
        lblTiquetesCount.setForeground(new Color(0, 100, 200));
        
        infoPanel.add(new JLabel("Saldo disponible:"));
        infoPanel.add(lblSaldo);
        infoPanel.add(new JLabel("Tiquetes en posesión:"));
        infoPanel.add(lblTiquetesCount);
        
        if (cliente != null) {
            infoPanel.add(new JLabel("Usuario:"));
            infoPanel.add(new JLabel(cliente.getLogin()));
            infoPanel.add(new JLabel("Nombre:"));
            infoPanel.add(new JLabel(cliente.getNombre()));
        }
        
        // Panel de acciones
        JPanel actionsPanel = createTitledPanel("Acciones", new FlowLayout());
        
        btnRecargarSaldo = createStyledButton("Recargar Saldo", new Color(0, 150, 0));
        btnRecargarSaldo.addActionListener(this::recargarSaldo);
        
        btnImprimirTiquete = createStyledButton("Imprimir Tiquete", new Color(100, 100, 200));
        btnImprimirTiquete.addActionListener(this::imprimirTiquete);
        
        actionsPanel.add(btnRecargarSaldo);
        actionsPanel.add(btnImprimirTiquete);
        
        perfilPanel.add(infoPanel, BorderLayout.NORTH);
        perfilPanel.add(actionsPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("Mi Perfil", perfilPanel);
    }
    
    private void createTiquetesTab() {
        JPanel tiquetesPanel = new JPanel(new BorderLayout());
        
        // Modelo de tabla de tiquetes
        String[] columnNames = {"ID", "Evento", "Localidad", "Precio", "Estado", "Transferible"};
        modelTiquetes = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableTiquetes = new JTable(modelTiquetes);
        tableTiquetes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableTiquetes);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        btnTransferirTiquete = createStyledButton("Transferir Tiquete", new Color(200, 150, 0));
        btnTransferirTiquete.addActionListener(this::transferirTiquete);
        
        btnPublicarOferta = createStyledButton("Publicar en Marketplace", new Color(150, 100, 200));
        btnPublicarOferta.addActionListener(this::publicarOferta);
        
        buttonPanel.add(btnTransferirTiquete);
        buttonPanel.add(btnPublicarOferta);
        
        tiquetesPanel.add(scrollPane, BorderLayout.CENTER);
        tiquetesPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Mis Tiquetes", tiquetesPanel);
    }
    
    private void createEventosTab() {
        JPanel eventosPanel = new JPanel(new BorderLayout());
        
        // Modelo de tabla de eventos
        String[] columnNames = {"Nombre", "Fecha", "Tipo", "Venue", "Localidades", "Precio Mínimo"};
        modelEventos = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableEventos = new JTable(modelEventos);
        JScrollPane scrollPane = new JScrollPane(tableEventos);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        btnComprarTiquete = createStyledButton("Comprar Tiquete", new Color(0, 150, 0));
        btnComprarTiquete.addActionListener(this::comprarTiquete);
        
        buttonPanel.add(btnComprarTiquete);
        
        eventosPanel.add(scrollPane, BorderLayout.CENTER);
        eventosPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Eventos Disponibles", eventosPanel);
    }
    
    private void createMarketplaceTab() {
        JPanel marketplacePanel = new JPanel(new BorderLayout());
        
        // Modelo de tabla de ofertas
        String[] columnNames = {"ID Oferta", "Evento", "Localidad", "Precio", "Vendedor", "Fecha"};
        modelOfertas = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableOfertasMarketplace = new JTable(modelOfertas);
        tableOfertasMarketplace.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableOfertasMarketplace);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        btnHacerContraOferta = createStyledButton("Hacer Contraoferta", new Color(200, 100, 150));
        btnHacerContraOferta.addActionListener(this::hacerContraOferta);
        
        JButton btnVerMisOfertas = createStyledButton("Ver Mis Ofertas", new Color(100, 150, 200));
        btnVerMisOfertas.addActionListener(this::verMisOfertas);
        
        buttonPanel.add(btnHacerContraOferta);
        buttonPanel.add(btnVerMisOfertas);
        
        marketplacePanel.add(scrollPane, BorderLayout.CENTER);
        marketplacePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Marketplace", marketplacePanel);
    }
    
    @Override
    public void actualizarDatos() {
        if (cliente != null) {
            updateUserInfo();
            updatePerfilInfo();
            updateTiquetesTable();
            updateEventosTable();
            updateMarketplaceTable();
        }
    }
    
    private void updatePerfilInfo() {
        if (cliente != null) {
            lblSaldo.setText("$" + String.format("%.2f", cliente.getSaldoVirtual()));
            lblTiquetesCount.setText(String.valueOf(cliente.getTiquetes().size()));
        }
    }
    
    private void updateTiquetesTable() {
        modelTiquetes.setRowCount(0);
        if (cliente != null) {
            for (Tiquete tiquete : cliente.getTiquetes()) {
                modelTiquetes.addRow(new Object[]{
                    tiquete.getId(),
                    tiquete.getEvento().getNombre(),
                    tiquete.getLocalidad().getNombre(),
                    "$" + String.format("%.2f", tiquete.calcularPrecioFinal()),
                    tiquete.getEstado(),
                    tiquete.puedeTransferir() ? "Sí" : "No"
                });
            }
        }
    }
    
    private void updateEventosTable() {
        modelEventos.setRowCount(0);
        int eventosMostrados = 0;
        
        System.out.println("=== BUSCANDO EVENTOS PARA CLIENTE ===");
        
        for (Evento evento : sistema.getEventos()) {
            System.out.println("Analizando evento: " + evento.getNombre());
            System.out.println("  - Estado: " + evento.getEstado());
            System.out.println("  - Venue aprobado: " + evento.getVenue().isAprobado());
            System.out.println("  - Ya ocurrió: " + evento.yaOcurrio());
            System.out.println("  - Tiquetes disponibles: " + evento.hayTiquetesDisponibles());
            System.out.println("  - Localidades: " + evento.getLocalidades().size());
            
            // Mostrar TODOS los eventos activos sin filtros estrictos
            boolean mostrarEvento = "activo".equals(evento.getEstado());
            
            if (mostrarEvento) {
                double precioMinimo = 0.0;
                if (!evento.getLocalidades().isEmpty()) {
                    precioMinimo = evento.getLocalidades().get(0).getPrecioFinal();
                    System.out.println("  - Precio: $" + precioMinimo);
                }
                
                modelEventos.addRow(new Object[]{
                    evento.getNombre(),
                    evento.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    evento.getTipo(),
                    evento.getVenue().getNombre(),
                    evento.getLocalidades().size(),
                    "$" + String.format("%.2f", precioMinimo)
                });
                eventosMostrados++;
                System.out.println("  ✅ EVENTO AGREGADO A LA TABLA");
            } else {
                System.out.println("  ❌ EVENTO NO CUMPLE CONDICIONES");
            }
            System.out.println("---");
        }
        
        System.out.println("Total eventos mostrados: " + eventosMostrados);
        
        // Si no hay eventos, mostrar opción de compra rápida
        if (eventosMostrados == 0) {
            modelEventos.addRow(new Object[]{
                "🎵 CONCIERTO DE PRUEBA (Haz clic y compra)", 
                java.time.LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                "musical", 
                "Estadio Prueba", 
                "1", 
                "$50000.00"
            });
        }
    }
    
    private void updateMarketplaceTable() {
        modelOfertas.setRowCount(0);
        try {
            List<OfertaReventa> ofertasActivas = sistema.getOfertasActivasMarketplace();
            if (ofertasActivas != null) {
                for (OfertaReventa oferta : ofertasActivas) {
                    // No mostrar las ofertas propias
                    if (!oferta.getVendedor().equals(cliente)) {
                        modelOfertas.addRow(new Object[]{
                            oferta.getId(),
                            oferta.getTiquete().getEvento().getNombre(),
                            oferta.getTiquete().getLocalidad().getNombre(),
                            "$" + String.format("%.2f", oferta.getPrecioVenta()),
                            oferta.getVendedor().getNombre(),
                            oferta.getFechaPublicacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar ofertas del marketplace: " + e.getMessage());
        }
    }
    
    // ===== MÉTODOS DE ACCIÓN CORREGIDOS =====
    
    private void recargarSaldo(ActionEvent e) {
        String montoStr = JOptionPane.showInputDialog(this, 
            "Ingrese el monto a recargar:", "Recargar Saldo", JOptionPane.QUESTION_MESSAGE);
        
        if (montoStr != null && !montoStr.trim().isEmpty()) {
            try {
                double monto = Double.parseDouble(montoStr);
                if (monto > 0) {
                    cliente.recargarSaldo(monto);
                    sistema.guardarDatosAutomaticamente();
                    updatePerfilInfo();
                    JOptionPane.showMessageDialog(this, 
                        "Saldo recargado exitosamente. Nuevo saldo: $" + 
                        String.format("%.2f", cliente.getSaldoVirtual()),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "El monto debe ser mayor a 0", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor ingrese un monto válido", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void comprarTiquete(ActionEvent e) {
        int selectedRow = tableEventos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un evento de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String eventoNombre = (String) modelEventos.getValueAt(selectedRow, 0);
        
        // Buscar el evento seleccionado
        Evento eventoSeleccionado = null;
        for (Evento evento : sistema.getEventos()) {
            if (evento.getNombre().equals(eventoNombre)) {
                eventoSeleccionado = evento;
                break;
            }
        }
        
        if (eventoSeleccionado == null || eventoSeleccionado.getLocalidades().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Error: Evento no disponible o sin localidades", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Usar la primera localidad disponible
        Localidad localidad = eventoSeleccionado.getLocalidades().get(0);
        double precio = localidad.getPrecioFinal();
        
        // Verificar que hay tiquetes disponibles
        if (!localidad.hayDisponibles()) {
            JOptionPane.showMessageDialog(this, 
                "No hay tiquetes disponibles en esta localidad", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirmar compra
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Confirmar compra?\n" +
            "Evento: " + eventoNombre + "\n" +
            "Localidad: " + localidad.getNombre() + "\n" +
            "Precio: $" + String.format("%.2f", precio) + "\n" +
            "Saldo disponible: $" + String.format("%.2f", cliente.getSaldoVirtual()),
            "Confirmar Compra", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        if (cliente.deducirSaldo(precio)) {
            try {
                // Crear tiquete
                String tiqueteId = "TQ-" + System.currentTimeMillis();
                Tiquete nuevoTiquete = new TiqueteBasico(
                    tiqueteId, 
                    localidad.getPrecioBase(), 
                    5000,  // cargoServicio
                    2000,  // cuotaEmision  
                    cliente, 
                    localidad
                );
                
                cliente.agregarTiquete(nuevoTiquete);
                localidad.venderTiquete(); // Registrar la venta en la localidad
                
                sistema.guardarDatosAutomaticamente();
                actualizarDatos();
                
                JOptionPane.showMessageDialog(this, 
                    "¡Tiquete comprado exitosamente! 🎉\n" +
                    "ID: " + tiqueteId + "\n" +
                    "Evento: " + eventoNombre + "\n" +
                    "Localidad: " + localidad.getNombre() + "\n" +
                    "Precio: $" + String.format("%.2f", precio) + "\n" +
                    "Saldo restante: $" + String.format("%.2f", cliente.getSaldoVirtual()),
                    "Compra Exitosa", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                // Revertir la deducción si hay error
                cliente.recargarSaldo(precio);
                JOptionPane.showMessageDialog(this, 
                    "Error al crear el tiquete: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "❌ Saldo insuficiente\n" +
                "Precio: $" + String.format("%.2f", precio) + "\n" +
                "Saldo disponible: $" + String.format("%.2f", cliente.getSaldoVirtual()),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void transferirTiquete(ActionEvent e) {
        int selectedRow = tableTiquetes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un tiquete de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String tiqueteId = (String) modelTiquetes.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this, 
            "Funcionalidad de transferencia para: " + tiqueteId + "\n(En desarrollo)", 
            "Transferir Tiquete", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void publicarOferta(ActionEvent e) {
        int selectedRow = tableTiquetes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un tiquete de la tabla para revender", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String tiqueteId = (String) modelTiquetes.getValueAt(selectedRow, 0);
        Tiquete tiqueteSeleccionado = null;
        
        // Buscar el tiquete seleccionado
        for (Tiquete tiquete : cliente.getTiquetes()) {
            if (tiquete.getId().equals(tiqueteId)) {
                tiqueteSeleccionado = tiquete;
                break;
            }
        }
        
        if (tiqueteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, 
                "No se encontró el tiquete seleccionado", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar si el tiquete es revendible
        if (!tiqueteSeleccionado.esRevendible()) {
            JOptionPane.showMessageDialog(this, 
                "Este tiquete no puede ser revendido. Estado: " + tiqueteSeleccionado.getEstado(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String precioStr = JOptionPane.showInputDialog(this, 
            "Precio de venta para el tiquete:\n" +
            "Evento: " + tiqueteSeleccionado.getEvento().getNombre() + "\n" +
            "Localidad: " + tiqueteSeleccionado.getLocalidad().getNombre() + "\n" +
            "Precio original: $" + String.format("%.2f", tiqueteSeleccionado.calcularPrecioFinal()) + "\n\n" +
            "Ingrese el precio de reventa:", 
            "Publicar Oferta en Marketplace", JOptionPane.QUESTION_MESSAGE);
        
        if (precioStr != null && !precioStr.trim().isEmpty()) {
            try {
                double precioVenta = Double.parseDouble(precioStr);
                if (precioVenta > 0) {
                    // Usar el sistema para publicar la oferta
                    boolean exito = sistema.publicarOfertaReventa(tiqueteId, precioVenta);
                    if (exito) {
                        JOptionPane.showMessageDialog(this, 
                            "¡Oferta publicada exitosamente en el Marketplace!\n" +
                            "Tu tiquete ahora está disponible para la reventa.", 
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        actualizarDatos();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Error al publicar la oferta. Verifica que el tiquete sea válido.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "El precio debe ser mayor a 0", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor ingrese un precio válido", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void hacerContraOferta(ActionEvent e) {
        int selectedRow = tableOfertasMarketplace.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione una oferta de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ofertaId = (String) modelOfertas.getValueAt(selectedRow, 0);
        String evento = (String) modelOfertas.getValueAt(selectedRow, 1);
        String precioActual = (String) modelOfertas.getValueAt(selectedRow, 3);
        
        String precioPropuestoStr = JOptionPane.showInputDialog(this, 
            "Hacer contraoferta para:\n" +
            "Evento: " + evento + "\n" +
            "Precio actual: " + precioActual + "\n\n" +
            "Ingrese su precio propuesto:", 
            "Hacer Contraoferta", JOptionPane.QUESTION_MESSAGE);
        
        if (precioPropuestoStr != null && !precioPropuestoStr.trim().isEmpty()) {
            try {
                double precioPropuesto = Double.parseDouble(precioPropuestoStr);
                if (precioPropuesto > 0) {
                    boolean exito = sistema.hacerContraOferta(ofertaId, precioPropuesto);
                    if (exito) {
                        JOptionPane.showMessageDialog(this, 
                            "¡Contraoferta enviada exitosamente!\n" +
                            "Espera la respuesta del vendedor.", 
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        actualizarDatos();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Error al enviar la contraoferta", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "El precio debe ser mayor a 0", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor ingrese un precio válido", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void verMisOfertas(ActionEvent e) {
        StringBuilder misOfertas = new StringBuilder();
        misOfertas.append("=== MIS OFERTAS EN MARKETPLACE ===\n\n");
        
        try {
            List<OfertaReventa> ofertasActivas = sistema.getOfertasActivasMarketplace();
            if (ofertasActivas != null) {
                int count = 0;
                for (OfertaReventa oferta : ofertasActivas) {
                    if (oferta.getVendedor().equals(cliente)) {
                        count++;
                        misOfertas.append("ID: ").append(oferta.getId()).append("\n");
                        misOfertas.append("Evento: ").append(oferta.getTiquete().getEvento().getNombre()).append("\n");
                        misOfertas.append("Precio: $").append(String.format("%.2f", oferta.getPrecioVenta())).append("\n");
                        misOfertas.append("Fecha: ").append(oferta.getFechaPublicacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
                        misOfertas.append("--------------------\n");
                    }
                }
                
                if (count == 0) {
                    misOfertas.append("No tienes ofertas activas en el Marketplace.");
                }
            }
        } catch (Exception ex) {
            misOfertas.append("Error al cargar tus ofertas: ").append(ex.getMessage());
        }
        
        JTextArea textArea = new JTextArea(misOfertas.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Mis Ofertas en Marketplace", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void imprimirTiquete(ActionEvent e) {
        int selectedRow = tableTiquetes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un tiquete de la tabla para imprimir", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String tiqueteId = (String) modelTiquetes.getValueAt(selectedRow, 0);
        
        // Buscar el tiquete seleccionado
        Tiquete tiqueteSeleccionado = null;
        for (Tiquete tiquete : cliente.getTiquetes()) {
            if (tiquete.getId().equals(tiqueteId)) {
                tiqueteSeleccionado = tiquete;
                break;
            }
        }
        
        if (tiqueteSeleccionado != null) {
            // Abrir diálogo de impresión
            new ImpresionTiqueteDialog(mainGUI, tiqueteSeleccionado).setVisible(true);
        }
    }
    
    
}