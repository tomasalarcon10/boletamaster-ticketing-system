package tests;

import org.junit.jupiter.api.*;
import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import tiquetes.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MarketplaceLogsTest {
    
    SistemaVentaTiquetes sistema;
    Cliente cliente;
    Administrador admin;
    
    @BeforeEach
    void setup() {
        sistema = new SistemaVentaTiquetes();
        cliente = (Cliente) sistema.registrarUsuario("cliente", "123", "Cliente", "cliente");
        admin = (Administrador) sistema.registrarUsuario("admin", "admin", "Admin", "administrador");
        
        Organizador org = (Organizador) sistema.registrarUsuario("org", "org", "Org", "organizador");
        Venue venue = new Venue("V1", "Plaza", "Cali", 2000, "", true);
        sistema.registrarVenue(venue);
        
        Evento evento = new Evento("E1", "Obra", LocalDateTime.now().plusDays(20), "cultural", org, venue);
        sistema.registrarEvento(evento);
        
        org.agregarLocalidad(evento, "VIP", 150000, true, 50, "NUMERADO");
        Localidad localidad = evento.buscarLocalidad("VIP");
        
        Tiquete tiquete = new TiqueteNumerado("T1", 150000, 15000, 5000, cliente, localidad, "A-1");
        cliente.agregarTiquete(tiquete);
    }
    
    @Test
    void logsRegistranAcciones_correctamente() {
        sistema.autenticar("cliente", "123");
        sistema.publicarOfertaReventa("T1", 180000);
        
        String idOferta = sistema.getOfertasActivasMarketplace().get(0).getId();
        sistema.eliminarMiOferta(idOferta);
        sistema.cerrarSesion();
        
        sistema.autenticar("admin", "admin");
        String logs = sistema.getLogMarketplace();
        
        assertNotNull(logs, "Debe retornar logs");
        assertTrue(logs.contains("OFERTA_PUBLICADA"), "Debe registrar publicación");
        assertTrue(logs.contains("OFERTA_ELIMINADA"), "Debe registrar eliminación");
    }
    
    @Test
    void soloAdministrador_puedeverLogs() {
        sistema.autenticar("cliente", "123");
        sistema.publicarOfertaReventa("T1", 180000);
        
        String logs = sistema.getLogMarketplace();
        
        assertTrue(logs.contains("Acceso denegado"), 
                  "Cliente no debe poder ver logs");
    }
}
