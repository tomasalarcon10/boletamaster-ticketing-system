package tests;

import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import tiquetes.*;
import finanzas.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Prueba que simula el flujo de compra:
 * - Un cliente recarga su saldo
 * - Compra un tiquete numerado
 * - Se genera una transacción con el monto esperado
 */
public class TestCompraYTransaccion {
    public static void main(String[] args) {
        SistemaVentaTiquetes sys = new SistemaVentaTiquetes();

        Cliente cli = (Cliente) sys.registrarUsuario("cli", "123", "Cliente", "cliente");
        Organizador org = (Organizador) sys.registrarUsuario("org", "o", "Org", "organizador");

        // Crear venue y evento
        Venue v = new Venue("V1", "Arena", "Medellín", 8000, "Sin restricciones", true);
        sys.registrarVenue(v);
        Evento e = new Evento("E1", "Concierto", LocalDateTime.now().plusDays(2), "musical", org, v);
        boolean ok = sys.registrarEvento(e);
        TestUtil.assertTrue(ok, "Evento registrado correctamente");

        // Crear localidad
        org.agregarLocalidad(e, "Platea", 200000, true, 100, "NUMERADO");
        Localidad platea = e.buscarLocalidad("Platea");

        // Recargar saldo y realizar compra
        cli.recargarSaldo(1000000);
        List<Tiquete> carrito = new ArrayList<>();
        carrito.add(new TiqueteNumerado("TN-1", 200000, 20000, 5000, cli, platea, "A-10"));

        Transaccion tx = sys.procesarCompra(cli, carrito, "saldo");
        TestUtil.assertTrue(tx != null, "Compra procesada correctamente");
        TestUtil.assertDoubleEquals(225000.0, tx.getMonto(), 0.001, "Monto total correcto");
        TestUtil.assertTrue(cli.getSaldoVirtual() < 1000000, "Saldo descontado correctamente");

        TestUtil.summary("TestCompraYTransaccion");
    }
}
