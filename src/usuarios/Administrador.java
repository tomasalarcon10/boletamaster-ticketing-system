package usuarios;

import eventos.Evento;
import eventos.Venue;
import finanzas.ConfiguracionCargos;
import finanzas.ReporteFinanciero;
import finanzas.Rembolso;
import tiquetes.Tiquete;
import java.time.LocalDate;
import java.util.List;

public class Administrador extends Usuario{
	
	private ConfiguracionCargos configuracionCargos;
    private ReporteFinanciero reporteFinanciero;
    
    public Administrador(String login, String password, String nombre) {
        super(login, password, nombre);
    }
    
    public ConfiguracionCargos getConfiguracionCargos() {
        return configuracionCargos;
    }
    
    public void setConfiguracionCargos(ConfiguracionCargos configuracion) {
        this.configuracionCargos = configuracion;
    }
    
    public ReporteFinanciero getReporteFinanciero() {
        return reporteFinanciero;
    }
    
    public void setReporteFinanciero(ReporteFinanciero reporte) {
        this.reporteFinanciero = reporte;
    }
    
    public void actualizarCargoMusical(double nuevo) {
        if (configuracionCargos != null) {
            configuracionCargos.setCargoServicioMusical(nuevo);
            System.out.println("Cargo musical actualizado a: " + nuevo + "%");
        }
    }
    
    public void actualizarCargoCultural(double nuevo) {
        if (configuracionCargos != null) {
            configuracionCargos.setCargoServicioCultural(nuevo);
            System.out.println("Cargo cultural actualizado a: " + nuevo + "%");
        }
    }
    
    public void actualizarCargoDeportivo(double nuevo) {
        if (configuracionCargos != null) {
            configuracionCargos.setCargoServicioDeportivo(nuevo);
            System.out.println("Cargo deportivo actualizado a: " + nuevo + "%");
        }
    }
    
    public void actualizarCargoReligioso(double nuevo) {
        if (configuracionCargos != null) {
            configuracionCargos.setCargoServicioReligioso(nuevo);
            System.out.println("Cargo religioso actualizado a: " + nuevo + "%");
        }
    }
    
    public void actualizarCuotaEmision(double nueva) {
        if (configuracionCargos != null) {
            configuracionCargos.setCuotaEmision(nueva);
            System.out.println("Cuota de emisión actualizada a: $" + nueva);
        }
    }
    
    public boolean aprobarVenue(Venue venue) {
        if (venue == null) {
            return false;
        }
        
        venue.setAprobado(true);
        System.out.println("Venue aprobado: " + venue.getNombre());
        return true;
    }
    
    public void rechazarVenue(Venue venue, String motivo) {
        if (venue != null) {
            System.out.println("Venue rechazado: " + venue.getNombre());
            System.out.println("  Motivo: " + motivo);
        }
    }
	
    public boolean cancelarEvento(Evento evento, String motivo) {
        if (evento == null) {
            return false;
        }
        
        System.out.println("Cancelando evento: " + evento.getNombre());
        System.out.println("  Motivo: " + motivo);
        
        evento.setEstado("cancelado");
        
        System.out.println("Evento cancelado. Procesando reembolsos...");
        return true;
    }
    
    public boolean autorizarCancelacionOrganizador(Evento evento) {
        if (evento == null) {
            return false;
        }
        
        System.out.println("Cancelación autorizada para: " + evento.getNombre());
        System.out.println("  Tipo de reembolso: Solo precio base");
        
        evento.setEstado("cancelado");
        return true;
    }
    
    public void procesarReembolsoEvento(Evento evento, String tipoReembolso) {
        if (evento == null) {
            return;
        }
        
        System.out.println("Procesando reembolsos para: " + evento.getNombre());
        System.out.println("Tipo de reembolso: " + tipoReembolso);

    }
    
    public boolean autorizarReembolsoUsuario(Tiquete tiquete, String motivo) {
        if (tiquete == null) {
            return false;
        }
        
        System.out.println("Evaluando solicitud de reembolso por calamidad");
        System.out.println("Tiquete: " + tiquete.getId());
        System.out.println("Motivo: " + motivo);
        System.out.println("Reembolso autorizado");
        return true;
    }
    
    public double verGananciaTotal() {
        if (reporteFinanciero != null) {
            return reporteFinanciero.verGananciaTotal();
        }
        return 0.0;
    }
    
    public double verGananciaPorFecha(LocalDate fecha) {
        if (reporteFinanciero != null) {
            return reporteFinanciero.verGananciaPorFecha(fecha);
        }
        return 0.0;
    }
    
    public double verGananciaPorEvento(Evento evento) {
        if (reporteFinanciero != null) {
            return reporteFinanciero.verGananciaPorEvento(evento);
        }
        return 0.0;
    }
    
    public double verGananciaPorOrganizador(Organizador organizador) {
        if (reporteFinanciero != null) {
            return reporteFinanciero.verGananciaPorOrganizador(organizador);
        }
        return 0.0;
    }
    
    public String generarReporteCompleto() {
        if (reporteFinanciero == null) {
            return "No hay datos de reporte disponibles";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("========== REPORTE FINANCIERO DEL SISTEMA ==========\n");
        sb.append("Ganancias Totales: $").append(String.format("%.2f", verGananciaTotal())).append("\n");
        sb.append("\n").append(reporteFinanciero.generarReporteAdministrador());
        
        return sb.toString();
    }
  
    
    @Override
    public String mostrarPerfil() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== PERFIL DE ADMINISTRADOR ==========\n");
        sb.append("Login: ").append(getLogin()).append("\n");
        sb.append("Nombre: ").append(getNombre()).append("\n");
        sb.append("Rol: Administrador del Sistema\n");
        
        if (configuracionCargos != null) {
            sb.append("\n--- Configuración de Cargos ---\n");
            sb.append("Musical: ").append(configuracionCargos.getCargoServicioMusical()).append("%\n");
            sb.append("Cultural: ").append(configuracionCargos.getCargoServicioCultural()).append("%\n");
            sb.append("Deportivo: ").append(configuracionCargos.getCargoServicioDeportivo()).append("%\n");
            sb.append("Religioso: ").append(configuracionCargos.getCargoServicioReligioso()).append("%\n");
            sb.append("Cuota emisión: $").append(configuracionCargos.getCuotaEmision()).append("\n");
        }
        
        sb.append("\nGanancias del sistema: $").append(String.format("%.2f", verGananciaTotal()));
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "Administrador: " + super.toString();
    }

}
