package finanzas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import eventos.Evento;
import usuarios.Organizador;

public class ReporteFinanciero {
    
    private List<Transaccion> transacciones;
    private ConfiguracionCargos configuracionCargos;

    public ReporteFinanciero(ConfiguracionCargos config) {
        this.transacciones = new ArrayList<>();
        this.configuracionCargos = config;
    }

    // -------------------- REGISTRO --------------------

    public void registrarTransaccion(Transaccion t) {
        if (t != null) {
            transacciones.add(t);
        }
    }

    // -------------------- CONSULTAS FINANCIERAS --------------------

    // Ver todos los cargos actuales del sistema (según UML: verCargos)
    public String verCargos() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== CONFIGURACIÓN DE CARGOS =====\n");
        sb.append("Cargo Musical: $").append(configuracionCargos.getCargoServicioMusical()).append("\n");
        sb.append("Cargo Cultural: $").append(configuracionCargos.getCargoServicioCultural()).append("\n");
        sb.append("Cargo Deportivo: $").append(configuracionCargos.getCargoServicioDeportivo()).append("\n");
        sb.append("Cargo Religioso: $").append(configuracionCargos.getCargoServicioReligioso()).append("\n");
        sb.append("Cuota de Emisión: $").append(configuracionCargos.getCuotaEmision()).append("\n");
        sb.append("===================================\n");
        return sb.toString();
    }

    // Ganancia total acumulada
    public double verGananciaTotal() {
        double total = 0;
        for (Transaccion t : transacciones) {
            total += t.getMonto();
        }
        return total;
    }

    // Ganancia por fecha específica
    public double verGananciaPorFecha(LocalDate fecha) {
        double total = 0;
        for (Transaccion t : transacciones) {
            if (t.getFecha().toLocalDate().equals(fecha)) {
                total += t.getMonto();
            }
        }
        return total;
    }

    // Ganancia generada por un evento
    public double verGananciaPorEvento(Evento e) {
        double total = 0;
        for (Transaccion t : transacciones) {
            if (t.getEvento() != null && t.getEvento().equals(e)) {
                total += t.getMonto();
            }
        }
        return total;
    }

    // Ganancia generada por un organizador
    public double verGananciaPorOrganizador(Organizador o) {
        double total = 0;
        for (Transaccion t : transacciones) {
            if (t.getEvento() != null &&
                t.getEvento().getOrganizador() != null &&
                t.getEvento().getOrganizador().equals(o)) {
                total += t.getMonto();
            }
        }
        return total;
    }

    // -------------------- REPORTE GENERAL --------------------

    public String generarReporteAdministrador() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== REPORTE FINANCIERO =====\n");
        sb.append("Total de transacciones: ").append(transacciones.size()).append("\n");
        sb.append("Ganancia total: $").append(String.format("%.2f", verGananciaTotal())).append("\n\n");

        double musical = 0, cultural = 0, deportivo = 0, religioso = 0;

        for (Transaccion t : transacciones) {
            if (t.getEvento() != null) {
                String tipo = t.getEvento().getTipo().toLowerCase();
                switch (tipo) {
                    case "musical" -> musical += t.getMonto();
                    case "cultural" -> cultural += t.getMonto();
                    case "deportivo" -> deportivo += t.getMonto();
                    case "religioso" -> religioso += t.getMonto();
                }
            }
        }

        sb.append("Ganancias por tipo de evento:\n");
        sb.append("Musical: $").append(musical).append("\n");
        sb.append("Cultural: $").append(cultural).append("\n");
        sb.append("Deportivo: $").append(deportivo).append("\n");
        sb.append("Religioso: $").append(religioso).append("\n");
        sb.append("==============================\n");

        return sb.toString();
    }
}
