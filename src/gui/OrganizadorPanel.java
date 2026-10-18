package gui;

import sistema.SistemaVentaTiquetes;

import usuarios.Organizador;
import usuarios.Usuario;
import eventos.Evento;
import eventos.Venue;
import eventos.Localidad;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel de interfaz gráfica para organizadores
 */
public class OrganizadorPanel extends UserPanel {
    private Organizador organizador;
    
    // Componentes de la interfaz
    private JLabel lblEventosCount;
    private JLabel lblGanancias;
    private JLabel lblPorcentajeVentas;
    
    private JTable tableEventos;
    private JTable tableVenues;
    private DefaultTableModel modelEventos;
    private DefaultTableModel modelVenues;
    
    // Botones principales
    private JButton btnCrearEvento;
    private JButton btnAgregarLocalidad;
    private JButton btnCrearOferta;
    private JButton btnSugerirVenue;
    private JButton btnVerReportes;
    
    public OrganizadorPanel(MainGUI mainGUI, SistemaVentaTiquetes sistema) {
        super(mainGUI, sistema);
        initializePanel();
    }
    
    private void initializePanel() {
        createHeader();
        createTabbedPane();
        createDashboardTab();
        createEventosTab();
        createVenuesTab();
        createReportesTab();
    }
    
    private void createDashboardTab() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(Color.WHITE);
        
        // Panel de estadísticas
        JPanel statsPanel = createTitledPanel("Estadísticas del Organizador", new GridLayout(2, 2, 15, 15));
        
        lblEventosCount = new JLabel("0", JLabel.CENTER);
        lblEventosCount.setFont(new Font("Arial", Font.BOLD, 24));
        lblEventosCount.setForeground(new Color(0, 100, 200));
        
        lblGanancias = new JLabel("$0.00", JLabel.CENTER);
        lblGanancias.setFont(new Font("Arial", Font.BOLD, 24));
        lblGanancias.setForeground(new Color(0, 150, 0));
        
        lblPorcentajeVentas = new JLabel("0%", JLabel.CENTER);
        lblPorcentajeVentas.setFont(new Font("Arial", Font.BOLD, 24));
        lblPorcentajeVentas.setForeground(new Color(200, 100, 0));
        
        JLabel lblCapacidad = new JLabel("-/-", JLabel.CENTER);
        lblCapacidad.setFont(new Font("Arial", Font.BOLD, 24));
        lblCapacidad.setForeground(new Color(150, 100, 200));
        
        statsPanel.add(createStatCard("Eventos Activos", lblEventosCount));
        statsPanel.add(createStatCard("Ganancias Totales", lblGanancias));
        statsPanel.add(createStatCard("% Ventas Global", lblPorcentajeVentas));
        statsPanel.add(createStatCard("Capacidad", lblCapacidad));
        
        // Panel de acciones rápidas
        JPanel quickActionsPanel = createTitledPanel("Acciones Rápidas", new FlowLayout());
        
        btnCrearEvento = createStyledButton("Crear Evento", new Color(0, 150, 0));
        btnCrearEvento.addActionListener(this::crearEvento);
        
        btnSugerirVenue = createStyledButton("Sugerir Venue", new Color(100, 100, 200));
        btnSugerirVenue.addActionListener(this::sugerirVenue);
        
        btnVerReportes = createStyledButton("Ver Reportes", new Color(200, 150, 0));
        btnVerReportes.addActionListener(this::verReportes);
        
        quickActionsPanel.add(btnCrearEvento);
        quickActionsPanel.add(btnSugerirVenue);
        quickActionsPanel.add(btnVerReportes);
        
        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        dashboardPanel.add(quickActionsPanel, BorderLayout.SOUTH);
        
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
    
