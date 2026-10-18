package tiquetes;

public class Beneficio {

    private String id;
    private String descripcion;
    private String tipo;
    private double valorEstimado;

    // ===== CONSTRUCTORES =====
    public Beneficio(String id, String descripcion, String tipo, double valorEstimado) {
        this.id = id;
        this.descripcion = descripcion;
        this.tipo = tipo;
        setValorEstimado(valorEstimado);
    }

    public Beneficio(String id, String descripcion, String tipo) {
        this(id, descripcion, tipo, 0.0);
    }

    // ===== GETTERS =====
    public String getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public double getValorEstimado() {
        return valorEstimado;
    }

    // ===== SETTERS =====
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setValorEstimado(double valor) {
        if (valor < 0) {
            System.out.println("Error: el valor estimado no puede ser negativo.");
            return;
        }
        this.valorEstimado = valor;
    }

    // ===== MÉTODOS FUNCIONALES =====
    public String getDetalles() {
        StringBuilder sb = new StringBuilder();
        sb.append("Beneficio: ").append(descripcion).append("\n");
        sb.append("Tipo: ").append(tipo).append("\n");
        if (valorEstimado > 0) {
            sb.append("Valor estimado: $").append(String.format("%.2f", valorEstimado));
        }
        return sb.toString();
    }

    /** Permite crear una copia del beneficio actual */
    public Beneficio copiar() {
        return new Beneficio(id, descripcion, tipo, valorEstimado);
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format("[%s] %s - %s", 
                tipo.toUpperCase(), 
                descripcion, 
                valorEstimado > 0 ? "$" + String.format("%.2f", valorEstimado) : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Beneficio beneficio = (Beneficio) obj;
        return id.equals(beneficio.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

