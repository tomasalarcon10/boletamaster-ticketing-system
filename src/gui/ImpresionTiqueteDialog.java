package gui;

import tiquetes.Tiquete;
import eventos.Evento;
import gui.qr.SimpleQRGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Diálogo para imprimir tiquetes con código QR
 */
public class ImpresionTiqueteDialog extends JDialog {
    private Tiquete tiquete;
    private JLabel lblQRCode;
    
    public ImpresionTiqueteDialog(Frame parent, Tiquete tiquete) {
        super(parent, "Imprimir Tiquete", true);
        this.tiquete = tiquete;
        initializeDialog();
    }
    
    private void initializeDialog() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Panel de previsualización
        JPanel previewPanel = createPreviewPanel();
        add(previewPanel, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton btnImprimir = new JButton("Imprimir");
        btnImprimir.addActionListener(e -> imprimirTiquete());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        buttonPanel.add(btnImprimir);
        buttonPanel.add(btnCerrar);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Título
        JLabel lblTitle = new JLabel("Tiquete - BoletaMaster", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Información del tiquete
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        
        Evento evento = tiquete.getEvento();
        String fechaImpresion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String fechaEvento = evento.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        addInfoRow(infoPanel, "Evento:", evento.getNombre());
        addInfoRow(infoPanel, "ID Tiquete:", tiquete.getId());
        addInfoRow(infoPanel, "Fecha Evento:", fechaEvento);
        addInfoRow(infoPanel, "Fecha Impresión:", fechaImpresion);
        addInfoRow(infoPanel, "Localidad:", tiquete.getLocalidad().getNombre());
        addInfoRow(infoPanel, "Precio:", "$" + String.format("%.2f", tiquete.calcularPrecioFinal()));
        
        // Panel del código QR
        JPanel qrPanel = new JPanel();
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setBorder(BorderFactory.createTitledBorder("Código QR"));
        
        // Generar datos para el QR
        String qrData = SimpleQRGenerator.generateQRData(
            evento.getNombre(),
            tiquete.getId(),
            fechaEvento,
            fechaImpresion
        );
        
        // Generar imagen QR
        BufferedImage qrImage = SimpleQRGenerator.generateQRImage(qrData, 200);
        lblQRCode = new JLabel(new ImageIcon(qrImage));
        lblQRCode.setHorizontalAlignment(SwingConstants.CENTER);
        
        qrPanel.add(lblQRCode);
        
        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(qrPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addInfoRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowPanel.setBackground(Color.WHITE);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(120, 20));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 12));
        
        rowPanel.add(lbl);
        rowPanel.add(lblValue);
        
        panel.add(rowPanel);
    }
    
    private void imprimirTiquete() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new TiquetePrintable());
            
            if (job.printDialog()) {
                // Marcar el tiquete como impreso (aquí implementarías la lógica de bloqueo)
                tiquete.setEstado("impreso");
                
                job.print();
                JOptionPane.showMessageDialog(this, 
                    "Tiquete impreso exitosamente\nEl tiquete ha sido marcado como impreso y no puede reimprimirse", 
                    "Impresión", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al imprimir: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private class TiquetePrintable implements Printable {
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }
            
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            
            // Dibujar el tiquete
            dibujarTiquete(g2d);
            
            return PAGE_EXISTS;
        }
        
        private void dibujarTiquete(Graphics2D g2d) {
            Evento evento = tiquete.getEvento();
            String fechaImpresion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String fechaEvento = evento.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("BOLETAMASTER - TIQUETE OFICIAL", 50, 50);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Evento: " + evento.getNombre(), 50, 80);
            g2d.drawString("ID: " + tiquete.getId(), 50, 100);
            g2d.drawString("Fecha Evento: " + fechaEvento, 50, 120);
            g2d.drawString("Fecha Impresión: " + fechaImpresion, 50, 140);
            g2d.drawString("Localidad: " + tiquete.getLocalidad().getNombre(), 50, 160);
            g2d.drawString("Precio: $" + String.format("%.2f", tiquete.calcularPrecioFinal()), 50, 180);
            
            // Generar y dibujar QR para impresión
            String qrData = SimpleQRGenerator.generateQRData(
                evento.getNombre(),
                tiquete.getId(),
                fechaEvento,
                fechaImpresion
            );
            
            BufferedImage qrImage = SimpleQRGenerator.generateQRImage(qrData, 150);
            g2d.drawImage(qrImage, 200, 200, null);
            
            // Mensaje de impreso una vez
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("** IMPRESO UNA VEZ - NO REIMPRIMIR **", 50, 400);
        }
    }
}