    private void createEventosTab() {
        JPanel eventosPanel = new JPanel(new BorderLayout());
        
        // Modelo de tabla de eventos
        String[] columnNames = {"Nombre", "Fecha", "Tipo", "Venue", "Localidades", "Vendidos", "Ganancias", "Estado"};
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
        
        btnAgregarLocalidad = createStyledButton("Agregar Localidad", new Color(0, 100, 200));
        btnAgregarLocalidad.addActionListener(this::agregarLocalidad);
        
        btnCrearOferta = createStyledButton("Crear Oferta", new Color(200, 100, 150));
        btnCrearOferta.addActionListener(this::crearOferta);
        
        JButton btnCancelarEvento = createStyledButton("Solicitar Cancelación", new Color(200, 80, 80));
        btnCancelarEvento.addActionListener(this::solicitarCancelacion);
        
        buttonPanel.add(btnAgregarLocalidad);
        buttonPanel.add(btnCrearOferta);
        buttonPanel.add(btnCancelarEvento);
        
        eventosPanel.add(scrollPane, BorderLayout.CENTER);
        eventosPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Mis Eventos", eventosPanel);
    }
    
    private void createVenuesTab() {
        JPanel venuesPanel = new JPanel(new BorderLayout());
        
        // Modelo de tabla de venues
        String[] columnNames = {"Nombre", "Ubicación", "Capacidad", "Restricciones", "Estado"};
        modelVenues = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableVenues = new JTable(modelVenues);
        JScrollPane scrollPane = new JScrollPane(tableVenues);
        
        venuesPanel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Venues Disponibles", venuesPanel);
    }
    
    private void createReportesTab() {
        JPanel reportesPanel = new JPanel(new BorderLayout());
        reportesPanel.setBackground(Color.WHITE);
        
        // Área de texto para reportes
        JTextArea txtReportes = new JTextArea();
        txtReportes.setEditable(false);
        txtReportes.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtReportes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(txtReportes);
        
        // Botones de reportes
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnReporteGeneral = createStyledButton("Reporte General", new Color(100, 100, 200));
        btnReporteGeneral.addActionListener(e -> generarReporteGeneral(txtReportes));
        
        JButton btnReporteEvento = createStyledButton("Reporte por Evento", new Color(150, 100, 200));
        btnReporteEvento.addActionListener(e -> generarReportePorEvento(txtReportes));
        
        JButton btnEstadisticas = createStyledButton("Estadísticas", new Color(200, 100, 150));
        btnEstadisticas.addActionListener(e -> generarEstadisticas(txtReportes));
        
        buttonPanel.add(btnReporteGeneral);
        buttonPanel.add(btnReporteEvento);
        buttonPanel.add(btnEstadisticas);
        
        reportesPanel.add(scrollPane, BorderLayout.CENTER);
        reportesPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Reportes", reportesPanel);
    }
    
    private void updateDashboard() {
        if (organizador != null) {
            List<Evento> eventos = organizador.getEventosActivos();
            lblEventosCount.setText(String.valueOf(eventos.size()));
            lblGanancias.setText("$" + String.format("%.2f", organizador.calcularGananciasTotales()));
            lblPorcentajeVentas.setText(String.format("%.1f%%", organizador.calcularPorcentajeVentasGlobal()));
        }
    }
    
    private void updateEventosTable() {
        modelEventos.setRowCount(0);
        if (organizador != null) {
            for (Evento evento : organizador.getEventosActivos()) {
                modelEventos.addRow(new Object[]{
                    evento.getNombre(),
                    evento.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    evento.getTipo(),
                    evento.getVenue().getNombre(),
                    evento.getLocalidades().size(),
                    evento.getTiquetesVendidosTotal() + "/" + evento.getCapacidadTotal(),
                    "$" + String.format("%.2f", evento.calcularGananciasOrganizador()),
                    evento.getEstado()
                });
            }
        }
    }
    
    private void updateVenuesTable() {
        modelVenues.setRowCount(0);
        for (Venue venue : sistema.getVenues()) {
            if (venue.isAprobado()) {
                modelVenues.addRow(new Object[]{
                    venue.getNombre(),
                    venue.getUbicacion(),
                    venue.getCapacidadMaxima(),
                    venue.getRestricciones(),
                    "Aprobado"
                });
            }
        }
    }
    
    // ===== MÉTODOS DE ACCIÓN =====
    
