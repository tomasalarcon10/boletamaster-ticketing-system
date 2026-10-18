package finanzas; 
public class ConfiguracionCargos {
    
    private double cargoServicioMusical;
    private double cargoServicioCultural;
    private double cargoServicioDeportivo;
    private double cargoServicioReligioso;
    private double cuotaEmision;
    
    // Constructor vacío (para compatibilidad con persistencia)
    public ConfiguracionCargos() {}

    // Constructor con parámetros iniciales
    public ConfiguracionCargos(double musical, double cultural, double deportivo, double religioso, double cuota) {
        this.cargoServicioMusical = musical;
        this.cargoServicioCultural = cultural;
        this.cargoServicioDeportivo = deportivo;
        this.cargoServicioReligioso = religioso;
        this.cuotaEmision = cuota;
    }
    
    // Getters y Setters
    public double getCargoServicioMusical() { return cargoServicioMusical; }
    public void setCargoServicioMusical(double cargoServicioMusical) { this.cargoServicioMusical = cargoServicioMusical; }

    public double getCargoServicioCultural() { return cargoServicioCultural; }
    public void setCargoServicioCultural(double cargoServicioCultural) { this.cargoServicioCultural = cargoServicioCultural; }

    public double getCargoServicioDeportivo() { return cargoServicioDeportivo; }
    public void setCargoServicioDeportivo(double cargoServicioDeportivo) { this.cargoServicioDeportivo = cargoServicioDeportivo; }

    public double getCargoServicioReligioso() { return cargoServicioReligioso; }
    public void setCargoServicioReligioso(double cargoServicioReligioso) { this.cargoServicioReligioso = cargoServicioReligioso; }

    public double getCuotaEmision() { return cuotaEmision; }
    public void setCuotaEmision(double cuotaEmision) { this.cuotaEmision = cuotaEmision; }

    // Devuelve el porcentaje de cargo por tipo
    public double obtenerCargoPorTipo(String tipoEvento) {
        tipoEvento = tipoEvento.toLowerCase();
        return switch (tipoEvento) {
            case "musical" -> cargoServicioMusical;
            case "cultural" -> cargoServicioCultural;
            case "deportivo" -> cargoServicioDeportivo;
            case "religioso" -> cargoServicioReligioso;
            default -> 0.0;
        };
    }

    // Calcula el cargo monetario aplicando el porcentaje al precio base
    public double calcularCargoPorTipo(String tipoEvento, double precioBase) {
        double porcentaje = obtenerCargoPorTipo(tipoEvento);
        return precioBase * porcentaje;
    }

    @Override
    public String toString() {
        return "Configuración de cargos: " +
               "Musical=" + cargoServicioMusical + ", " +
               "Cultural=" + cargoServicioCultural + ", " +
               "Deportivo=" + cargoServicioDeportivo + ", " +
               "Religioso=" + cargoServicioReligioso + ", " +
               "Cuota Emisión=" + cuotaEmision;
    }
}