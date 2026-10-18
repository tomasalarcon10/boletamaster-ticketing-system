package tiquetes;

import usuarios.Cliente;
import eventos.Localidad;

public class TiqueteNumerado extends Tiquete{
	
	private String asiento;
    private boolean ocupado;
    
    public TiqueteNumerado(String id, double precioBase, double cargoServicio, double cuotaEmision, Cliente dueño, Localidad localidad, String asiento) {
		super(id, precioBase, cargoServicio, cuotaEmision, dueño, localidad);
		this.asiento = asiento;
		this.ocupado = false;
	}
    
    public String getAsiento() {
        return asiento;
    }
    
    public boolean isOcupado() {
        return ocupado;
    }
    
    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }
    
    public void setNumeroAsiento(String nuevoNumero) {
        this.asiento = nuevoNumero;
    }
    
    public int getNumeroAsiento() {
        try {
            return Integer.parseInt(asiento);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public void marcarOcupado() {
        this.ocupado = true;
        marcarComoUsado();
        System.out.println("Asiento " + asiento + " marcado como ocupado");
    }
    
    public void liberarAsiento() {
        this.ocupado = false;
        System.out.println("Asiento " + asiento + " liberado");
    }
    
    public String getInfoAsiento() {
        return String.format("Asiento: %s %s", asiento, ocupado ? "(OCUPADO)" : "(DISPONIBLE)");
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== TIQUETE NUMERADO ==========\n");
        sb.append(getInfoBasica());
        sb.append("Asiento: ").append(asiento).append("\n");
        sb.append("Estado asiento: ").append(ocupado ? "Ocupado" : "Disponible").append("\n");
        if (getEvento() != null) {
            sb.append("Evento: ").append(getEvento().getNombre()).append("\n");
            sb.append("Fecha: ").append(getEvento().getFechaHora()).append("\n");
        }
        sb.append("════════════════════════════════════");
        return sb.toString();
    }

}
