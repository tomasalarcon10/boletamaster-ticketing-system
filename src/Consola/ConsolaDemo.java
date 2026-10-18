package Consola;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import sistema.SistemaVentaTiquetes;
import eventos.*;
import usuarios.*;
import tiquetes.Tiquete;
import tiquetes.TiqueteBasico;
public class ConsolaDemo {

    public static void main(String[] args) {
        SistemaVentaTiquetes sistema = new SistemaVentaTiquetes();

        System.out.println("===== DEMO DEL SISTEMA DE TIQUETES =====\n");

        // === 1. Crear usuarios ===
        Organizador org = (Organizador) sistema.registrarUsuario("org1", "123", "Carlos", "organizador");
        Administrador admin = (Administrador) sistema.registrarUsuario("admin", "123", "Sofía", "administrador");
        Cliente cliente = (Cliente) sistema.registrarUsuario("cliente1", "123", "Laura", "cliente");

        // === 2. Crear venue y aprobarlo ===
        Venue v1 = new Venue("V001", "Estadio Central", "Bogotá", 3000, "Sin restricciones", false);
        sistema.registrarVenue(v1);

        sistema.autenticar("admin", "123");
        sistema.aprobarVenue("V001");
        sistema.cerrarSesion();

        // === 3. Crear evento ===
        LocalDateTime fechaEvento = LocalDateTime.now().plusDays(7);
        Evento e1 = new Evento("E001", "Concierto Pop", fechaEvento, "musical", org, v1);
        sistema.registrarEvento(e1);

        // === 4. Crear localidades ===
        Localidad locGeneral = new Localidad("General", 80000, false, 500, "básico", e1);
        Localidad locVIP = new Localidad("VIP", 150000, true, 200, "premium", e1);

        e1.agregarLocalidad(locGeneral);
        e1.agregarLocalidad(locVIP);

        // Simular ventas:
        locGeneral.venderTiquetes(200);
        locVIP.venderTiquetes(150);

        // === 5. Mostrar resumen del evento ===
        System.out.println(sistema.generarResumenEvento(e1));

        // === 6. Mostrar información detallada del evento ===
        e1.mostrarInformacionEvento();

        // === 7. Mostrar resumen general del sistema ===
        System.out.println(sistema.generarReporteGeneral());
        
        // === 8. Simular compra real ===
        System.out.println("\n=== SIMULANDO COMPRA REAL ===");
        sistema.autenticar("cliente1", "123");

        // El cliente recarga saldo antes de comprar
        cliente.recargarSaldo(500000);
        System.out.println("Saldo del cliente: $" + cliente.getSaldoVirtual());

        // Crear lista de tiquetes
        List<Tiquete> tiquetesCompra = new ArrayList<>();

        // Crear tiquetes concretos (usando tu clase TiqueteBasico)
        tiquetesCompra.add(new TiqueteBasico("T1", locVIP.getPrecioFinal(), 10000, 5000, cliente, locVIP));
        tiquetesCompra.add(new TiqueteBasico("T2", locGeneral.getPrecioFinal(), 10000, 5000, cliente, locGeneral));

        // Procesar la compra.
        sistema.procesarCompra(cliente, tiquetesCompra, "saldo");
        sistema.cerrarSesion();
        
        // === 9. Simular un reembolso ===
        System.out.println("\n=== SIMULANDO REEMBOLSO ===");
        sistema.autenticar("admin", "123");

        // Reembolsar el primer tiquete de la compra
        Tiquete tiqueteReembolsado = tiquetesCompra.get(0);
        sistema.procesarReembolsoIndividual(tiqueteReembolsado, "Cancelación del cliente");
        System.out.println("Saldo del cliente después del reembolso: $" + cliente.getSaldoVirtual());
        
        // === 10. Guardar los datos del sistema ===
        sistema.getPersistencia().guardarDatos();
        System.out.println("Datos guardados en ./datos_sistema");
        sistema.cerrarSesion();
    }
}
