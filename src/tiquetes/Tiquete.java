package tiquetes;

import usuarios.Cliente;
import eventos.Localidad;
import java.time.LocalDateTime;

/**
 * Clase abstracta que representa un tiquete de evento.
 * Incluye la lógica base de compra, transferencia, estado y precio.
 */
public abstract class Tiquete {

    private String id;
    private double precioBase;
    private double cargoServicio;
    private double cuotaEmision;
    private LocalDateTime fechaCompra;
    private Cliente dueño;
    private boolean transferible;
    private Localidad localidad;
    private String estado; // vendido, transferido, usado, reembolsado

    // ===== CONSTRUCTOR =====
    public Tiquete(String id, double precioBase, double cargoServicio, double cuotaEmision,
                   Cliente dueño, Localidad localidad) {
        this.id = id;
        this.precioBase = precioBase;
        this.cargoServicio = cargoServicio;
        this.cuotaEmision = cuotaEmision;
        this.dueño = dueño;
        this.localidad = localidad;
        this.fechaCompra = LocalDateTime.now();
        this.transferible = true;
        this.estado = "vendido";
    }

    // ===== GETTERS =====
    public String getId() { return id; }
    public double getPrecioBase() { return precioBase; }
    public double getCargoServicio() { return cargoServicio; }
    public double getCuotaEmision() { return cuotaEmision; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public Cliente getDueño() { return dueño; }
    public boolean isTransferible() { return transferible; }
    public Localidad getLocalidad() { return localidad; }
    public String getEstado() { return estado; }

    // ===== SETTERS =====
    public void setFechaCompra(LocalDateTime fecha) { this.fechaCompra = fecha; }
    public void setTransferible(boolean transferible) { this.transferible = transferible; }
    public void setDueño(Cliente nuevoDueño) { this.dueño = nuevoDueño; }
    public void setEstado(String estado) { this.estado = estado; }

    // ===== MÉTODOS LÓGICOS =====

    /** Calcula el precio final del tiquete con todos los cargos incluidos */
    public double calcularPrecioFinal() {
        return precioBase + cargoServicio + cuotaEmision;
    }

    /** Verifica si el tiquete puede ser transferido */
    public boolean puedeTransferir() {
        if (!transferible) return false;

        if (estado.equals("usado") || estado.equals("reembolsado") || estado.equals("transferido"))
            return false;

        if (localidad != null && localidad.getEvento() != null) {
            LocalDateTime fechaEvento = localidad.getEvento().getFechaHora();
            if (fechaEvento.isBefore(LocalDateTime.now())) return false;
        }

        return true;
    }

    /** Transfiere el tiquete a otro cliente, si cumple las condiciones */
    public boolean transferir(Cliente nuevoDueño, String passwordActual) {
        if (nuevoDueño == null) {
            System.out.println("Error: El destinatario no puede ser nulo");
            return false;
        }
        if (nuevoDueño.equals(dueño)) {
            System.out.println("Error: No puedes transferir un tiquete a ti mismo");
            return false;
        }
        if (!dueño.validarPassword(passwordActual)) {
            System.out.println("Error: Contraseña incorrecta");
            return false;
        }
        if (!puedeTransferir()) {
            System.out.println("Error: Este tiquete no puede ser transferido");
            return false;
        }

        this.setDueño(nuevoDueño);
        this.estado = "transferido";

        System.out.println("✓ Tiquete " + id + " transferido exitosamente a " + nuevoDueño.getNombre());
        return true;
    }

    /** Marca el tiquete como usado */
    public void marcarComoUsado() {
        this.estado = "usado";
    }

    /** Verifica si el tiquete sigue siendo válido */
    public boolean esValido() {
        return estado.equals("vendido") || estado.equals("transferido");
    }

    /** Retorna el evento al que pertenece el tiquete */
    public eventos.Evento getEvento() {
        return (localidad != null) ? localidad.getEvento() : null;
    }

    /** Devuelve información básica del tiquete */
    public String getInfoBasica() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append("\n");
        sb.append("Precio final: $").append(String.format("%.2f", calcularPrecioFinal())).append("\n");
        sb.append("Dueño: ").append(dueño.getNombre()).append("\n");
        sb.append("Estado: ").append(estado).append("\n");
        if (localidad != null)
            sb.append("Localidad: ").append(localidad.getNombre()).append("\n");
        return sb.toString();
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format("Tiquete[%s] - $%.2f - %s", id, calcularPrecioFinal(), estado);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tiquete tiquete = (Tiquete) obj;
        return id.equals(tiquete.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    public boolean esRevendible() {
        return true;
    }
}
