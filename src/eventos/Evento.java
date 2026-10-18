package eventos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import usuarios.Organizador;

public class Evento {
    
    // ===== Atributos =====
    private String id;
    private String nombre;
    private LocalDateTime fechaHora;
    private String tipo; // musical, cultural, deportivo, religioso
    private Organizador organizador;
    private Venue venue;
    private List<Localidad> localidades;
    private double porcentajeVentas;
    private String estado; // "activo" o "cancelado"

    // ===== Constructor =====
    public Evento(String id, String nombre, LocalDateTime fechaHora, String tipo, 
                  Organizador organizador, Venue venue) {
        this.id = id;
        this.nombre = nombre;
        this.fechaHora = fechaHora;
        this.tipo = tipo;
        this.organizador = organizador;
        this.venue = venue;
        this.localidades = new ArrayList<>();
        this.porcentajeVentas = 0.0;
        this.estado = "activo";
        
        // Relación bidireccional automática
        if (organizador != null) {
            organizador.agregarEvento(this);
        }
    }

    // ===== Getters =====
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getTipo() { return tipo; }
    public Organizador getOrganizador() { return organizador; }
    public Venue getVenue() { return venue; }
    public List<Localidad> getLocalidades() { return localidades; }
    public double getPorcentajeVentas() { return porcentajeVentas; }
    public String getEstado() { return estado; }

    // ===== Setters =====
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setEstado(String estado) { this.estado = estado; }

 // ====== MÉTODOS DE APOYO PARA ORGANIZADOR ======

 // Calcula las ganancias totales para el organizador
 public double calcularGananciasOrganizador() {
     double total = 0.0;
     for (Localidad loc : localidades) {
         total += loc.calcularIngresos(); // cada localidad aporta ingresos
     }
     return total;
 }

 // Cuenta la cantidad total de tiquetes vendidos en el evento
 public int calcularTiquetesVendidos() {
     int total = 0;
     for (Localidad loc : localidades) {
         total += loc.getTiquetesVendidos();
     }
     return total;
 }

 // Calcula la capacidad total del evento (sumatoria de localidades)
 public int calcularCapacidadTotal() {
     int total = 0;
     for (Localidad loc : localidades) {
         total += loc.getCapacidad();
     }
     return total;
 }
    // ===== Métodos funcionales =====
    
    public void agregarLocalidad(Localidad localidad) {
        if (localidad != null && !localidades.contains(localidad)) {
            localidades.add(localidad);
        }
    }

    // Calcula el porcentaje global de ventas del evento 
    public double calcularPorcentajeVentas() {
        int totalDisponibles = 0;
        int totalVendidos = 0;

        for (Localidad localidad : localidades) {
            totalDisponibles += localidad.getCapacidad();
            totalVendidos += localidad.getTiquetesVendidos();
        }

        if (totalDisponibles == 0) {
            porcentajeVentas = 0.0;
        } else {
            porcentajeVentas = (totalVendidos * 100.0) / totalDisponibles;
        }
        return porcentajeVentas;
    }

    // Retorna el total de tiquetes vendidos en todas las localidades 
    public int getTiquetesVendidosTotal() {
        int total = 0;
        for (Localidad localidad : localidades) {
            total += localidad.getTiquetesVendidos();
        }
        return total;
    }

    // Retorna la capacidad total sumando todas las localidades 
    public int getCapacidadTotal() {
        int total = 0;
        for (Localidad localidad : localidades) {
            total += localidad.getCapacidad();
        }
        return total;
    }

    // Busca una localidad por nombre (ignorando mayúsculas/minúsculas) 
    public Localidad buscarLocalidad(String nombreLocalidad) {
        for (Localidad loc : localidades) {
            if (loc.getNombre().equalsIgnoreCase(nombreLocalidad)) {
                return loc;
            }
        }
        return null;
    }

    // Verifica si el evento ya ocurrió 
    public boolean yaOcurrio() {
        return LocalDateTime.now().isAfter(fechaHora);
    }

    // Verifica si aún hay tiquetes disponibles 
    public boolean hayTiquetesDisponibles() {
        for (Localidad loc : localidades) {
            if (loc.hayDisponibles()) {
                return true;
            }
        }
        return false;
    }

    // Muestra la información detallada del evento (para la consola) 
    public void mostrarInformacionEvento() {
        System.out.println("====================================");
        System.out.println("        INFORMACIÓN DEL EVENTO      ");
        System.out.println("====================================");
        System.out.println("ID: " + id);
        System.out.println("Nombre: " + nombre);
        System.out.println("Fecha y Hora: " + fechaHora);
        System.out.println("Tipo: " + tipo);
        System.out.println("Estado: " + estado);
        System.out.println("Venue: " + venue.getNombre());

        if (organizador != null) {
            System.out.println("Organizador: " + organizador.getNombre());
        } else {
            System.out.println("Organizador: (No asignado)");
        }

        System.out.println("------------------------------------");
        System.out.println("Capacidad Total: " + getCapacidadTotal());
        System.out.println("Tiquetes Vendidos: " + getTiquetesVendidosTotal());
        System.out.println("Porcentaje de Ventas: " + String.format("%.2f", calcularPorcentajeVentas()) + "%");

        System.out.println("\n--- Localidades ---");
        if (localidades.isEmpty()) {
            System.out.println("  (Sin localidades configuradas)");
        } else {
            for (Localidad loc : localidades) {
                System.out.println("  - " + loc.getNombre() + ": " 
                                   + loc.getTiquetesVendidos() + "/" + loc.getCapacidad()
                                   + " vendidos - Precio Final: $" 
                                   + String.format("%.2f", loc.getPrecioFinal()));
            }
        }
        System.out.println("====================================\n");
    }

    @Override
    public String toString() {
        return "Evento: " + nombre + " (" + tipo + ") - " + fechaHora;
    }
}