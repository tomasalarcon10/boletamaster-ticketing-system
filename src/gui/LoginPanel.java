package gui;

import sistema.SistemaVentaTiquetes;
import usuarios.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel de inicio de sesión para todos los tipos de usuarios
 */
public class LoginPanel extends JPanel {
    private MainGUI mainGUI;
    private SistemaVentaTiquetes sistema;
    
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegistrar;
    private JComboBox<String> cmbTipoUsuario;
    
    public LoginPanel(MainGUI mainGUI, SistemaVentaTiquetes sistema) {
        this.mainGUI = mainGUI;
        this.sistema = sistema;
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Panel principal con borde
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        JLabel lblTitle = new JLabel("BoletaMaster Pro", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(new Color(0, 100, 200));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 30, 10);
        mainContainer.add(lblTitle, gbc);
        
        // Subtítulo
        JLabel lblSubtitle = new JLabel("Sistema de Venta de Tiquetes", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtitle.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 30, 10);
        mainContainer.add(lblSubtitle, gbc);
        
        // Reset insets
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridwidth = 1;
        
        // Tipo de usuario
        JLabel lblTipoUsuario = new JLabel("Tipo de Usuario:");
        lblTipoUsuario.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainContainer.add(lblTipoUsuario, gbc);
        
        cmbTipoUsuario = new JComboBox<>(new String[]{"Cliente", "Organizador", "Administrador"});
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainContainer.add(cmbTipoUsuario, gbc);
        
        // Usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainContainer.add(lblUsuario, gbc);
        
        txtUsuario = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainContainer.add(txtUsuario, gbc);
        
        // Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainContainer.add(lblPassword, gbc);
        
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainContainer.add(txtPassword, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(new Color(0, 100, 200));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(140, 35));
        btnLogin.addActionListener(new LoginListener());
        
        btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setBackground(new Color(100, 150, 200));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegistrar.setPreferredSize(new Dimension(120, 35));
        btnRegistrar.addActionListener(new RegisterListener());
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegistrar);
        
        mainContainer.add(buttonPanel, gbc);
        
        // Información del sistema
        JLabel lblInfo = new JLabel(
            "<html><div style='text-align: center;'>" +
            "Usuarios de prueba:<br>" +
            "• Cliente: cliente1 / 123<br>" +
            "• Organizador: organizador1 / 123<br>" +
            "• Administrador: admin / 123<br>" +
            "Datos cargados: " + sistema.getUsuarios().size() + " usuarios, " + 
            sistema.getEventos().size() + " eventos" +
            "</div></html>", JLabel.CENTER);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 11));
        lblInfo.setForeground(Color.DARK_GRAY);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        
        add(mainContainer, BorderLayout.CENTER);
        add(lblInfo, BorderLayout.SOUTH);
        
        // Permitir login con Enter
        txtPassword.addActionListener(new LoginListener());
    }
    
    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String usuario = txtUsuario.getText().trim();
            String password = new String(txtPassword.getPassword());
            String tipo = (String) cmbTipoUsuario.getSelectedItem();
            
            if (usuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginPanel.this, 
                    "Por favor ingrese usuario y contraseña", 
                    "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Autenticar usuario
            Usuario user = sistema.autenticar(usuario, password);
            
            if (user == null) {
                JOptionPane.showMessageDialog(LoginPanel.this, 
                    "Credenciales inválidas", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verificar tipo de usuario
            boolean tipoCorrecto = false;
            switch (tipo.toLowerCase()) {
                case "cliente":
                    tipoCorrecto = (user instanceof usuarios.Cliente);
                    break;
                case "organizador":
                    tipoCorrecto = (user instanceof usuarios.Organizador);
                    break;
                case "administrador":
                    tipoCorrecto = (user instanceof usuarios.Administrador);
                    break;
            }
            
            if (!tipoCorrecto) {
                JOptionPane.showMessageDialog(LoginPanel.this, 
                    "El usuario no es del tipo seleccionado", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Redirigir a la pantalla correspondiente
            if (user instanceof usuarios.Cliente) {
                mainGUI.showClienteScreen((usuarios.Cliente) user);
            } else if (user instanceof usuarios.Organizador) {
                mainGUI.showOrganizadorScreen((usuarios.Organizador) user);
            } else if (user instanceof usuarios.Administrador) {
                mainGUI.showAdministradorScreen((usuarios.Administrador) user);
            }
            
            // Limpiar campos
            txtPassword.setText("");
        }
    }
    
    private class RegisterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String tipo = (String) cmbTipoUsuario.getSelectedItem();
            
            // Diálogo de registro simplificado
            JTextField txtNewUser = new JTextField();
            JPasswordField txtNewPass = new JPasswordField();
            JTextField txtNombre = new JTextField();
            
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Usuario:"));
            panel.add(txtNewUser);
            panel.add(new JLabel("Contraseña:"));
            panel.add(txtNewPass);
            panel.add(new JLabel("Nombre completo:"));
            panel.add(txtNombre);
            
            int result = JOptionPane.showConfirmDialog(LoginPanel.this, panel, 
                "Registrar nuevo " + tipo, JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                String newUser = txtNewUser.getText().trim();
                String newPass = new String(txtNewPass.getPassword());
                String nombre = txtNombre.getText().trim();
                
                if (newUser.isEmpty() || newPass.isEmpty() || nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPanel.this, 
                        "Todos los campos son obligatorios", 
                        "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Usuario usuario = sistema.registrarUsuario(newUser, newPass, nombre, tipo.toLowerCase());
                
                if (usuario != null) {
                    JOptionPane.showMessageDialog(LoginPanel.this, 
                        "¡Registro exitoso! Ahora puede iniciar sesión.", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    sistema.guardarDatosAutomaticamente();
                    
                    // Autocompletar campos
                    txtUsuario.setText(newUser);
                    txtPassword.setText("");
                } else {
                    JOptionPane.showMessageDialog(LoginPanel.this, 
                        "Error en el registro. El usuario ya existe.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}