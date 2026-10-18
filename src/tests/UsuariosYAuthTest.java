package tests;

import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import tiquetes.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica:
 * - Registro y autenticación de usuarios
 * - Creación de evento y localidad
 * - Transferencia de tiquete entre clientes
 */
public class UsuariosYAuthTest {

    SistemaVentaTiquetes sys;
    Cliente ana, bob;
    Organizador org;

    @BeforeEach
    void setup() {
        sys = new SistemaVentaTiquetes();
        ana = (Cliente) sys.registrarUsuario("ana", "123", "Ana", "cliente");
        bob = (Cliente) sys.registrarUsuario("bob", "abc", "Bob", "cliente");
        org = (Organizador) sys.registrarUsuario("org1", "ooo", "Org Uno", "organizador");
        sys.registrarUsuario("root", "root", "Admin", "administrador");
    }

    @Test
    void registroYAuth_basico() {
        Usuario log = sys.autenticar("ana", "123");
        assertNotNull(log, "Autenticación debería funcionar");
        assertEquals("ana", log.getLogin());
        sys.cerrarSesion();
    }

    @Test
    void transferenciaTiquete_entreClientes() {
        Venue v = new Venue("V1", "Coliseo", "Bogotá", 5000, "Sin restricciones", true);
        sys.registrarVenue(v);
        Evento e = new Evento("E1", "Rock Fest", LocalDateTime.now().plusDays(5), "musical", org, v);
        assertTrue(sys.registrarEvento(e), "Evento debe registrarse");

        org.agregarLocalidad(e, "General", 100_000, false, 1000, "BASICO");
        Localidad gen = e.buscarLocalidad("General");
        assertNotNull(gen, "Localidad debería existir");

        Tiquete t1 = new TiqueteBasico("T-1", 100_000, 10_000, 5_000, ana, gen);
        ana.comprarTiquete(t1);

        boolean ok = ana.transferirTiquete(t1, bob);
        assertTrue(ok, "La transferencia debería ser exitosa");
        assertTrue(bob.getTiquetes().contains(t1));
        assertFalse(ana.getTiquetes().contains(t1));
    }
}
