package gui;

import sistema.SistemaVentaTiquetes;
import usuarios.Administrador;
import usuarios.Usuario;
import usuarios.Organizador;
import eventos.Evento;
import eventos.Venue;
import finanzas.ConfiguracionCargos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Panel de interfaz gráfica para administradores
 */
public class AdministradorPanel extends UserPanel {
    private Administrador administrador;
    
    // Componentes de la interfaz
    private JLabel lblGananciasTotales;
    private JLabel lblUsuariosCount;
    private JLabel lblEventosCount;
    
    private JTable tableVenuesPendientes;
    private JTable tableEventos;
    private JTable tableOfertasMarketplace;
    private DefaultTableModel modelVenuesPendientes;
    private DefaultTableModel modelEventos;
    private DefaultTableModel modelOfertas;
    
    // Botones principales
    private JButton btnAprobarVenue;
    private JButton btnRechazarVenue;
    private JButton btnCancelarEvento;
    private JButton btnConfigurarCargos;
    private JButton btnEliminarOferta;
    
    public AdministradorPanel(MainGUI mainGUI, SistemaVentaTiquetes sistema) {
        super(mainGUI, sistema);
        initializePanel();
    }
    
    private void initializePanel() {
        createHeader();
        createTabbedPane();
        createDashboardTab();
        createVenuesTab();
        createEventosTab();
        createMarketplaceTab();
        createConfiguracionTab();
    }
    
    private void createDashboardTab() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(Color.WHITE);
        
        // Panel de estadísticas
        JPanel statsPanel = createTitledPanel("Estadísticas del Sistema", new GridLayout(2, 2, 15, 15));
        
        lblGananciasTotales = new JLabel("$0.00", JLabel.CENTER);
        lblGananciasTotales.setFont(new Font("Arial", Font.BOLD, 24));
        lblGananciasTotales.setForeground(new Color(0, 150, 0));
        
        lblUsuariosCount = new JLabel("0", JLabel.CENTER);
        lblUsuariosCount.setFont(new Font("Arial", Font.BOLD, 24));
        lblUsuariosCount.setForeground(new Color(0, 100, 200));
        
        lblEventosCount = new JLabel("0", JLabel.CENTER);
        lblEventosCount.setFont(new Font("Arial", Font.BOLD, 24));
        lblEventosCount.setForeground(new Color(200, 100, 0));
        
        JLabel lblVenuesCount = new JLabel(String.valueOf(sistema.getVenues().size()), JLabel.CENTER);
        lblVenuesCount.setFont(new Font("Arial", Font.BOLD, 24));
        lblVenuesCount.setForeground(new Color(150, 100, 200));
        
