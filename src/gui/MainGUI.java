package gui;

import sistema.SistemaVentaTiquetes;
import usuarios.*;

import javax.swing.*;
import java.awt.*;

/**
 * Clase principal que reemplaza la interfaz de consola por una interfaz gráfica Swing
 */
public class MainGUI extends JFrame {
    private SistemaVentaTiquetes sistema;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    private MusicPlayer musicPlayer;  // <<<<<<<<<<<< Nuevo
    private JButton muteButton;   
    
    // Paneles principales
    private LoginPanel loginPanel;
    private ClientePanel clientePanel;
    private OrganizadorPanel organizadorPanel;
    private AdministradorPanel administradorPanel;
    
    public MainGUI() {
        this.sistema = new SistemaVentaTiquetes();
        initializeSystem();
        initializeGUI();
    }
    
    private void initializeSystem() {
        try {
            sistema.getPersistencia().cargarDatos();
            System.out.println("Sistema cargado exitosamente");
            System.out.println("Usuarios: " + sistema.getUsuarios().size());
            System.out.println("Eventos: " + sistema.getEventos().size());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar datos del sistema: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void toggleMusic() {
        if (musicPlayer.isPlaying()) {
            musicPlayer.stop();
            muteButton.setText("Activar música");
        } else {
            musicPlayer.resume();
            muteButton.setText("Silenciar música");
        }
    }
    
    private void initializeGUI() {
        setTitle("BoletaMaster Pro - Sistema de Venta de Tiquetes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Reproductor de música
        musicPlayer = new MusicPlayer();
        musicPlayer.play("./Musica/Shop-The-Legend-of-Zelda-Ocarina-of-Time-music_zelda.wav");  

        muteButton = new JButton("Silenciar música");
        muteButton.addActionListener(e -> toggleMusic());

        add(muteButton, BorderLayout.NORTH);  
        
        // Configurar layout principal
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Crear paneles
        loginPanel = new LoginPanel(this, sistema);
        clientePanel = new ClientePanel(this, sistema);
        organizadorPanel = new OrganizadorPanel(this, sistema);
        administradorPanel = new AdministradorPanel(this, sistema);
        
        // Agregar paneles al card layout
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(clientePanel, "CLIENTE");
        mainPanel.add(organizadorPanel, "ORGANIZADOR");
        mainPanel.add(administradorPanel, "ADMINISTRADOR");
        
        add(mainPanel);
        showLoginScreen();
        
        // Guardar datos al cerrar
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                sistema.getPersistencia().guardarDatos();
                System.out.println("Datos guardados al cerrar la aplicación");
            }
        });
    }
    
    public void showLoginScreen() {
        cardLayout.show(mainPanel, "LOGIN");
        sistema.cerrarSesion();
    }
    
    public void showClienteScreen(Cliente cliente) {
        clientePanel.setClienteActual(cliente);
        clientePanel.actualizarDatos();
        cardLayout.show(mainPanel, "CLIENTE");
    }
    
    public void showOrganizadorScreen(Organizador organizador) {
        organizadorPanel.setOrganizadorActual(organizador);
        organizadorPanel.actualizarDatos();
        cardLayout.show(mainPanel, "ORGANIZADOR");
    }
    
    public void showAdministradorScreen(Administrador administrador) {
        administradorPanel.setAdministradorActual(administrador);
        administradorPanel.actualizarDatos();
        cardLayout.show(mainPanel, "ADMINISTRADOR");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Configurar look and feel de manera segura
            try {
                // Obtener el look and feel del sistema de forma correcta
                String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();
                UIManager.setLookAndFeel(systemLookAndFeel);
            } catch (Exception e) {
                System.err.println("Error setting look and feel: " + e.getMessage());
                // Continuar con el look and feel por defecto (Metal)
            }
            
            MainGUI mainGUI = new MainGUI();
            mainGUI.setVisible(true);
        });
    }
}