    private void crearEvento(ActionEvent e) {
        JDialog dialog = new JDialog(mainGUI, "Crear Nuevo Evento", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(mainGUI);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        JTextField txtId = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtFecha = new JTextField();
        JTextField txtTipo = new JTextField();
        JComboBox<String> cmbVenue = new JComboBox<>();
        
        // Llenar combo de venues aprobados
        for (Venue venue : sistema.getVenues()) {
            if (venue.isAprobado()) {
                cmbVenue.addItem(venue.getNombre() + " - " + venue.getUbicacion());
            }
        }
        
        formPanel.add(new JLabel("ID Evento:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Fecha (dd/MM/yyyy HH:mm):"));
        formPanel.add(txtFecha);
        formPanel.add(new JLabel("Tipo:"));
        formPanel.add(txtTipo);
        formPanel.add(new JLabel("Venue:"));
        formPanel.add(cmbVenue);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnCrear = new JButton("Crear Evento");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnCrear.addActionListener(ev -> {
            // Lógica para crear evento
            JOptionPane.showMessageDialog(dialog, 
                "Evento creado exitosamente\n(En desarrollo)", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            actualizarDatos();
        });
        
        btnCancelar.addActionListener(ev -> dialog.dispose());
        
        buttonPanel.add(btnCrear);
        buttonPanel.add(btnCancelar);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void agregarLocalidad(ActionEvent e) {
        int selectedRow = tableEventos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un evento de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String eventoNombre = (String) modelEventos.getValueAt(selectedRow, 0);
        Evento eventoSeleccionado = null;
        
        // Buscar el evento seleccionado
        for (Evento evento : organizador.getEventosActivos()) {
            if (evento.getNombre().equals(eventoNombre)) {
                eventoSeleccionado = evento;
                break;
            }
        }
        
        if (eventoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, 
                "No se encontró el evento seleccionado", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Diálogo para agregar localidad
        JTextField txtNombre = new JTextField("General");
        JTextField txtPrecio = new JTextField("50000");
        JTextField txtCapacidad = new JTextField("100");
        JCheckBox chkNumerada = new JCheckBox("Localidad Numerada", false);
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"basico", "numerado"});
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Nombre Localidad:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Precio Base:"));
        panel.add(txtPrecio);
        panel.add(new JLabel("Capacidad:"));
        panel.add(txtCapacidad);
        panel.add(new JLabel("Tipo Tiquete:"));
        panel.add(cmbTipo);
        panel.add(new JLabel(""));
        panel.add(chkNumerada);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Agregar Localidad a: " + eventoNombre, JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nombreLocalidad = txtNombre.getText().trim();
                double precio = Double.parseDouble(txtPrecio.getText());
                int capacidad = Integer.parseInt(txtCapacidad.getText());
                boolean numerada = chkNumerada.isSelected();
                String tipoTiquete = (String) cmbTipo.getSelectedItem();
                
                if (nombreLocalidad.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "El nombre de la localidad no puede estar vacío", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Agregar la localidad
                organizador.agregarLocalidad(eventoSeleccionado, nombreLocalidad, precio, 
                                           numerada, capacidad, tipoTiquete);
                
                sistema.guardarDatosAutomaticamente();
                actualizarDatos();
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Localidad '" + nombreLocalidad + "' agregada exitosamente\n" +
                    "Precio: $" + String.format("%.2f", precio) + "\n" +
                    "Capacidad: " + capacidad + " personas\n" +
                    "El evento ahora está disponible para la venta",
                    "Localidad Agregada", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor ingrese valores numéricos válidos para precio y capacidad", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void crearOferta(ActionEvent e) {
        int selectedRow = tableEventos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un evento de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String eventoNombre = (String) modelEventos.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this, 
            "Crear oferta para: " + eventoNombre + "\n(En desarrollo)", 
            "Crear Oferta", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void sugerirVenue(ActionEvent e) {
        JDialog dialog = new JDialog(mainGUI, "Sugerir Nuevo Venue", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainGUI);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtId = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtUbicacion = new JTextField();
        JTextField txtCapacidad = new JTextField();
        JTextField txtRestricciones = new JTextField();
        
        formPanel.add(new JLabel("ID Venue:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Ubicación:"));
        formPanel.add(txtUbicacion);
        formPanel.add(new JLabel("Capacidad:"));
        formPanel.add(txtCapacidad);
        formPanel.add(new JLabel("Restricciones:"));
        formPanel.add(txtRestricciones);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnSugerir = new JButton("Sugerir Venue");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnSugerir.addActionListener(ev -> {
            JOptionPane.showMessageDialog(dialog, 
                "Venue sugerido exitosamente\nEsperando aprobación del administrador", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        btnCancelar.addActionListener(ev -> dialog.dispose());
        
        buttonPanel.add(btnSugerir);
        buttonPanel.add(btnCancelar);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void solicitarCancelacion(ActionEvent e) {
        int selectedRow = tableEventos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un evento de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String eventoNombre = (String) modelEventos.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this, 
            "Solicitar cancelación de: " + eventoNombre + "\n(En desarrollo)", 
            "Solicitar Cancelación", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void verReportes(ActionEvent e) {
        tabbedPane.setSelectedIndex(3); // Cambiar a pestaña de reportes
    }
    
    private void generarReporteGeneral(JTextArea txtReportes) {
        if (organizador != null) {
            StringBuilder reporte = new StringBuilder();
            reporte.append("=== REPORTE FINANCIERO GENERAL ===\n\n");
            reporte.append("Ganancias Totales: $").append(String.format("%.2f", organizador.calcularGananciasTotales())).append("\n");
            reporte.append("Porcentaje de Ventas Global: ").append(String.format("%.2f", organizador.calcularPorcentajeVentasGlobal())).append("%\n");
            reporte.append("Total Eventos Activos: ").append(organizador.getEventosActivos().size()).append("\n\n");
            
            reporte.append("--- Detalle por Evento ---\n");
            for (Evento evento : organizador.getEventosActivos()) {
                reporte.append("* ").append(evento.getNombre()).append(": $")
                      .append(String.format("%.2f", evento.calcularGananciasOrganizador()))
                      .append(" (").append(evento.getTiquetesVendidosTotal()).append(" vendidos)\n");
            }
            
            txtReportes.setText(reporte.toString());
        }
    }
    
    private void generarReportePorEvento(JTextArea txtReportes) {
        int selectedRow = tableEventos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un evento de la tabla", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String eventoNombre = (String) modelEventos.getValueAt(selectedRow, 0);
        Evento eventoSeleccionado = null;
        
        for (Evento evento : organizador.getEventosActivos()) {
            if (evento.getNombre().equals(eventoNombre)) {
                eventoSeleccionado = evento;
                break;
            }
        }
        
        if (eventoSeleccionado != null) {
            String reporte = organizador.verReportePorEvento(eventoSeleccionado);
            txtReportes.setText(reporte);
        }
    }
    
    private void generarEstadisticas(JTextArea txtReportes) {
        if (organizador != null) {
            List<Evento> eventos = organizador.getEventosActivos();
            int totalTiquetesVendidos = eventos.stream()
                    .mapToInt(Evento::getTiquetesVendidosTotal)
                    .sum();
            int totalCapacidad = eventos.stream()
                    .mapToInt(Evento::getCapacidadTotal)
                    .sum();
            
            StringBuilder stats = new StringBuilder();
            stats.append("=== ESTADÍSTICAS GENERALES ===\n\n");
            stats.append("Eventos activos: ").append(eventos.size()).append("\n");
            stats.append("Tiquetes vendidos: ").append(totalTiquetesVendidos).append("\n");
            stats.append("Capacidad total: ").append(totalCapacidad).append("\n");
            stats.append("Ocupación promedio: ").append(
                totalCapacidad > 0 ? String.format("%.1f", (totalTiquetesVendidos * 100.0 / totalCapacidad)) : "0"
            ).append("%\n");
            stats.append("Ganancias totales: $").append(String.format("%.2f", organizador.calcularGananciasTotales())).append("\n");
            
            txtReportes.setText(stats.toString());
        }
    }
    
    public void setOrganizadorActual(Organizador organizador) {
        this.organizador = organizador;
        this.usuarioActual = organizador;
    }
    
    @Override
    public void actualizarDatos() {
        if (organizador != null) {
            updateUserInfo();
            updateDashboard();
            updateEventosTable();
            updateVenuesTable();
        }
    }
}