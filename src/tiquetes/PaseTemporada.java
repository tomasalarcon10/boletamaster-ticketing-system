package tiquetes;

import eventos.Localidad;
import usuarios.Cliente;
import eventos.Evento;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un pase que permite el acceso a múltiples eventos dentro
 * de un rango de fechas (por ejemplo, un pase de temporada).
 */
public class PaseTemporada extends Tiquete {

    private List<Evento> eventosIncluidos;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    /**
     * Constructor del pase de temporada.
     */
    public PaseTemporada(String id, double precioBase, double cargoServicio, double cuotaEmision,
                         Cliente dueño, Localidad localidad,
                         List<Evento> eventosIncluidos, LocalDate fechaInicio, LocalDate fechaFin) {

        super(id, precioBase, cargoServicio, cuotaEmision, dueño, localidad);
        this.eventosIncluidos = (eventosIncluidos != null) ? new ArrayList<>(eventosIncluidos) : new ArrayList<>();
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // ===== GETTERS =====

    public List<Evento> getEventosIncluidos() {
        return new ArrayList<>(eventosIncluidos);
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    // ===== FUNCIONALIDAD =====

    public void agregarEvento(Evento evento) {
        if (evento != null && !eventosIncluidos.contains(evento)) {
            eventosIncluidos.add(evento);
        }
    }

    public boolean cubreEvento(Evento evento) {
        return eventosIncluidos.contains(evento);
    }

    public boolean estaVigente() {
        return estaVigente(LocalDate.now());
    }

    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public int getCantidadEventos() {
        return eventosIncluidos.size();
    }

    private boolean algunEventoPaso() {
        for (Evento evento : eventosIncluidos) {
            if (evento.getFechaHora().isBefore(java.time.LocalDateTime.now())) {
                return true;
            }
        }
        return false;
    }

    public String resumenPase() {
        StringBuilder sb = new StringBuilder();
        sb.append("PASE DE TEMPORADA\n");
        sb.append("Vigencia: ").append(fechaInicio).append(" a ").append(fechaFin).append("\n");
        sb.append("Eventos incluidos (").append(eventosIncluidos.size()).append("):\n");

        for (int i = 0; i < eventosIncluidos.size(); i++) {
            Evento evt = eventosIncluidos.get(i);
            sb.append("  ").append(i + 1).append(". ").append(evt.getNombre());
            sb.append(" - ").append(evt.getFechaHora().toLocalDate()).append("\n");
        }

        return sb.toString();
    }

    // ===== SOBRESCRITOS =====

    @Override
    public boolean puedeTransferir() {
        if (!super.puedeTransferir()) {
            return false;
        }

        if (algunEventoPaso()) {
            System.out.println("No se puede transferir: al menos un evento ya pasó");
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== PASE DE TEMPORADA ==========\n");
        sb.append(getInfoBasica());
        sb.append("Vigencia: ").append(fechaInicio).append(" → ").append(fechaFin).append("\n");
        sb.append("Eventos incluidos: ").append(eventosIncluidos.size()).append("\n");
        sb.append("Estado vigencia: ").append(estaVigente() ? "VIGENTE" : "EXPIRADO").append("\n");
        sb.append("\n--- Eventos ---\n");
        for (Evento evt : eventosIncluidos) {
            sb.append("• ").append(evt.getNombre()).append(" - ")
              .append(evt.getFechaHora()).append("\n");
        }
        sb.append("════════════════════════════════════════");
        return sb.toString();
    }
}