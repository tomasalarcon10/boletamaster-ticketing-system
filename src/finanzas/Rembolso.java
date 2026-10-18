package finanzas;

import java.time.LocalDateTime;
import java.util.UUID;
import usuarios.Cliente;
import usuarios.Usuario;
import eventos.Evento;

public class Rembolso {
    private String id;
    private double montoReembolsado;
    private LocalDateTime fecha;
    private Evento evento;
    private Cliente cliente;  // Cambiado de Usuario a Cliente
    
    public Rembolso(String id, Cliente cliente, Evento evento, double montoReembolsado, LocalDateTime fecha) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.cliente = cliente;
        this.evento = evento;
        this.montoReembolsado = montoReembolsado;
        this.fecha = fecha;
    }
    
    public void procesarReembolso() {
        if (cliente != null) {
            cliente.recargarSaldo(montoReembolsado);
            System.out.println("Reembolso procesado para " + cliente.getNombre() +
                               " por valor de $" + montoReembolsado);
        } else {
            System.out.println("Error: no se puede procesar reembolso, cliente nulo.");
        }
    }

    // Getters útiles para persistencia
    public String getId() { return id; }
    public double getMontoReembolsado() { return montoReembolsado; }
    public LocalDateTime getFecha() { return fecha; }
    public Evento getEvento() { return evento; }
    public Cliente getCliente() { return cliente; }
}