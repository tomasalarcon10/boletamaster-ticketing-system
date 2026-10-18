package eventos;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Oferta {
    private String id;
    private double descuento; // porcentaje de descuento (0-100)
    private Localidad localidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Constructor principal
     */
    public Oferta(String id, double descuento, LocalDateTime fechaInicio,
                  LocalDateTime fechaFin, Localidad localidad) {

        this.id = id;
        this.descuento = (descuento >= 0 && descuento <= 100) ? descuento : 0;
        this.localidad = localidad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin.isAfter(fechaInicio) ? fechaFin : fechaInicio.plusDays(1);
    }

    // ===== GETTERS =====
    public String getId() { return id; }
    public double getDescuento() { return descuento; }
    public Localidad getLocalidad() { return localidad; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }

    // ===== SETTERS =====
    public void setDescuento(double descuento) {
        if (descuento >= 0 && descuento <= 100) this.descuento = descuento;
        else System.out.println("Error: El descuento debe estar entre 0 y 100");
    }

    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public void setFechaFin(LocalDateTime fechaFin) {
        if (fechaFin.isAfter(fechaInicio)) this.fechaFin = fechaFin;
        else System.out.println("Error: La fecha de fin debe ser posterior a la de inicio");
    }

    public void setLocalidad(Localidad localidad) { this.localidad = localidad; }

    // ===== LÓGICA =====
    public boolean estaVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return !ahora.isBefore(fechaInicio) && !ahora.isAfter(fechaFin);
    }

    public boolean estaVigenteEn(LocalDateTime fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public boolean yaExpiro() { return LocalDateTime.now().isAfter(fechaFin); }

    public boolean noHaComenzado() { return LocalDateTime.now().isBefore(fechaInicio); }

    public double aplicarDescuento(double precio) {
        return estaVigente() ? precio * (1 - descuento / 100.0) : precio;
    }

    public double calcularAhorro(double precio) {
        return estaVigente() ? precio * (descuento / 100.0) : 0;
    }

    public double getDescuentoEfectivo() {
        return estaVigente() ? descuento : 0;
    }

    public long getHorasRestantes() {
        return estaVigente() ? Duration.between(LocalDateTime.now(), fechaFin).toHours() : 0;
    }

    public String getEstado() {
        if (noHaComenzado()) return "PRÓXIMAMENTE";
        if (estaVigente()) return "VIGENTE";
        if (yaExpiro()) return "EXPIRADA";
        return "DESCONOCIDO";
    }

    // ===== VISUALIZACIÓN =====
    public void mostrarInformacion() {
        System.out.println("=== OFERTA ===");
        System.out.println("ID: " + id);
        System.out.println("Descuento: " + descuento + "%");
        System.out.println("Localidad: " + (localidad != null ? localidad.getNombre() : "(sin asignar)"));
        System.out.println("Desde: " + fechaInicio.format(FORMATO_FECHA));
        System.out.println("Hasta: " + fechaFin.format(FORMATO_FECHA));
        System.out.println("Estado: " + getEstado());

        if (estaVigente()) {
            System.out.println("⏰ Quedan " + getHorasRestantes() + " horas.");
            System.out.println("Ejemplo: $100000 → $" + String.format("%.2f", aplicarDescuento(100000)));
        }
    }

    public void mostrarResumen() {
        System.out.println("Oferta " + descuento + "% OFF - " + getEstado() +
                (localidad != null ? " (" + localidad.getNombre() + ")" : ""));
    }

    @Override
    public String toString() {
        return descuento + "% OFF en " +
                (localidad != null ? localidad.getNombre() : "localidad desconocida") +
                " [" + getEstado() + "]";
    }
}