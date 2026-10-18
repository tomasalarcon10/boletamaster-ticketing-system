package finanzas;

import java.time.LocalDateTime;
import java.util.UUID;
import usuarios.Cliente;
import eventos.Evento;

public class Transaccion {
    private String id;
    private Cliente cliente;
    private Evento evento;
    private double monto;
    private LocalDateTime fecha;
    private String tipo;
    
    public Transaccion(String id, Cliente cliente, Evento evento, double monto, LocalDateTime fecha, String tipo) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.cliente = cliente;
        this.evento = evento;
        this.monto = monto;
        this.fecha = fecha;
        this.tipo = tipo;
    }
    
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public String getTipo() { return tipo; }
    public Cliente getCliente() { return cliente; }
    public Evento getEvento() { return evento; }
    public String getId() { return id; }

    // Métodos adicionales opcionales
    public String getNombreCliente() {
        return cliente != null ? cliente.getNombre() : "Desconocido";
    }

    public String getNombreEvento() {
        return evento != null ? evento.getNombre() : "Sin evento";
    }
}