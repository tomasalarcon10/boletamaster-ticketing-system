package usuarios;

import eventos.Evento;
import eventos.Localidad;
import eventos.Oferta;
import eventos.Venue;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Organizador extends Usuario {

    private List<Evento> eventosActivos;

    public Organizador(String login, String password, String nombre) {
        super(login, password, nombre);
        this.eventosActivos = new ArrayList<>();
    }

    // ===== EVENTOS =====
    public List<Evento> getEventosActivos() {
        return new ArrayList<>(eventosActivos);
    }

    public void agregarEvento(Evento evento) {
        if (evento != null && !eventosActivos.contains(evento)) {
            eventosActivos.add(evento);
        }
    }

    public boolean removerEvento(Evento evento) {
        return eventosActivos.remove(evento);
    }

    // ===== VENUES =====
    public Venue sugerirVenue(String id, String nombre, String ubicacion, int capacidad, String restricciones) {
        Venue venue = new Venue(id, nombre, ubicacion, capacidad, restricciones, false);
        venue.setOrganizadorSugerente(this);

        System.out.println("Venue sugerido: " + nombre + " (pendiente de aprobación)");
        return venue;
    }

    // ===== OFERTAS =====
    public boolean crearOferta(Evento evento, String nombreLocalidad, double descuento,
                               LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        if (!eventosActivos.contains(evento)) {
            System.out.println("Error: El evento no pertenece a este organizador");
            return false;
        }

        Localidad localidad = evento.buscarLocalidad(nombreLocalidad);
        if (localidad == null) {
            System.out.println("Error: Localidad no encontrada");
            return false;
        }

        String idOferta = "OFR-" + evento.getId() + "-" + nombreLocalidad + "-" + System.currentTimeMillis();
        Oferta oferta = new Oferta(idOferta, descuento,fechaInicio, fechaFin, localidad);

        localidad.aplicarOferta(oferta);
        System.out.println("Oferta creada: " + descuento + "% de descuento en " + nombreLocalidad);
        return true;
    }

    // ===== LOCALIDADES =====
    public void agregarLocalidad(Evento evento, String nombre, double precio,
            boolean numerada, int capacidad, String tipoTiquete) {
    	if (!eventosActivos.contains(evento)) {
    		System.out.println("Error: El evento no pertenece a este organizador");
    		return;
    	}

    	Localidad nueva = new Localidad(nombre, precio, numerada, capacidad, tipoTiquete, evento);
    	evento.agregarLocalidad(nueva);
    	System.out.println("Localidad agregada: " + nombre + " al evento " + evento.getNombre());
    }

    public void cambiarTipoTiquete(Localidad localidad, String nuevoTipo) {
        if (localidad != null) {
            localidad.setTipoTiquetes(nuevoTipo);
            System.out.println("Tipo de tiquete cambiado a: " + nuevoTipo);
        }
    }

    public void cambiarCapacidadLocalidad(Localidad localidad, int nuevaCapacidad) {
        if (localidad != null) {
            localidad.setCapacidad(nuevaCapacidad);
            System.out.println("Capacidad cambiada a: " + nuevaCapacidad);
        }
    }

    // ===== REPORTES =====
    public double calcularGananciasTotales() {
        double total = 0.0;
        for (Evento evento : eventosActivos) {
            total += evento.calcularGananciasOrganizador();
        }
        return total;
    }

    public double calcularPorcentajeVentasGlobal() {
        int totalVendidos = 0;
        int totalDisponibles = 0;

        for (Evento evento : eventosActivos) {
            totalVendidos += evento.calcularTiquetesVendidos();
            totalDisponibles += evento.calcularCapacidadTotal();
        }

        if (totalDisponibles == 0) return 0.0;
        return (totalVendidos * 100.0) / totalDisponibles;
    }

    public String verReportePorEvento(Evento evento) {
        if (!eventosActivos.contains(evento)) {
            return "Error: El evento no pertenece a este organizador";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n========== REPORTE DEL EVENTO ==========\n");
        sb.append("Evento: ").append(evento.getNombre()).append("\n");
        sb.append("Fecha: ").append(evento.getFechaHora()).append("\n");
        sb.append("Ganancias: $").append(String.format("%.2f", evento.calcularGananciasOrganizador())).append("\n");
        sb.append("Porcentaje de ventas: ").append(String.format("%.2f", evento.getPorcentajeVentas())).append("%\n");
        sb.append("Tiquetes vendidos: ").append(evento.calcularTiquetesVendidos());
        sb.append(" / ").append(evento.calcularCapacidadTotal()).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    public String verReportePorLocalidad(Localidad localidad) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== REPORTE DE LOCALIDAD ==========\n");
        sb.append("Localidad: ").append(localidad.getNombre()).append("\n");
        sb.append("Precio base: $").append(String.format("%.2f", localidad.getPrecioBase())).append("\n");
        sb.append("Ganancias: $").append(String.format("%.2f", localidad.calcularGananciasOrganizador())).append("\n");
        sb.append("Tiquetes vendidos: ").append(localidad.getTiquetesVendidos());
        sb.append(" / ").append(localidad.getCapacidad()).append("\n");
        sb.append("Porcentaje: ").append(String.format("%.2f", localidad.calcularPorcentajeVentas())).append("%\n");
        sb.append("==========================================\n");
        return sb.toString();
    }

    // ===== CANCELACIÓN =====
    public boolean solicitarCancelacionEvento(Evento evento) {
        if (!eventosActivos.contains(evento)) {
            System.out.println("Error: El evento no pertenece a este organizador");
            return false;
        }

        System.out.println("Solicitud de cancelación registrada para: " + evento.getNombre());
        System.out.println("Esperando autorización del administrador...");
        return true;
    }

    // ===== UTILIDAD =====
    public boolean esEventoPropio(Evento evento) {
        return eventosActivos.contains(evento);
    }

    @Override
    public String mostrarPerfil() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================\n");
        sb.append("PERFIL DE ORGANIZADOR\n");
        sb.append(super.mostrarPerfil());
        sb.append("\nEventos activos: ").append(eventosActivos.size()).append("\n");
        sb.append("Ganancias totales: $").append(String.format("%.2f", calcularGananciasTotales())).append("\n");
        sb.append("% Ventas global: ").append(String.format("%.2f", calcularPorcentajeVentasGlobal())).append("%\n");
        sb.append("=================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Organizador: " + super.toString() + " (Eventos activos: " + eventosActivos.size() + ")";
    }
}