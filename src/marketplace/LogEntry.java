package marketplace;

import usuarios.Usuario;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    private String id;
    private LocalDateTime fechaHora;
    private Usuario usuario;
    private String accion;
    private String detalles;
    
    private static final DateTimeFormatter FMT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogEntry(String id, Usuario usuario, String accion, String detalles) {
        this.id = id;
        this.fechaHora = LocalDateTime.now();
        this.usuario = usuario;
        this.accion = accion;
        this.detalles = detalles;
    }

    // Getters
    public String getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public Usuario getUsuario() { return usuario; }
    public String getAccion() { return accion; }
    public String getDetalles() { return detalles; }
    
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s", 
                fechaHora.format(FMT), 
                usuario.getLogin(), 
                accion, 
                detalles);
    }
}