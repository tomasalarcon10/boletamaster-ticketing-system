package marketplace;

import tiquetes.Tiquete;
import usuarios.Cliente;
import java.time.LocalDateTime;

public class OfertaReventa {
    private String id;
    private Tiquete tiquete;
    private Cliente vendedor;
    private double precioVenta;
    private LocalDateTime fechaPublicacion;
    private boolean activa;

    public OfertaReventa(String id, Tiquete tiquete, Cliente vendedor, double precioVenta) {
        this.id = id;
        this.tiquete = tiquete;
        this.vendedor = vendedor;
        this.precioVenta = precioVenta;
        this.fechaPublicacion = LocalDateTime.now();
        this.activa = true;
    }

    // Getters
    public String getId() { return id; }
    public Tiquete getTiquete() { return tiquete; }
    public Cliente getVendedor() { return vendedor; }
    public double getPrecioVenta() { return precioVenta; }
    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public boolean isActiva() { return activa; }
    
    public void setActiva(boolean activa) { this.activa = activa; }

    public boolean esRevendible() {
        return tiquete != null && tiquete.esRevendible();
    }
    
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    @Override
    public String toString() {
        return String.format("Oferta[%s] - %s - $%.2f - %s", 
                id, tiquete.getId(), precioVenta, activa ? "ACTIVA" : "INACTIVA");
    }
}