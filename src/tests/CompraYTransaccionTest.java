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
 * - Recarga de saldo y compra con saldo
 * - Generación de transacción con monto esperado
 */
public class CompraYTransaccionTest {

    SistemaVentaTiquetes sys;
    Cliente cli;
    Organizador org;

    @BeforeEach
    void setup() {
        sys = new SistemaVentaTiquetes();
        cli = (Cliente) sys.registrarUsuario("cli", "123", "Cliente", "cliente");
        org = (Organizador) sys.registrarUsuario("org", "o", "Org", "organizador");
    }

    @Test
    void compraConSaldo_generacionTransaccion() {
        Venue v = new Venue("V1", "Arena", "Medellín", 8000, "Sin restricciones", true);
        sys.registrarVenue(v);

        Evento e = new Evento("E1", "Concierto", LocalDateTime.now().plusDays(2), "musical", org, v);
        assertTrue(sys.registrarEvento(e), "Evento debería registrarse");

        org.agregarLocalidad(e, "Platea", 200_000, true, 100, "NUMERADO");
        Localidad platea = e.buscarLocalidad("Platea");
        assertNotNull(platea, "Localidad debería existir");

        cli.recargarSaldo(1_000_000);

        List<Tiquete> carrito = new ArrayList<>();
        carrito.add(new TiqueteNumerado("TN-1", 200_000, 20_000, 5_000, cli, platea, "A-10"));

        Transaccion tx = sys.procesarCompra(cli, carrito, "saldo");
        assertNotNull(tx, "Compra debería generar una transacción");

        assertEquals(225_000.0, tx.getMonto(), 0.001, "Monto total esperado");
        assertTrue(cli.getSaldoVirtual() <= 1_000_000 - 225_000.0 + 0.001, "Saldo descontado");
    }
}
