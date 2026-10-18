package tests;

import org.junit.jupiter.api.*;
import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import tiquetes.*;
import marketplace.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MarketplaceOfertasTest {
    
    SistemaVentaTiquetes sistema;
    Cliente vendedor, comprador;
    Organizador org;
    Evento evento;
    Localidad localidad;
    Tiquete tiquete;
    
    @BeforeEach
    void setup() {
        sistema = new SistemaVentaTiquetes();
        vendedor = (Cliente) sistema.registrarUsuario("vendedor", "123", "Vendedor", "cliente");
        comprador = (Cliente) sistema.registrarUsuario("comprador", "abc", "Comprador", "cliente");
        org = (Organizador) sistema.registrarUsuario("org", "org", "Organizador", "organizador");
        
        Venue venue = new Venue("V1", "Teatro", "Bogotá", 1000, "Sin restricciones", true);
        sistema.registrarVenue(venue);
        
        evento = new Evento("E1", "Concierto Rock", LocalDateTime.now().plusDays(10), "musical", org, venue);
        sistema.registrarEvento(evento);
        
        org.agregarLocalidad(evento, "Platea", 100000, true, 200, "NUMERADO");
        localidad = evento.buscarLocalidad("Platea");
        
        tiquete = new TiqueteBasico("T1", 100000, 10000, 5000, vendedor, localidad);
        vendedor.agregarTiquete(tiquete);
    }
    
    @Test
    void publicarOferta_exitoso() {
        sistema.autenticar("vendedor", "123");
        boolean resultado = sistema.publicarOfertaReventa("T1", 120000);
        assertTrue(resultado, "Debe poder publicar una oferta");
        
        List<OfertaReventa> ofertas = sistema.getOfertasActivasMarketplace();
        assertEquals(1, ofertas.size(), "Debe haber 1 oferta activa");
        assertEquals(120000, ofertas.get(0).getPrecioVenta(), 0.01);
    }
    
    @Test
    void eliminarOferta_propietario() {
        sistema.autenticar("vendedor", "123");
        sistema.publicarOfertaReventa("T1", 120000);
        
        List<OfertaReventa> ofertas = sistema.getOfertasActivasMarketplace();
        String idOferta = ofertas.get(0).getId();
        
        boolean eliminada = sistema.eliminarMiOferta(idOferta);
        assertTrue(eliminada, "El propietario debe poder eliminar su oferta");
        assertEquals(0, sistema.getOfertasActivasMarketplace().size());
    }
    
    @Test
    void eliminarOferta_administrador() {
        sistema.autenticar("vendedor", "123");
        sistema.publicarOfertaReventa("T1", 120000);
        sistema.cerrarSesion();
        
        Administrador admin = (Administrador) sistema.registrarUsuario("admin", "admin", "Admin", "administrador");
        sistema.autenticar("admin", "admin");
        
        String idOferta = sistema.getOfertasActivasMarketplace().get(0).getId();
        boolean eliminada = sistema.eliminarOfertaAdministrador(idOferta);
        
        assertTrue(eliminada, "Administrador debe poder eliminar cualquier oferta");
        assertEquals(0, sistema.getOfertasActivasMarketplace().size());
    }
    
    @Test
    void noPublicarOferta_tiqueteNoRevendible() {
        // PaqueteDeluxe no es revendible
        PaqueteDeluxe paquete = new PaqueteDeluxe("PD1", 500000, 50000, 5000, 
                                                   vendedor, localidad, null, null);
        vendedor.agregarTiquete(paquete);
        
        sistema.autenticar("vendedor", "123");
        boolean resultado = sistema.publicarOfertaReventa("PD1", 600000);
        
        assertFalse(resultado, "No debe permitir publicar tiquetes no revendibles");
    }
}