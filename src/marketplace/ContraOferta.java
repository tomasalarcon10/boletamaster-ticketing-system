package marketplace;

import usuarios.Cliente;
import java.time.LocalDateTime;

public class ContraOferta {
    private String id;
    private OfertaReventa ofertaReventa;
    private Cliente comprador;
    private double precioPropuesto;
    private LocalDateTime fecha;
    private String estado;

    public ContraOferta(String id, OfertaReventa ofertaReventa, Cliente comprador, double precioPropuesto) {
        this.id = id;
        this.ofertaReventa = ofertaReventa;
        this.comprador = comprador;
        this.precioPropuesto = precioPropuesto;
        this.fecha = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }

    public String getId() { return id; }
    public OfertaReventa getOfertaReventa() { return ofertaReventa; }
    public Cliente getComprador() { return comprador; }
    public double getPrecioPropuesto() { return precioPropuesto; }
    public LocalDateTime getFecha() { return fecha; }
    public String getEstado() { return estado; }

    public void setEstado(String estado) { 
        this.estado = estado; 
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return String.format("ContraOferta[%s] - %s - $%.2f - %s", 
                id, ofertaReventa.getId(), precioPropuesto, estado);
    }
}