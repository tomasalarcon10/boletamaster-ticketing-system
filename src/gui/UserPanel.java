package gui;

import sistema.SistemaVentaTiquetes;
import usuarios.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Panel base con funcionalidades comunes para todos los paneles de usuario
 */
public abstract class UserPanel extends JPanel {
    protected MainGUI mainGUI;
    protected SistemaVentaTiquetes sistema;
    protected Usuario usuarioActual;
    
    protected JLabel lblWelcome;
    protected JLabel lblUserInfo;
    protected JButton btnCerrarSesion;
    protected JTabbedPane tabbedPane;
    
    public UserPanel(MainGUI mainGUI, SistemaVentaTiquetes sistema) {
        this.mainGUI = mainGUI;
        this.sistema = sistema;
        this.usuarioActual = null;
        setLayout(new BorderLayout());
    }
    
    protected void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Información del usuario
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfoPanel.setBackground(new Color(240, 240, 240));
        
        lblWelcome = new JLabel();
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        
        lblUserInfo = new JLabel();
        lblUserInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUserInfo.setForeground(Color.GRAY);
        
        userInfoPanel.add(lblWelcome);
        userInfoPanel.add(Box.createHorizontalStrut(20));
        userInfoPanel.add(lblUserInfo);
        
        // Botón cerrar sesión
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(new Color(240, 240, 240));
        
        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(200, 80, 80));
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.addActionListener(e -> mainGUI.showLoginScreen());
        
        logoutPanel.add(btnCerrarSesion);
        
        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    protected void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    public abstract void actualizarDatos();
    
    protected void updateUserInfo() {
        if (usuarioActual != null) {
            lblWelcome.setText("¡Bienvenido, " + usuarioActual.getNombre() + "!");
            lblUserInfo.setText("Usuario: " + usuarioActual.getLogin() + 
                              " | Saldo: $" + String.format("%.2f", usuarioActual.getSaldoVirtual()));
        }
    }
    
    // Método utilitario para crear botones con estilo consistente
    protected JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }
    
    // Método para crear paneles con borde y título
    protected JPanel createTitledPanel(String title, LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), title),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);
        return panel;
    }
}