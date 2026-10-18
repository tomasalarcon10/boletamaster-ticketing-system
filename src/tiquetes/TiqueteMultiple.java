package tiquetes;

import usuarios.Cliente;
import eventos.Localidad;

/**
 * Representa un tiquete que agrupa varios accesos o beneficios
 * dentro de un mismo paquete (por ejemplo: pase doble o familiar).
 */
public abstract class TiqueteMultiple extends Tiquete {
    
    private double precioPaquete;
    private double precioMaximo;

    /**
     * Constructor base para tiquetes múltiples.
     * 
     * @param id Identificador único del tiquete
     * @param precioBase Precio base del paquete
     * @param cargoServicio Cargo por servicio
     * @param cuotaEmision Cuota de emisión
     * @param dueño Cliente dueño del tiquete
     * @param localidad Localidad asociada al tiquete
     * @param precioPaquete Precio total del paquete
     */
    public TiqueteMultiple(String id, double precioBase, double cargoServicio,
                           double cuotaEmision, Cliente dueño, Localidad localidad, double precioPaquete) {
        super(id, precioBase, cargoServicio, cuotaEmision, dueño, localidad);
        this.precioPaquete = precioPaquete;
        this.precioMaximo = precioBase;
    }

    // ===== Getters y Setters =====
    
    public double getPrecioPaquete() {
        return precioPaquete;
    }

    public double getPrecioMaximo() {
        return precioMaximo;
    }

    public void setPrecioPaquete(double precio) {
        if (precio >= 0) this.precioPaquete = precio;
    }

    public void setPrecioMaximo(double precio) {
        if (precio >= 0) this.precioMaximo = precio;
    }

    // ===== Métodos sobrescritos =====
    
    @Override
    public double calcularPrecioFinal() {
        return precioPaquete + getCargoServicio() + getCuotaEmision();
    }

    @Override
    public String getInfoBasica() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== TIQUETE MÚLTIPLE ==========\n");
        sb.append("ID: ").append(getId()).append("\n");
        sb.append("Precio del paquete: $").append(String.format("%.2f", precioPaquete)).append("\n");
        sb.append("Precio final: $").append(String.format("%.2f", calcularPrecioFinal())).append("\n");
        sb.append("Dueño: ").append(getDueño() != null ? getDueño().getNombre() : "Sin asignar").append("\n");
        sb.append("Estado: ").append(getEstado()).append("\n");
        if (getLocalidad() != null) {
            sb.append("Localidad: ").append(getLocalidad().getNombre()).append("\n");
        }
        sb.append("=====================================\n");
        return sb.toString();
    }
}
