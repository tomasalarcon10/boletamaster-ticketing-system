package eventos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import usuarios.Organizador;

public class Venue {
    private String id;
    private String nombre;
    private String ubicacion;
    private int capacidadMaxima;
    private String restricciones;
    private List<Evento> eventos;
    private boolean aprobado;
    private Organizador organizadorSugerente; // 🔹 Nuevo atributo

    /**
     * Constructor principal de Venue
     */
    public Venue(String id, String nombre, String ubicacion, int capacidadMaxima,
                 String restricciones, boolean aprobado) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.capacidadMaxima = Math.max(0, capacidadMaxima);
        this.restricciones = restricciones;
        this.aprobado = aprobado;
        this.eventos = new ArrayList<>();
        this.organizadorSugerente = null;
    }

    // 🔸 Constructor alternativo (sin "aprobado", útil para sugerencias)
    public Venue(String id, String nombre, String ubicacion, int capacidadMaxima, String restricciones) {
        this(id, nombre, ubicacion, capacidadMaxima, restricciones, false);
    }

    // ===== GETTERS =====
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
    public String getRestricciones() { return restricciones; }
    public boolean isAprobado() { return aprobado; }
    public List<Evento> getEventos() { return eventos; }
    public Organizador getOrganizadorSugerente() { return organizadorSugerente; }

    // ===== SETTERS =====
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setRestricciones(String restricciones) { this.restricciones = restricciones; }

    public void setCapacidadMaxima(int capacidadMaxima) {
        if (capacidadMaxima > 0) {
            this.capacidadMaxima = capacidadMaxima;
        }
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }

    public void setOrganizadorSugerente(Organizador organizador) {
        this.organizadorSugerente = organizador;
    }

    // ===== LÓGICA =====
    public int getCapacidadTotal() {
        return capacidadMaxima;
    }

    public void agregarEvento(Evento evento) {
        if (evento != null && !eventos.contains(evento)) {
            eventos.add(evento);
        }
    }

    public boolean estaDisponible(LocalDate fecha) {
        for (Evento evento : eventos) {
            if (evento.getFechaHora().toLocalDate().equals(fecha)) {
                return false;
            }
        }
        return true;
    }

    public List<Evento> getEventosEnFecha(LocalDate fecha) {
        List<Evento> eventosEnFecha = new ArrayList<>();
        for (Evento evento : eventos) {
            if (evento.getFechaHora().toLocalDate().equals(fecha)) {
                eventosEnFecha.add(evento);
            }
        }
        return eventosEnFecha;
    }

    public int getCantidadEventos() {
        return eventos.size();
    }

    // ===== VISUALIZACIÓN =====
    public void mostrarInformacion() {
        System.out.println("=== VENUE ===");
        System.out.println("ID: " + id);
        System.out.println("Nombre: " + nombre);
        System.out.println("Ubicación: " + ubicacion);
        System.out.println("Capacidad: " + capacidadMaxima);
        System.out.println("Restricciones: " + restricciones);
        System.out.println("Aprobado: " + (aprobado ? "Sí" : "No"));

        if (organizadorSugerente != null) {
            System.out.println("Sugerido por: " + organizadorSugerente.getNombre());
        }

        System.out.println("Eventos programados: " + eventos.size());
        if (!eventos.isEmpty()) {
            System.out.println("\nEventos en este venue:");
            for (Evento e : eventos) {
                System.out.println("  - " + e.getNombre() + " (" + e.getFechaHora().toLocalDate() + ")");
            }
        }
    }

    public void mostrarResumen() {
        System.out.println(nombre + " | " + ubicacion + " | Cap: " + capacidadMaxima +
                " | " + (aprobado ? "Aprobado" : "Pendiente"));
    }

    @Override
    public String toString() {
        return nombre + " - " + ubicacion + " (Cap: " + capacidadMaxima + ")";
    }
}