        statsPanel.add(createStatCard("Ganancias Totales", lblGananciasTotales));
        statsPanel.add(createStatCard("Usuarios", lblUsuariosCount));
        statsPanel.add(createStatCard("Eventos", lblEventosCount));
        statsPanel.add(createStatCard("Venues", lblVenuesCount));
        
        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("Dashboard", dashboardPanel);
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);
        
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void createVenuesTab() {
        JPanel venuesPanel = new JPanel(new BorderLayout());
        
        // Modelo de tabla de venues pendientes
        String[] columnNames = {"Nombre", "Ubicación", "Capacidad", "Restricciones", "Sugerido por"};
        modelVenuesPendientes = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableVenuesPendientes = new JTable(modelVenuesPendientes);
        tableVenuesPendientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableVenuesPendientes);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        btnAprobarVenue = createStyledButton("Aprobar Venue", new Color(0, 150, 0));
        btnAprobarVenue.addActionListener(this::aprobarVenue);
        
        btnRechazarVenue = createStyledButton("Rechazar Venue", new Color(200, 80, 80));
        btnRechazarVenue.addActionListener(this::rechazarVenue);
        
        buttonPanel.add(btnAprobarVenue);
        buttonPanel.add(btnRechazarVenue);
        
        venuesPanel.add(scrollPane, BorderLayout.CENTER);
        venuesPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Venues Pendientes", venuesPanel);
    }
    
    private void createEventosTab() {
        JPanel eventosPanel = new JPanel(new BorderLayout());
        
        // Modelo de tabla de eventos
        String[] columnNames = {"Nombre", "Fecha", "Tipo", "Organizador", "Venue", "Vendidos", "Estado"};
        modelEventos = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableEventos = new JTable(modelEventos);
        tableEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableEventos);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        btnCancelarEvento = createStyledButton("Cancelar Evento", new Color(200, 80, 80));
        btnCancelarEvento.addActionListener(this::cancelarEvento);
        
        JButton btnVerDetalles = createStyledButton("Ver Detalles", new Color(100, 100, 200));
        btnVerDetalles.addActionListener(this::verDetallesEvento);
        
        buttonPanel.add(btnCancelarEvento);
        buttonPanel.add(btnVerDetalles);
        
        eventosPanel.add(scrollPane, BorderLayout.CENTER);
        eventosPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Gestión de Eventos", eventosPanel);
    }
    
    private void createMarketplaceTab() {
        JPanel marketplacePanel = new JPanel(new BorderLayout());
        
        // Modelo de tabla de ofertas
        String[] columnNames = {"ID Oferta", "Evento", "Vendedor", "Precio", "Fecha"};
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
        
        btnEliminarOferta = createStyledButton("Eliminar Oferta", new Color(200, 80, 80));
        btnEliminarOferta.addActionListener(this::eliminarOferta);
        
        JButton btnVerLogs = createStyledButton("Ver Logs", new Color(100, 100, 200));
        btnVerLogs.addActionListener(this::verLogsMarketplace);
        
        buttonPanel.add(btnEliminarOferta);
        buttonPanel.add(btnVerLogs);
        
        marketplacePanel.add(scrollPane, BorderLayout.CENTER);
        marketplacePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Moderar Marketplace", marketplacePanel);
    }
    
    private void createConfiguracionTab() {
        JPanel configPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        configPanel.setBackground(Color.WHITE);
        
        // Campos para configuración de cargos
        JSpinner spnCargoMusical = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 50.0, 0.5));
        JSpinner spnCargoCultural = new JSpinner(new SpinnerNumberModel(8.0, 0.0, 50.0, 0.5));
        JSpinner spnCargoDeportivo = new JSpinner(new SpinnerNumberModel(12.0, 0.0, 50.0, 0.5));
        JSpinner spnCargoReligioso = new JSpinner(new SpinnerNumberModel(5.0, 0.0, 50.0, 0.5));
        JSpinner spnCuotaEmision = new JSpinner(new SpinnerNumberModel(5000.0, 0.0, 20000.0, 500.0));
        
        configPanel.add(new JLabel("Cargo Musical (%):"));
        configPanel.add(spnCargoMusical);
        configPanel.add(new JLabel("Cargo Cultural (%):"));
        configPanel.add(spnCargoCultural);
        configPanel.add(new JLabel("Cargo Deportivo (%):"));
        configPanel.add(spnCargoDeportivo);
        configPanel.add(new JLabel("Cargo Religioso (%):"));
        configPanel.add(spnCargoReligioso);
        configPanel.add(new JLabel("Cuota de Emisión ($):"));
        configPanel.add(spnCuotaEmision);
        
        btnConfigurarCargos = createStyledButton("Actualizar Cargos", new Color(0, 150, 0));
        btnConfigurarCargos.addActionListener(e -> actualizarCargos(
            (Double) spnCargoMusical.getValue(),
            (Double) spnCargoCultural.getValue(),
            (Double) spnCargoDeportivo.getValue(),
            (Double) spnCargoReligioso.getValue(),
            (Double) spnCuotaEmision.getValue()
        ));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnConfigurarCargos);
        
        JPanel mainConfigPanel = new JPanel(new BorderLayout());
        mainConfigPanel.add(configPanel, BorderLayout.CENTER);
        mainConfigPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Configuración", mainConfigPanel);
    }
    
    private void updateDashboard() {
        lblGananciasTotales.setText("$" + String.format("%.2f", administrador.verGananciaTotal()));
        lblUsuariosCount.setText(String.valueOf(sistema.getUsuarios().size()));
        lblEventosCount.setText(String.valueOf(sistema.getEventos().size()));
    }
    
    private void updateVenuesPendientesTable() {
        modelVenuesPendientes.setRowCount(0);
        for (Venue venue : sistema.getVenuesPendientes()) {
            String sugerente = venue.getOrganizadorSugerente() != null ? 
                venue.getOrganizadorSugerente().getNombre() : "N/A";
            
            modelVenuesPendientes.addRow(new Object[]{
                venue.getNombre(),
                venue.getUbicacion(),
                venue.getCapacidadMaxima(),
                venue.getRestricciones(),
                sugerente
            });
        }
    }
    
    private void updateEventosTable() {
        modelEventos.setRowCount(0);
        for (Evento evento : sistema.getEventos()) {
            modelEventos.addRow(new Object[]{
                evento.getNombre(),
                evento.getFechaHora().toString(),
                evento.getTipo(),
                evento.getOrganizador().getNombre(),
                evento.getVenue().getNombre(),
                evento.getTiquetesVendidosTotal() + "/" + evento.getCapacidadTotal(),
                evento.getEstado()
            });
        }
    }
    
    private void updateMarketplaceTable() {
        modelOfertas.setRowCount(0);
        // Implementar lógica para cargar ofertas del marketplace
    }
    
    // ===== MÉTODOS DE ACCIÓN =====
    
    private void aprobarVenue(ActionEvent e) {
        int selectedRow = tableVenuesPendientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un venue de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String venueNombre = (String) modelVenuesPendientes.getValueAt(selectedRow, 0);
        Venue venueSeleccionado = null;
        
        // Buscar el venue seleccionado
        for (Venue venue : sistema.getVenuesPendientes()) {
            if (venue.getNombre().equals(venueNombre)) {
                venueSeleccionado = venue;
                break;
            }
        }
        
        if (venueSeleccionado == null) {
            JOptionPane.showMessageDialog(this, 
                "No se encontró el venue seleccionado", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de aprobar el venue?\n" +
            "Nombre: " + venueSeleccionado.getNombre() + "\n" +
            "Ubicación: " + venueSeleccionado.getUbicacion() + "\n" +
            "Capacidad: " + venueSeleccionado.getCapacidadMaxima() + " personas\n" +
            "Una vez aprobado, los organizadores podrán usarlo para eventos.",
            "Confirmar Aprobación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = sistema.aprobarVenue(venueSeleccionado.getId());
            if (exito) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Venue '" + venueSeleccionado.getNombre() + "' aprobado exitosamente\n" +
                    "Ahora está disponible para crear eventos.",
                    "Venue Aprobado", JOptionPane.INFORMATION_MESSAGE);
                actualizarDatos();
                sistema.guardarDatosAutomaticamente();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al aprobar el venue", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void rechazarVenue(ActionEvent e) {
        int selectedRow = tableVenuesPendientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un venue de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String venueNombre = (String) modelVenuesPendientes.getValueAt(selectedRow, 0);
        String motivo = JOptionPane.showInputDialog(this, 
            "Motivo del rechazo para: " + venueNombre, "Motivo del Rechazo");
        
        if (motivo != null && !motivo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Venue '" + venueNombre + "' rechazado\nMotivo: " + motivo, 
                "Venue Rechazado", JOptionPane.INFORMATION_MESSAGE);
            actualizarDatos();
            sistema.guardarDatosAutomaticamente();
        }
    }
    
    private void cancelarEvento(ActionEvent e) {
        int selectedRow = tableEventos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un evento de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String eventoNombre = (String) modelEventos.getValueAt(selectedRow, 0);
        String motivo = JOptionPane.showInputDialog(this, 
            "Motivo de la cancelación para: " + eventoNombre, "Motivo de Cancelación");
        
        if (motivo != null && !motivo.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de cancelar el evento: " + eventoNombre + "?\n" +
                "Se procesarán reembolsos automáticamente.", 
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, 
                    "Evento '" + eventoNombre + "' cancelado exitosamente\n" +
                    "Proceso de reembolsos iniciado", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarDatos();
                sistema.guardarDatosAutomaticamente();
            }
        }
    }
    
    private void verDetallesEvento(ActionEvent e) {
        int selectedRow = tableEventos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un evento de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String eventoNombre = (String) modelEventos.getValueAt(selectedRow, 0);
        Evento eventoSeleccionado = null;
        
        for (Evento evento : sistema.getEventos()) {
            if (evento.getNombre().equals(eventoNombre)) {
                eventoSeleccionado = evento;
                break;
            }
        }
        
        if (eventoSeleccionado != null) {
            String resumen = sistema.generarResumenEvento(eventoSeleccionado);
            JTextArea textArea = new JTextArea(resumen);
            textArea.setEditable(false);
            textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, 
                "Detalles del Evento: " + eventoNombre, JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void eliminarOferta(ActionEvent e) {
        int selectedRow = tableOfertasMarketplace.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione una oferta de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ofertaId = (String) modelOfertas.getValueAt(selectedRow, 0);
        String motivo = JOptionPane.showInputDialog(this, 
            "Motivo de la eliminación para oferta: " + ofertaId, "Motivo de Eliminación");
        
        if (motivo != null && !motivo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Oferta " + ofertaId + " eliminada por moderación\nMotivo: " + motivo, 
                "Oferta Eliminada", JOptionPane.INFORMATION_MESSAGE);
            actualizarDatos();
            sistema.guardarDatosAutomaticamente();
        }
    }
    
    private void verLogsMarketplace(ActionEvent e) {
        String logs = sistema.getLogMarketplace();
        JTextArea textArea = new JTextArea(logs);
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Logs del Marketplace", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void actualizarCargos(double musical, double cultural, double deportivo, double religioso, double cuota) {
        if (administrador != null) {
            administrador.actualizarCargoMusical(musical);
            administrador.actualizarCargoCultural(cultural);
            administrador.actualizarCargoDeportivo(deportivo);
            administrador.actualizarCargoReligioso(religioso);
            administrador.actualizarCuotaEmision(cuota);
            
            JOptionPane.showMessageDialog(this, 
                "Configuración de cargos actualizada exitosamente:\n" +
                "Musical: " + musical + "%\n" +
                "Cultural: " + cultural + "%\n" +
                "Deportivo: " + deportivo + "%\n" +
                "Religioso: " + religioso + "%\n" +
                "Cuota Emisión: $" + String.format("%.2f", cuota),
                "Configuración Actualizada", JOptionPane.INFORMATION_MESSAGE);
            
            sistema.guardarDatosAutomaticamente();
        }
    }
    
    public void setAdministradorActual(Administrador administrador) {
        this.administrador = administrador;
        this.usuarioActual = administrador;
    }
    
    @Override
    public void actualizarDatos() {
        if (administrador != null) {
            updateUserInfo();
            updateDashboard();
            updateVenuesPendientesTable();
            updateEventosTable();
            updateMarketplaceTable();
        }
    }
}