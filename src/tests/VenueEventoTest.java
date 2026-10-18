package tests;

import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica:
 * - Un administrador aprueba un venue
 * - Un organizador registra un evento
 * - No se permite otro evento el mismo día en el mismo venue
 */
public class VenueEventoTest {

    SistemaVentaTiquetes sys;
    Administrador admin;
    Organizador org;

    @BeforeEach
    void setup() {
        sys = new SistemaVentaTiquetes();
        admin = (Administrador) sys.registrarUsuario("admin", "admin", "Admin", "administrador");
        org = (Organizador) sys.registrarUsuario("org", "org", "Organizador", "organizador");
        sys.autenticar("admin", "admin");
    }

    @Test
    void aprobarVenue_y_conflictoEventosMismoDia() {
        Venue v1 = new Venue("V1", "Teatro Central", "Cali", 1200, "No fumar", false);
        sys.registrarVenue(v1);

        assertTrue(sys.aprobarVenue("V1"), "Admin debería aprobar el venue");
        sys.cerrarSesion();

        Evento e1 = new Evento("E1", "Obra A", LocalDateTime.now().plusDays(10), "cultural", org, v1);
        assertTrue(sys.registrarEvento(e1), "Primer evento debería registrarse");

        Evento e2 = new Evento("E2", "Obra B", e1.getFechaHora().plusHours(2), "cultural", org, v1);
        assertFalse(sys.registrarEvento(e2), "Segundo evento en misma fecha/venue debería fallar");
    }
}
