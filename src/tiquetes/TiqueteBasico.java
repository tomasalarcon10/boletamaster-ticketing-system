package tiquetes;

import usuarios.Cliente;
import eventos.Localidad;

/**
 * Representa un tiquete básico sin beneficios adicionales.
 */
public class TiqueteBasico extends Tiquete {

    // Constructor PÚBLICO
    public TiqueteBasico(String id, double precioBase, double cargoServicio, 
                         double cuotaEmision, Cliente dueño, Localidad localidad) {
        super(id, precioBase, cargoServicio, cuotaEmision, dueño, localidad);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== TIQUETE BÁSICO ==========\n");
        sb.append(getInfoBasica());
        if (getEvento() != null) {
            sb.append("Evento: ").append(getEvento().getNombre()).append("\n");
            sb.append("Fecha: ").append(getEvento().getFechaHora()).append("\n");
        }
        sb.append("════════════════════════════════════\n");
        return sb.toString();
    }
}