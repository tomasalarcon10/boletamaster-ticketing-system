package tests;

import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import tiquetes.*;

import java.time.LocalDateTime;

/**
 * Prueba básica para verificar:
 * - Registro y autenticación de usuarios
 * - Creación de evento y localidad
 * - Compra y transferencia de tiquete entre clientes
 */
public class TestUsuariosYAuth {
    public static void main(String[] args) {
        SistemaVentaTiquetes sys = new SistemaVentaTiquetes();

        // Registro de usuarios
        Usuario c1 = sys.registrarUsuario("ana", "123", "Ana", "cliente");
        Usuario c2 = sys.registrarUsuario("bob", "abc", "Bob", "cliente");
        Usuario org = sys.registrarUsuario("org1", "ooo", "Org Uno", "organizador");
        Usuario admin = sys.registrarUsuario("root", "root", "Admin", "administrador");
        TestUtil.assertTrue(c1 != null && c2 != null && org != null && admin != null, "Registro de usuarios exitoso");

        // Autenticación
        Usuario log = sys.autenticar("ana", "123");
        TestUtil.assertTrue(log != null && log.getLogin().equals("ana"), "Autenticación correcta");
        sys.cerrarSesion();

        // Creación de venue y evento
        Organizador o = (Organizador) org;
        Venue v = new Venue("V1", "Coliseo", "Bogotá", 5000, "Sin restricciones", true);
        sys.registrarVenue(v);
        Evento e = new Evento("E1", "Rock Fest", LocalDateTime.now().plusDays(5), "musical", o, v);
        boolean added = sys.registrarEvento(e);
        TestUtil.assertTrue(added, "Evento registrado correctamente");

        // Crear localidad
        o.agregarLocalidad(e, "General", 100000, false, 1000, "BASICO");
        Localidad gen = e.buscarLocalidad("General");
        TestUtil.assertTrue(gen != null, "Localidad creada correctamente");

        // Crear tiquete y probar transferencia
        Cliente ana = (Cliente) c1;
        Cliente bob = (Cliente) c2;
        Tiquete t1 = new TiqueteBasico("T-1", 100000, 10000, 5000, ana, gen);
        ana.comprarTiquete(t1);

        boolean transfer = ana.transferirTiquete(t1, bob);
        TestUtil.assertTrue(transfer, "Transferencia de tiquete entre clientes");
        TestUtil.assertTrue(bob.getTiquetes().contains(t1), "El nuevo dueño recibió el tiquete");
        TestUtil.assertTrue(!ana.getTiquetes().contains(t1), "El cliente original ya no tiene el tiquete");

        TestUtil.summary("TestUsuariosYAuth");
    }
}
