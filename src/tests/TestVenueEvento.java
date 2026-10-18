package tests;

import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import java.time.LocalDateTime;

/**
 * Prueba para verificar:
 * - Que un administrador apruebe un venue
 * - Que un organizador registre un evento
 * - Que el sistema detecte conflictos de horario en el mismo venue
 */
public class TestVenueEvento {
    public static void main(String[] args) {
        SistemaVentaTiquetes sys = new SistemaVentaTiquetes();

        Usuario admin = sys.registrarUsuario("admin", "admin", "Admin", "administrador");
        Usuario org = sys.registrarUsuario("org", "org", "Organizador", "organizador");
        sys.autenticar("admin", "admin");

        // Registrar y aprobar un venue
        Venue v1 = new Venue("V1", "Teatro Central", "Cali", 1200, "No fumar", false);
        sys.registrarVenue(v1);
        boolean okApprove = sys.aprobarVenue("V1");
        TestUtil.assertTrue(okApprove, "El administrador aprueba el venue");

        sys.cerrarSesion();

        // Registrar evento sin conflicto
        Organizador o = (Organizador) org;
        Evento e1 = new Evento("E1", "Obra A", LocalDateTime.now().plusDays(10), "cultural", o, v1);
        boolean reg1 = sys.registrarEvento(e1);
        TestUtil.assertTrue(reg1, "Evento registrado sin conflicto");

        // Intentar otro evento el mismo día (conflicto)
        Evento e2 = new Evento("E2", "Obra B", e1.getFechaHora().plusHours(2), "cultural", o, v1);
        boolean reg2 = sys.registrarEvento(e2);
        TestUtil.assertTrue(!reg2, "Conflicto de fecha detectado correctamente");

        TestUtil.summary("TestVenueEvento");
    }
}
