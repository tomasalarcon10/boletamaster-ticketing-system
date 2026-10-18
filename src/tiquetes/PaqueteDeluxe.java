package tiquetes;

import eventos.Localidad;
import usuarios.Cliente;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un paquete "Deluxe" que incluye varios tiquetes
 * y beneficios adicionales. No puede transferirse.
 */
public class PaqueteDeluxe extends Tiquete {

    private List<Tiquete> tiquetesIncluidos;
    private List<Beneficio> beneficios;

    /**
     * Constructor principal del paquete deluxe.
     */
    public PaqueteDeluxe(String id, double precioBase, double cargoServicio, double cuotaEmision,
                         Cliente dueño, Localidad localidad,
                         List<Tiquete> tiquetesIncluidos, List<Beneficio> beneficios) {

        super(id, precioBase, cargoServicio, cuotaEmision, dueño, localidad);

        // Evita null pointers al construir el objeto
        this.tiquetesIncluidos = (tiquetesIncluidos != null) ? new ArrayList<>(tiquetesIncluidos) : new ArrayList<>();
        this.beneficios = (beneficios != null) ? new ArrayList<>(beneficios) : new ArrayList<>();

        // Los paquetes Deluxe no son transferibles
        setTransferible(false);
    }

    // ===== GETTERS =====

    public List<Tiquete> getTiquetesIncluidos() {
        return new ArrayList<>(tiquetesIncluidos);
    }

    public List<Beneficio> getBeneficios() {
        return new ArrayList<>(beneficios);
    }

    // ===== FUNCIONALIDAD =====

    public void agregarTiquete(Tiquete tiquete) {
        if (tiquete != null && !tiquetesIncluidos.contains(tiquete)) {
            tiquetesIncluidos.add(tiquete);
            tiquete.setTransferible(false); // Los tiquetes internos tampoco se pueden transferir
        }
    }

    public void agregarBeneficio(Beneficio beneficio) {
        if (beneficio != null && !beneficios.contains(beneficio)) {
            beneficios.add(beneficio);
        }
    }

    public int getCantidadTiquetes() {
        return tiquetesIncluidos.size();
    }

    public int getCantidadBeneficios() {
        return beneficios.size();
    }

    /**
     * Calcula el valor total estimado de todos los beneficios del paquete.
     */
    public double calcularValorBeneficios() {
        double total = 0.0;
        for (Beneficio beneficio : beneficios) {
            total += beneficio.getValorEstimado();
        }
        return total;
    }

    /**
     * Genera un resumen detallado del paquete Deluxe.
     */
    public String resumenPaquete() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== PAQUETE DELUXE ==========\n");
        sb.append("ID: ").append(getId()).append("\n");
        sb.append("Precio: $").append(String.format("%.2f", calcularPrecioFinal())).append("\n\n");

        sb.append("--- TIQUETES INCLUIDOS (").append(getCantidadTiquetes()).append(") ---\n");
        for (int i = 0; i < tiquetesIncluidos.size(); i++) {
            Tiquete t = tiquetesIncluidos.get(i);
            sb.append(i + 1).append(". ");
            if (t.getLocalidad() != null) {
                sb.append(t.getLocalidad().getNombre());
            } else {
                sb.append("Tiquete ").append(t.getId());
            }
            sb.append("\n");
        }

        sb.append("\n--- BENEFICIOS INCLUIDOS (").append(getCantidadBeneficios()).append(") ---\n");
        for (int i = 0; i < beneficios.size(); i++) {
            Beneficio b = beneficios.get(i);
            sb.append(i + 1).append(". ").append(b.getDescripcion());
            sb.append(" [").append(b.getTipo()).append("]");
            if (b.getValorEstimado() > 0) {
                sb.append(" - $").append(String.format("%.2f", b.getValorEstimado()));
            }
            sb.append("\n");
        }

        sb.append("\nValor total en beneficios: $")
          .append(String.format("%.2f", calcularValorBeneficios())).append("\n");
        sb.append("════════════════════════════════════════");
        return sb.toString();
    }

    // ===== SOBRESCRITOS =====

    @Override
    public boolean puedeTransferir() {
        return false;
    }

    @Override
    public boolean transferir(Cliente nuevoDueño, String passwordActual) {
        System.out.println("✗ Los paquetes Deluxe no pueden ser transferidos");
        return false;
    }

    @Override
    public String toString() {
        return resumenPaquete();
    }
    
    @Override
    public boolean esRevendible() {
        return false;
    }
}