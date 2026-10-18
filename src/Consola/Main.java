package Consola;

import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.Venue;
import eventos.Evento;
import eventos.Localidad;
import tiquetes.TiqueteBasico;

import java.time.LocalDateTime;

/**
 * Clase principal que coordina todos los tipos de usuarios del sistema
 * CON INICIALIZACIÓN FORZADA DE DATOS Y MANEJO ROBUSTO DE ERRORES
 */
public class Main {
    
    private static void inicializarDatosForzados(SistemaVentaTiquetes sistema) {
        System.out.println("INICIALIZANDO DATOS FORZADOS...");
        
        try {
            // ===== CREAR USUARIOS =====
            System.out.println("Creando usuarios por defecto...");
            sistema.registrarUsuario("cliente1", "123", "Juan Pérez", "cliente");
            sistema.registrarUsuario("cliente2", "123", "María García", "cliente");
            sistema.registrarUsuario("organizador1", "123", "Carlos Eventos", "organizador");
            sistema.registrarUsuario("admin", "123", "Admin Principal", "administrador");
            
            // ===== RECARGAR SALDOS =====
            System.out.println("Recargando saldos...");
            Usuario cliente1 = sistema.buscarUsuario("cliente1");
            if (cliente1 != null) {
                cliente1.recargarSaldo(100000.0);
                System.out.println("cliente1: $100,000");
            }
            
            Usuario cliente2 = sistema.buscarUsuario("cliente2");
            if (cliente2 != null) {
                cliente2.recargarSaldo(50000.0);
                System.out.println("cliente2: $50,000");
            }
            
            // ===== CREAR VENUES =====
            System.out.println("Creando venues por defecto...");
            Venue venue1 = new Venue("v1", "Estadio Atanasio", "Medellín", 50000, "Ninguna", true);
            Venue venue2 = new Venue("v2", "Teatro Metro", "Bogotá", 2000, "Solo eventos culturales", true);
            Venue venue3 = new Venue("v3", "Plaza de Toros", "Cali", 15000, "Pendiente", false);
            
            sistema.registrarVenue(venue1);
            sistema.registrarVenue(venue2);
            sistema.registrarVenue(venue3);
            
            System.out.println("   * " + venue1.getNombre());
            System.out.println("   * " + venue2.getNombre());
            System.out.println("   * " + venue3.getNombre());
            
            // ===== CREAR EVENTOS DE PRUEBA =====
            System.out.println("Creando eventos de prueba...");
            Organizador org = (Organizador) sistema.buscarUsuario("organizador1");
            
            if (org != null) {
                // Evento 1
                Evento evento1 = new Evento("e1", "Concierto Rock 2024", 
                    LocalDateTime.now().plusDays(30), "musical", org, venue1);
                Localidad localidad1 = new Localidad("General", 80000, false, 1000, "basico", evento1);
                evento1.agregarLocalidad(localidad1);
                sistema.registrarEvento(evento1);
                System.out.println("   * " + evento1.getNombre());
                
                // Evento 2
                Evento evento2 = new Evento("e2", "Festival Cultural", 
                    LocalDateTime.now().plusDays(15), "cultural", org, venue2);
                Localidad localidad2 = new Localidad("Platea", 50000, true, 200, "numerado", evento2);
                evento2.agregarLocalidad(localidad2);
                sistema.registrarEvento(evento2);
                System.out.println("   * " + evento2.getNombre());
            }
            
            System.out.println("DATOS FORZADOS INICIALIZADOS EXITOSAMENTE");
            System.out.println("4 usuarios creados");
            System.out.println("3 venues creados"); 
            System.out.println("2 eventos creados");
            
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO en inicialización: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void verificarEstadoSistema(SistemaVentaTiquetes sistema) {
        System.out.println("\n ESTADO DEL SISTEMA:");
        System.out.println("Usuarios: " + sistema.getUsuarios().size());
        System.out.println("Venues: " + sistema.getVenues().size());
        System.out.println("Eventos: " + sistema.getEventos().size());
        System.out.println("Transacciones: " + sistema.getTransacciones().size());
        System.out.println("Reembolsos: " + sistema.getReembolsos().size());
        System.out.println("Ofertas Marketplace: " + sistema.getMarketplace().getOfertasActivas().size());
    }
    
    public static void main(String[] args) {
        System.out.println("=" .repeat(60));
        System.out.println("SISTEMA DE VENTA DE TIQUETES - BOLETAMASTER PRO");
        System.out.println("=" .repeat(60));
        
        SistemaVentaTiquetes sistema = new SistemaVentaTiquetes();
        
        try {
            // ===== CARGA DE DATOS =====
            System.out.println("\n CARGANDO DATOS DEL SISTEMA...");
            sistema.getPersistencia().cargarDatos();
            
            System.out.println("\n=== VERIFICACIÓN DE DATOS CARGADOS ===");
            System.out.println("Usuarios cargados: " + sistema.getUsuarios().size());
            System.out.println("Eventos cargados: " + sistema.getEventos().size());
            System.out.println("Venues cargados: " + sistema.getVenues().size());
            
            for (Usuario usuario : sistema.getUsuarios()) {
                System.out.println(" - " + usuario.getNombre() + " ($" + usuario.getSaldoVirtual() + ")");
            }
            System.out.println("=============================\n");
            
            // ===== VERIFICACIÓN E INICIALIZACIÓN =====
            boolean necesitaInicializacion = 
                sistema.getUsuarios().isEmpty() || 
                sistema.getVenues().isEmpty() || 
                sistema.getEventos().isEmpty();
            
            if (necesitaInicializacion) {
                System.out.println("DATOS INCOMPLETOS O INEXISTENTES");
                System.out.println("EJECUTANDO INICIALIZACIÓN FORZADA...");
                
                inicializarDatosForzados(sistema);
                
                System.out.println("\n GUARDANDO DATOS INICIALIZADOS...");
                sistema.getPersistencia().guardarDatos();
                System.out.println("DATOS GUARDADOS EXITOSAMENTE");
            } else {
                System.out.println("DATOS CARGADOS CORRECTAMENTE");
            }
            
            verificarEstadoSistema(sistema);
            
        } catch (Exception e) {
            System.err.println("\n ERROR CRÍTICO AL INICIAR EL SISTEMA: " + e.getMessage());
            System.err.println("Intentando recuperación...");
            
            try {
                inicializarDatosForzados(sistema);
                sistema.getPersistencia().guardarDatos();
                System.out.println("SISTEMA RECUPERADO MEDIANTE INICIALIZACIÓN FORZADA");
            } catch (Exception ex) {
                System.err.println("ERROR IRRECUPERABLE: " + ex.getMessage());
                System.out.println("No se puede continuar. Contacte al administrador.");
                System.exit(1);
            }
        }
        
        // ===== MENÚ PRINCIPAL =====
        ejecutarMenuPrincipal(sistema);
    }
    
    private static void ejecutarMenuPrincipal(SistemaVentaTiquetes sistema) {
        while (true) {
            try {
                InputValidator.mostrarHeader("SISTEMA DE VENTA DE TIQUETES - BOLETAMASTER PRO");
                
                System.out.println("ESTADO ACTUAL:");
                System.out.println("Usuarios registrados: " + sistema.getUsuarios().size());
                System.out.println("Eventos activos: " + sistema.getEventos().stream()
                    .filter(e -> "activo".equals(e.getEstado())).count());
                System.out.println("Ofertas en Marketplace: " + sistema.getMarketplace().getOfertasActivas().size());
                
                System.out.println("\n SELECCIONE SU TIPO DE USUARIO:");
                System.out.println("═".repeat(60));
                System.out.println("1. CLIENTE - Comprar tiquetes y usar Marketplace");
                System.out.println("2. ORGANIZADOR - Gestionar eventos y venues");
                System.out.println("3. ADMINISTRADOR - Moderar sistema y ver reportes");
                System.out.println("4. ESTADO DEL SISTEMA - Ver información técnica");
                System.out.println("0. SALIR DEL SISTEMA");
                System.out.println("═".repeat(60));
                
                int opcion = InputValidator.leerEnteroEnRango("Seleccione una opción: ", 0, 4);
                
                switch (opcion) {
                    case 1 -> {
                        System.out.println("INICIANDO MÓDULO CLIENTE...");
                        MainCliente mainCliente = new MainCliente(sistema);
                        mainCliente.ejecutar();
                    }
                    case 2 -> {
                        System.out.println("INICIANDO MÓDULO ORGANIZADOR...");
                        MainOrganizador mainOrganizador = new MainOrganizador(sistema);
                        mainOrganizador.ejecutar();
                    }
                    case 3 -> {
                        System.out.println("INICIANDO MÓDULO ADMINISTRADOR...");
                        MainAdministrador mainAdmin = new MainAdministrador(sistema);
                        mainAdmin.ejecutar();
                    }
                    case 4 -> {
                        InputValidator.mostrarHeader("ESTADO TÉCNICO DEL SISTEMA");
                        verificarEstadoSistema(sistema);
                        
                        System.out.println("\n INFORMACIÓN ADICIONAL:");
                        System.out.println("   Ruta de datos: ./datos_sistema/");
                        System.out.println("   Hora del sistema: " + LocalDateTime.now());
                        System.out.println("   Memoria usada: " + 
                            (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024) + " MB");
                        
                        InputValidator.presioneEnter();
                    }
                    case 0 -> {
                        System.out.println("\n GUARDANDO DATOS...");
                        sistema.getPersistencia().guardarDatos();
                        System.out.println("DATOS GUARDADOS EXITOSAMENTE");
                        System.out.println("¡GRACIAS POR USAR BOLETAMASTER PRO!");
                        System.out.println("¡HASTA PRONTO!");
                        System.exit(0);
                    }
                }
                
            } catch (Exception e) {
                System.err.println("ERROR EN MENÚ PRINCIPAL: " + e.getMessage());
                System.out.println("Reiniciando menú principal...");
                InputValidator.presioneEnter();
            }
        }
    }
}