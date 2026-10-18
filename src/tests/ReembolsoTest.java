package tests;

import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import tiquetes.*;
import finanzas.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica:
 * - Reembolso individual aprobado por administrador autenticado
 * - Monto se recarga al cliente y el tiquete cambia de estado
 */
public class ReembolsoTest {

    SistemaVentaTiquetes sys;
    Administrador admin;
    Cliente cli;
    Organizador org;
    Evento evento;
    Localidad loc;

    @BeforeEach
    void setup() {
        sys = new SistemaVentaTiquetes();
        admin = (Administrador) sys.registrarUsuario("admin", "admin", "Admin", "administrador");
        cli = (Cliente) sys.registrarUsuario("cli", "123", "Cliente", "cliente");
        org = (Organizador) sys.registrarUsuario("org", "o", "Org", "organizador");

        Venue v = new Venue("V1", "Arena", "Medellín", 8000, "Sin restricciones", true);
        sys.registrarVenue(v);
        evento = new Evento("E1", "Concierto", LocalDateTime.now().plusDays(3), "musical", org, v);
        assertTrue(sys.registrarEvento(evento));

        org.agregarLocalidad(evento, "General", 120_000, false, 500, "BASICO");
        loc = evento.buscarLocalidad("General");
        assertNotNull(loc);

        cli.recargarSaldo(500_000);
    }

    @Test
    void reembolsoIndividual_aprobadoPorAdmin() {
        // Compra
        List<Tiquete> carrito = new ArrayList<>();
        Tiquete t = new TiqueteBasico("TB-1", 120_000, 12_000, 5_000, cli, loc);
        carrito.add(t);

        Transaccion tx = sys.procesarCompra(cli, carrito, "saldo");
        assertNotNull(tx, "La compra debe completarse");

        double saldoDespuesCompra = cli.getSaldoVirtual();

        // Reembolso (requiere admin autenticado)
        sys.autenticar("admin", "admin");
        boolean ok = sys.procesarReembolsoIndividual(t, "calamidad");
        assertTrue(ok, "El reembolso debería ser aprobado por admin");

        assertEquals("reembolsado", t.getEstado(), "El tiquete debe quedar reembolsado");
        assertTrue(cli.getSaldoVirtual() > saldoDespuesCompra, "El saldo del cliente debe aumentar tras reembolso");
    }
}
