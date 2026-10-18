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

/**
 * Pruebas unitarias para el flujo de contraofertas del Marketplace.
 * Se validan los casos de éxito, rechazo y restricciones.
 */
public class MarketplaceContraOfertasTest {

    SistemaVentaTiquetes sistema;
    Cliente vendedor, comprador;
    Organizador org;
    Tiquete tiquete;

    @BeforeEach
    void setup() {
        // Se crea un sistema nuevo antes de cada prueba
        sistema = new SistemaVentaTiquetes();

        // Se registran los usuarios necesarios
        vendedor  = (Cliente) sistema.registrarUsuario("vendedor", "123", "Vendedor", "cliente");
        comprador = (Cliente) sistema.registrarUsuario("comprador", "abc", "Comprador", "cliente");
        org       = (Organizador) sistema.registrarUsuario("org", "org", "Org", "organizador");

        // Se crea un venue aprobado y un evento del organizador
        Venue venue = new Venue("V1", "Estadio", "Medellín", 5000, "", true);
        sistema.registrarVenue(venue);
        Evento evento = new Evento("E1", "Festival", LocalDateTime.now().plusDays(15), "musical", org, venue);
        sistema.registrarEvento(evento);

        // Se agrega una localidad general y se crea un tiquete para el vendedor
        org.agregarLocalidad(evento, "General", 80000, false, 1000, "BASICO");
        Localidad localidad = evento.buscarLocalidad("General");

        tiquete = new TiqueteBasico("T1", 80000, 8000, 5000, vendedor, localidad);
        vendedor.agregarTiquete(tiquete);

        // El comprador tiene saldo suficiente para negociar
        comprador.recargarSaldo(200000);
        // El vendedor ya inicia con saldo 0, no es necesario recargar
    }

    @Test
    void hacerContraOferta_exitoso() {
        // El vendedor publica una oferta de reventa
        sistema.autenticar("vendedor", "123");
        sistema.publicarOfertaReventa("T1", 100000);
        assertFalse(sistema.getOfertasActivasMarketplace().isEmpty(), "Debe existir una oferta publicada");
        String idOferta = sistema.getOfertasActivasMarketplace().get(0).getId();
        sistema.cerrarSesion();

        // El comprador hace una contraoferta válida
        sistema.autenticar("comprador", "abc");
        boolean resultado = sistema.hacerContraOferta(idOferta, 85000);
        assertTrue(resultado, "Debe permitir hacer una contraoferta");

        // El vendedor debe ver la contraoferta pendiente
        sistema.cerrarSesion();
        sistema.autenticar("vendedor", "123");
        List<ContraOferta> contraofertas = sistema.getContraOfertasPendientes();
        assertEquals(1, contraofertas.size(), "Debe existir una contraoferta pendiente");
    }

    @Test
    void aceptarContraOferta_transaccionExitosa() {
        // El vendedor publica la oferta
        sistema.autenticar("vendedor", "123");
        sistema.publicarOfertaReventa("T1", 100000);
        String idOferta = sistema.getOfertasActivasMarketplace().get(0).getId();
        sistema.cerrarSesion();

        // El comprador hace una contraoferta
        sistema.autenticar("comprador", "abc");
        sistema.hacerContraOferta(idOferta, 90000);
        sistema.cerrarSesion();

        // El vendedor acepta la contraoferta
        sistema.autenticar("vendedor", "123");
        List<ContraOferta> contraofertas = sistema.getContraOfertasPendientes();
        assertEquals(1, contraofertas.size(), "Debe haber una contraoferta pendiente");

        String idContraOferta = contraofertas.get(0).getId();
        double saldoVendedorAntes = vendedor.getSaldoVirtual();
        double saldoCompradorAntes = comprador.getSaldoVirtual();

        boolean aceptada = sistema.aceptarContraOferta(idContraOferta);
        assertTrue(aceptada, "La contraoferta debe aceptarse correctamente");

        // Se verifica el intercambio de dinero y de propiedad
        assertEquals(saldoVendedorAntes + 90000, vendedor.getSaldoVirtual(), 0.01, "El vendedor recibe el pago");
        assertEquals(saldoCompradorAntes - 90000, comprador.getSaldoVirtual(), 0.01, "El comprador paga el monto");
        assertEquals(comprador, tiquete.getDueño(), "El tiquete pasa al comprador");
        assertEquals(0, sistema.getOfertasActivasMarketplace().size(), "La oferta se elimina tras la venta");
    }

    @Test
    void rechazarContraOferta_sinCambios() {
        // El vendedor publica una oferta
        sistema.autenticar("vendedor", "123");
        sistema.publicarOfertaReventa("T1", 100000);
        String idOferta = sistema.getOfertasActivasMarketplace().get(0).getId();
        sistema.cerrarSesion();

        // El comprador realiza una contraoferta
        sistema.autenticar("comprador", "abc");
        sistema.hacerContraOferta(idOferta, 70000);
        sistema.cerrarSesion();

        // El vendedor rechaza la contraoferta
        sistema.autenticar("vendedor", "123");
        String idContraOferta = sistema.getContraOfertasPendientes().get(0).getId();
        sistema.rechazarContraOferta(idContraOferta);

        // No debe cambiar la propiedad ni eliminar la oferta
        assertEquals(0, sistema.getContraOfertasPendientes().size(), "La contraoferta debe eliminarse");
        assertEquals(vendedor, tiquete.getDueño(), "El tiquete sigue siendo del vendedor");
        assertEquals(1, sistema.getOfertasActivasMarketplace().size(), "La oferta sigue activa");
    }

    @Test
    void noHacerContraOferta_aPropiaOferta() {
        // El vendedor publica su propia oferta
        sistema.autenticar("vendedor", "123");
        sistema.publicarOfertaReventa("T1", 100000);
        String idOferta = sistema.getOfertasActivasMarketplace().get(0).getId();

        // Intenta contraofertar sobre su propia publicación
        boolean resultado = sistema.hacerContraOferta(idOferta, 95000);
        assertFalse(resultado, "No debe permitir hacer una contraoferta a su propia oferta");
    }
}
