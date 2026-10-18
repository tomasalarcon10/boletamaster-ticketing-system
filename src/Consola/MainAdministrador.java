package Consola;

import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import finanzas.*;
import marketplace.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz de consola para Administradores con herramientas completas de moderación
 */
public class MainAdministrador {
    private SistemaVentaTiquetes sistema;
    private Administrador adminActual;

    public MainAdministrador(SistemaVentaTiquetes sistema) {
        this.sistema = sistema;
    }

    public void ejecutar() {
        if (adminActual == null) {
            if (!autenticar()) {
                return;
            }
        }
        
        mostrarMenuPrincipal();
    }

    private boolean autenticar() {
        while (true) {
            InputValidator.mostrarHeader("SISTEMA DE VENTA DE TIQUETES - ADMINISTRADOR");
            System.out.println("INICIAR SESIÓN - ADMINISTRADOR");
            System.out.println("=".repeat(40));
            
            int opcion = InputValidator.leerEnteroEnRango(
                "1. Iniciar sesión\n2. Volver al menú principal\nSeleccione: ", 1, 2);
            
            switch (opcion) {
                case 1 -> { 
                    if (iniciarSesion()) {
                        return true;
                    }
                    System.out.println("No se pudo iniciar sesión. Intenta de nuevo.");
                    InputValidator.presioneEnter();
                }
                case 2 -> { 
                    System.out.println("Volviendo al menú principal del sistema...");
                    return false;
                }
            }
        }
    }

    private boolean iniciarSesion() {
        System.out.println("\n INICIAR SESIÓN");
        String login = InputValidator.leerTexto("Usuario: ");
        String password = InputValidator.leerTexto("Contraseña: ");
        
        Usuario usuario = sistema.autenticar(login, password);
        if (usuario instanceof Administrador) {
            adminActual = (Administrador) usuario;
            System.out.println("¡Bienvenido, " + adminActual.getNombre() + "!");
            InputValidator.presioneEnter();
            return true;
        } else {
            System.out.println("Credenciales inválidas o no es un administrador.");
            InputValidator.presioneEnter();
            return false;
        }
    }

    private void mostrarMenuPrincipal() {
        while (adminActual != null) {
            InputValidator.mostrarHeader("MENÚ PRINCIPAL - " + adminActual.getNombre().toUpperCase());
            
            System.out.println("\n" + "═".repeat(60));
            System.out.println("1. Ver mi perfil y configuración");
            System.out.println("2. Configurar cargos del sistema");
            System.out.println("3. Gestionar venues (aprobaciones)");
            System.out.println("4. Gestionar eventos");
            System.out.println("5. Gestionar reembolsos");
            System.out.println("6. Reportes financieros");
            System.out.println("7. MODERAR MARKETPLACE");
            System.out.println("8. Cerrar sesión");
            System.out.println("0. Salir del sistema");
            System.out.println("═".repeat(60));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione una opción: ", 0, 8);
            
            switch (opcion) {
                case 1 -> verPerfil();
                case 2 -> menuConfiguracionCargos();
                case 3 -> menuGestionVenues();
                case 4 -> menuGestionEventos();
                case 5 -> menuGestionReembolsos();
                case 6 -> menuReportesFinancieros();
                case 7 -> menuModeracionMarketplace();
                case 8 -> { 
                    System.out.println("...Cerrando sesión de " + adminActual.getNombre() + "...");
                    adminActual = null;
                    return;
                }
                case 0 -> salirDelSistema();
            }
        }
    }

    // ===== MÉTODOS DE PERFIL Y CONFIGURACIÓN =====
    
    private void verPerfil() {
        InputValidator.mostrarHeader("MI PERFIL - ADMINISTRADOR");
        System.out.println(adminActual.mostrarPerfil());
        InputValidator.presioneEnter();
    }
    
    private void menuConfiguracionCargos() {
        while (true) {
            InputValidator.mostrarHeader("CONFIGURACIÓN DE CARGOS DEL SISTEMA");
            
            ConfiguracionCargos config = adminActual.getConfiguracionCargos();
            if (config == null) {
                System.out.println("Error: No hay configuración de cargos disponible.");
                InputValidator.presioneEnter();
                return;
            }
            
            System.out.println("CONFIGURACIÓN ACTUAL:");
            System.out.println("Cargo Musical: " + config.getCargoServicioMusical() + "%");
            System.out.println("Cargo Cultural: " + config.getCargoServicioCultural() + "%");
            System.out.println("Cargo Deportivo: " + config.getCargoServicioDeportivo() + "%");
            System.out.println("Cargo Religioso: " + config.getCargoServicioReligioso() + "%");
            System.out.println("Cuota de Emisión: $" + config.getCuotaEmision());
            
            System.out.println("\n" + "═".repeat(50));
            System.out.println("1. Modificar cargo musical");
            System.out.println("2. Modificar cargo cultural");
            System.out.println("3. Modificar cargo deportivo");
            System.out.println("4. Modificar cargo religioso");
            System.out.println("5. Modificar cuota de emisión");
            System.out.println("0. Volver al menú principal");
            System.out.println("═".repeat(50));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione: ", 0, 5);
            
            switch (opcion) {
                case 1 -> modificarCargoMusical();
                case 2 -> modificarCargoCultural();
                case 3 -> modificarCargoDeportivo();
                case 4 -> modificarCargoReligioso();
                case 5 -> modificarCuotaEmision();
                case 0 -> { return; }
            }
        }
    }
    
    private void modificarCargoMusical() {
        double nuevoCargo = InputValidator.leerDouble("Nuevo cargo para eventos musicales (%): ");
        adminActual.actualizarCargoMusical(nuevoCargo);
        System.out.println("Cargo musical actualizado a: " + nuevoCargo + "%");
        sistema.guardarDatosAutomaticamente();
        InputValidator.presioneEnter();
    }
    
    private void modificarCargoCultural() {
        double nuevoCargo = InputValidator.leerDouble("Nuevo cargo para eventos culturales (%): ");
        adminActual.actualizarCargoCultural(nuevoCargo);
        System.out.println("Cargo cultural actualizado a: " + nuevoCargo + "%");
        sistema.guardarDatosAutomaticamente();
        InputValidator.presioneEnter();
    }
    
    private void modificarCargoDeportivo() {
        double nuevoCargo = InputValidator.leerDouble("Nuevo cargo para eventos deportivos (%): ");
        adminActual.actualizarCargoDeportivo(nuevoCargo);
        System.out.println("Cargo deportivo actualizado a: " + nuevoCargo + "%");
        sistema.guardarDatosAutomaticamente();
        InputValidator.presioneEnter();
    }
    
    private void modificarCargoReligioso() {
        double nuevoCargo = InputValidator.leerDouble("Nuevo cargo para eventos religiosos (%): ");
        adminActual.actualizarCargoReligioso(nuevoCargo);
        System.out.println(" Cargo religioso actualizado a: " + nuevoCargo + "%");
        sistema.guardarDatosAutomaticamente();
        InputValidator.presioneEnter();
    }
    
    private void modificarCuotaEmision() {
        double nuevaCuota = InputValidator.leerDoublePositivo("Nueva cuota de emisión: $");
        adminActual.actualizarCuotaEmision(nuevaCuota);
        System.out.println(" Cuota de emisión actualizada a: $" + nuevaCuota);
        sistema.guardarDatosAutomaticamente();
        InputValidator.presioneEnter();
    }

    // ===== GESTIÓN DE VENUES =====
    
    private void menuGestionVenues() {
        while (true) {
            InputValidator.mostrarHeader("GESTIÓN DE VENUES - APROBACIONES");
            
            List<Venue> venuesPendientes = sistema.getVenuesPendientes();
            List<Venue> todosVenues = sistema.getVenues();
            
            System.out.println(" ESTADÍSTICAS:");
            System.out.println(" Venues pendientes: " + venuesPendientes.size());
            System.out.println(" Venues aprobados: " + (todosVenues.size() - venuesPendientes.size()));
            System.out.println(" Total venues: " + todosVenues.size());
            
            System.out.println("\n" + "═".repeat(50));
            System.out.println("1.  Ver venues pendientes de aprobación");
            System.out.println("2.  Aprobar venue");
            System.out.println("3.  Rechazar venue");
            System.out.println("4.  Ver todos los venues");
            System.out.println("0.  Volver al menú principal");
            System.out.println("═".repeat(50));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione: ", 0, 4);
            
            switch (opcion) {
                case 1 -> verVenuesPendientes();
                case 2 -> aprobarVenue();
                case 3 -> rechazarVenue();
                case 4 -> verTodosVenues();
                case 0 -> { return; }
            }
        }
    }
    
    private void verVenuesPendientes() {
        InputValidator.mostrarHeader("VENUES PENDIENTES DE APROBACIÓN");
        List<Venue> venuesPendientes = sistema.getVenuesPendientes();
        
        if (venuesPendientes.isEmpty()) {
            System.out.println(" ¡No hay venues pendientes de aprobación!");
        } else {
            for (int i = 0; i < venuesPendientes.size(); i++) {
                Venue venue = venuesPendientes.get(i);
                System.out.println((i + 1) + ". " + venue.getNombre());
                System.out.println("   Ubicación: " + venue.getUbicacion());
                System.out.println("   Capacidad: " + venue.getCapacidadMaxima());
                System.out.println("   Restricciones: " + venue.getRestricciones());
                if (venue.getOrganizadorSugerente() != null) {
                    System.out.println("   Sugerido por: " + venue.getOrganizadorSugerente().getNombre());
                }
                System.out.println();
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void aprobarVenue() {
        InputValidator.mostrarHeader("APROBAR VENUE");
        List<Venue> venuesPendientes = sistema.getVenuesPendientes();
        
        if (venuesPendientes.isEmpty()) {
            System.out.println("No hay venues pendientes para aprobar.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("VENUES PENDIENTES:");
        for (int i = 0; i < venuesPendientes.size(); i++) {
            Venue venue = venuesPendientes.get(i);
            System.out.println((i + 1) + ". " + venue.getNombre() + " - " + venue.getUbicacion());
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione el venue a aprobar: ", 1, venuesPendientes.size());
        Venue venueSeleccionado = venuesPendientes.get(seleccion - 1);
        
        boolean exito = sistema.aprobarVenue(venueSeleccionado.getId());
        if (exito) {
            System.out.println("¡Venue '" + venueSeleccionado.getNombre() + "' aprobado exitosamente!");
            sistema.guardarDatosAutomaticamente();
        } else {
            System.out.println(" Error al aprobar el venue.");
        }
        InputValidator.presioneEnter();
    }
    
    private void rechazarVenue() {
        InputValidator.mostrarHeader("RECHAZAR VENUE");
        List<Venue> venuesPendientes = sistema.getVenuesPendientes();
        
        if (venuesPendientes.isEmpty()) {
            System.out.println("No hay venues pendientes para rechazar.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("VENUES PENDIENTES:");
        for (int i = 0; i < venuesPendientes.size(); i++) {
            Venue venue = venuesPendientes.get(i);
            System.out.println((i + 1) + ". " + venue.getNombre() + " - " + venue.getUbicacion());
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione el venue a rechazar: ", 1, venuesPendientes.size());
        Venue venueSeleccionado = venuesPendientes.get(seleccion - 1);
        String motivo = InputValidator.leerTextoNoVacio("Motivo del rechazo: ");
        
        adminActual.rechazarVenue(venueSeleccionado, motivo);
        sistema.getVenues().remove(venueSeleccionado); // Eliminar del sistema
        System.out.println("Venue '" + venueSeleccionado.getNombre() + "' rechazado.");
        sistema.guardarDatosAutomaticamente();
        InputValidator.presioneEnter();
    }
    
    private void verTodosVenues() {
        InputValidator.mostrarHeader("TODOS LOS VENUES DEL SISTEMA");
        List<Venue> todosVenues = sistema.getVenues();
        
        if (todosVenues.isEmpty()) {
            System.out.println(" No hay venues registrados en el sistema.");
        } else {
            for (Venue venue : todosVenues) {
                System.out.println("* " + venue.getNombre());
                System.out.println("* " + venue.getUbicacion());
                System.out.println("Capacidad: " + venue.getCapacidadMaxima());
                System.out.println("Estado: " + (venue.isAprobado() ? "Aprobado" : "Pendiente"));
                System.out.println("Eventos realizados: " + venue.getEventos().size());
                System.out.println();
            }
        }
        InputValidator.presioneEnter();
    }

    // ===== GESTIÓN DE EVENTOS =====
    
    private void menuGestionEventos() {
        while (true) {
            InputValidator.mostrarHeader("GESTIÓN DE EVENTOS");
            
            List<Evento> eventos = sistema.getEventos();
            long eventosActivos = eventos.stream().filter(e -> "activo".equals(e.getEstado())).count();
            long eventosCancelados = eventos.stream().filter(e -> "cancelado".equals(e.getEstado())).count();
            
            System.out.println("ESTADÍSTICAS:");
            System.out.println("Eventos activos: " + eventosActivos);
            System.out.println("Eventos cancelados: " + eventosCancelados);
            System.out.println("Total eventos: " + eventos.size());
            
            System.out.println("\n" + "═".repeat(50));
            System.out.println("1. Ver todos los eventos");
            System.out.println("2. Cancelar evento");
            System.out.println("3. Ver detalles de evento");
            System.out.println("0. Volver al menú principal");
            System.out.println("═".repeat(50));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione: ", 0, 3);
            
            switch (opcion) {
                case 1 -> verTodosEventos();
                case 2 -> cancelarEvento();
                case 3 -> verDetallesEvento();
                case 0 -> { return; }
            }
        }
    }
    
    private void verTodosEventos() {
        InputValidator.mostrarHeader("TODOS LOS EVENTOS DEL SISTEMA");
        List<Evento> eventos = sistema.getEventos();
        
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos en el sistema.");
        } else {
            for (Evento evento : eventos) {
                System.out.println("* " + evento.getNombre());
                System.out.println("ID: " + evento.getId());
                System.out.println("Fecha: " + evento.getFechaHora());
                System.out.println("Tipo: " + evento.getTipo());
                System.out.println("Estado: " + evento.getEstado());
                System.out.println("Organizador: " + evento.getOrganizador().getNombre());
                System.out.println("Venue: " + evento.getVenue().getNombre());
                System.out.println("Tiquetes vendidos: " + evento.getTiquetesVendidosTotal() + "/" + evento.getCapacidadTotal());
                System.out.println();
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void cancelarEvento() {
        InputValidator.mostrarHeader("CANCELAR EVENTO");
        List<Evento> eventosActivos = sistema.getEventos().stream()
                .filter(e -> "activo".equals(e.getEstado()))
                .toList();
        
        if (eventosActivos.isEmpty()) {
            System.out.println("No hay eventos activos para cancelar.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("EVENTOS ACTIVOS:");
        for (int i = 0; i < eventosActivos.size(); i++) {
            Evento evento = eventosActivos.get(i);
            System.out.println((i + 1) + ". " + evento.getNombre() + 
                             " - " + evento.getFechaHora() +
                             " - Tiquetes vendidos: " + evento.getTiquetesVendidosTotal());
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione el evento a cancelar: ", 1, eventosActivos.size());
        Evento eventoSeleccionado = eventosActivos.get(seleccion - 1);
        String motivo = InputValidator.leerTextoNoVacio("Motivo de la cancelación: ");
        
        boolean exito = adminActual.cancelarEvento(eventoSeleccionado, motivo);
        if (exito) {
            System.out.println("Evento '" + eventoSeleccionado.getNombre() + "' cancelado exitosamente.");
            System.out.println("Iniciando proceso de reembolsos...");
            sistema.guardarDatosAutomaticamente();
        } else {
            System.out.println("Error al cancelar el evento.");
        }
        InputValidator.presioneEnter();
    }
    
    private void verDetallesEvento() {
        InputValidator.mostrarHeader("DETALLES DE EVENTO");
        List<Evento> eventos = sistema.getEventos();
        
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos en el sistema.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("EVENTOS DISPONIBLES:");
        for (int i = 0; i < eventos.size(); i++) {
            Evento evento = eventos.get(i);
            System.out.println((i + 1) + ". " + evento.getNombre() + " - " + evento.getFechaHora());
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione el evento: ", 1, eventos.size());
        Evento eventoSeleccionado = eventos.get(seleccion - 1);
        
        System.out.println(sistema.generarResumenEvento(eventoSeleccionado));
        InputValidator.presioneEnter();
    }

    // ===== GESTIÓN DE REEMBOLSOS =====
    
    private void menuGestionReembolsos() {
        while (true) {
            InputValidator.mostrarHeader("GESTIÓN DE REEMBOLSOS");
            
            List<Rembolso> reembolsos = sistema.getReembolsos();
            double totalReembolsado = reembolsos.stream().mapToDouble(Rembolso::getMontoReembolsado).sum();
            
            System.out.println("ESTADÍSTICAS:");
            System.out.println("Total reembolsos procesados: " + reembolsos.size());
            System.out.println("Total dinero reembolsado: $" + String.format("%.2f", totalReembolsado));
            
            System.out.println("\n" + "═".repeat(50));
            System.out.println("1. Ver historial de reembolsos");
            System.out.println("2. Autorizar reembolso individual");
            System.out.println("3. Procesar reembolsos por evento");
            System.out.println("0. Volver al menú principal");
            System.out.println("═".repeat(50));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione: ", 0, 3);
            
            switch (opcion) {
                case 1 -> verHistorialReembolsos();
                case 2 -> autorizarReembolsoIndividual();
                case 3 -> procesarReembolsosEvento();
                case 0 -> { return; }
            }
        }
    }
    
    private void verHistorialReembolsos() {
        InputValidator.mostrarHeader("HISTORIAL DE REEMBOLSOS");
        List<Rembolso> reembolsos = sistema.getReembolsos();
        
        if (reembolsos.isEmpty()) {
            System.out.println("No hay reembolsos en el historial.");
        } else {
            for (Rembolso reembolso : reembolsos) {
                System.out.println("ID " + reembolso.getId());
                System.out.println("Cliente: " + reembolso.getCliente().getNombre());
                System.out.println("Evento: " + reembolso.getEvento().getNombre());
                System.out.println("Monto: $" + reembolso.getMontoReembolsado());
                System.out.println("Fecha: " + reembolso.getFecha());
                System.out.println();
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void autorizarReembolsoIndividual() {
        InputValidator.mostrarHeader("AUTORIZAR REEMBOLSO INDIVIDUAL");
        System.out.println("Esta función requiere integración con el sistema de tiquetes.");
        System.out.println("Para implementación completa, se necesitaría listar tiquetes válidos para reembolso.");
        InputValidator.presioneEnter();
    }
    
    private void procesarReembolsosEvento() {
        InputValidator.mostrarHeader("PROCESAR REEMBOLSOS POR EVENTO");
        List<Evento> eventosCancelados = sistema.getEventos().stream()
                .filter(e -> "cancelado".equals(e.getEstado()))
                .toList();
        
        if (eventosCancelados.isEmpty()) {
            System.out.println("No hay eventos cancelados que requieran reembolsos.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("EVENTOS CANCELADOS PENDIENTES DE REEMBOLSO:");
        for (int i = 0; i < eventosCancelados.size(); i++) {
            Evento evento = eventosCancelados.get(i);
            System.out.println((i + 1) + ". " + evento.getNombre() + 
                             " - Tiquetes vendidos: " + evento.getTiquetesVendidosTotal());
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione el evento: ", 1, eventosCancelados.size());
        Evento eventoSeleccionado = eventosCancelados.get(seleccion - 1);
        String tipoReembolso = InputValidator.leerTextoNoVacio("Tipo de reembolso (completo/parcial): ");
        
        adminActual.procesarReembolsoEvento(eventoSeleccionado, tipoReembolso);
        System.out.println("Proceso de reembolso iniciado para: " + eventoSeleccionado.getNombre());
        InputValidator.presioneEnter();
    }

    // ===== REPORTES FINANCIEROS =====
    
    private void menuReportesFinancieros() {
        while (true) {
            InputValidator.mostrarHeader("REPORTES FINANCIEROS");
            
            System.out.println("REPORTES DISPONIBLES:");
            System.out.println("\n" + "═".repeat(50));
            System.out.println("1. Reporte financiero completo");
            System.out.println("2. Ganancias totales");
            System.out.println("3. Ganancias por fecha");
            System.out.println("4. Ganancias por evento");
            System.out.println("5. Ganancias por organizador");
            System.out.println("6. Ver configuración de cargos");
            System.out.println("0. Volver al menú principal");
            System.out.println("═".repeat(50));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione: ", 0, 6);
            
            switch (opcion) {
                case 1 -> generarReporteCompleto();
                case 2 -> verGananciaTotal();
                case 3 -> verGananciaPorFecha();
                case 4 -> verGananciaPorEvento();
                case 5 -> verGananciaPorOrganizador();
                case 6 -> verConfiguracionCargos();
                case 0 -> { return; }
            }
        }
    }
    
    private void generarReporteCompleto() {
        InputValidator.mostrarHeader("REPORTE FINANCIERO COMPLETO");
        String reporte = adminActual.generarReporteCompleto();
        System.out.println(reporte);
        InputValidator.presioneEnter();
    }
    
    private void verGananciaTotal() {
        InputValidator.mostrarHeader("GANANCIAS TOTALES DEL SISTEMA");
        double gananciaTotal = adminActual.verGananciaTotal();
        System.out.println("GANANCIAS TOTALES: $" + String.format("%.2f", gananciaTotal));
        InputValidator.presioneEnter();
    }
    
    private void verGananciaPorFecha() {
        InputValidator.mostrarHeader("GANANCIAS POR FECHA");
        System.out.println("Función de ganancias por fecha - Para implementación completa");
        InputValidator.presioneEnter();
    }
    
    private void verGananciaPorEvento() {
        InputValidator.mostrarHeader("GANANCIAS POR EVENTO");
        List<Evento> eventos = sistema.getEventos();
        
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos en el sistema.");
        } else {
            for (Evento evento : eventos) {
                double ganancia = adminActual.verGananciaPorEvento(evento);
                System.out.println("* " + evento.getNombre() + ": $" + String.format("%.2f", ganancia));
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void verGananciaPorOrganizador() {
        InputValidator.mostrarHeader("GANANCIAS POR ORGANIZADOR");
        List<Usuario> usuarios = sistema.getUsuarios();
        List<Organizador> organizadores = usuarios.stream()
                .filter(u -> u instanceof Organizador)
                .map(u -> (Organizador) u)
                .toList();
        
        if (organizadores.isEmpty()) {
            System.out.println("No hay organizadores en el sistema.");
        } else {
            for (Organizador organizador : organizadores) {
                double ganancia = adminActual.verGananciaPorOrganizador(organizador);
                System.out.println("* " + organizador.getNombre() + ": $" + String.format("%.2f", ganancia));
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void verConfiguracionCargos() {
        InputValidator.mostrarHeader("CONFIGURACIÓN DE CARGOS ACTUAL");
        ConfiguracionCargos config = adminActual.getConfiguracionCargos();
        
        if (config != null) {
            System.out.println(config.toString());
        } else {
            System.out.println("No hay configuración de cargos disponible.");
        }
        InputValidator.presioneEnter();
    }

    // ===== MODERACIÓN DEL MARKETPLACE =====
    
    private void menuModeracionMarketplace() {
        while (true) {
            InputValidator.mostrarHeader("MODERACIÓN DEL MARKETPLACE");
            
            List<OfertaReventa> ofertasActivas = sistema.getMarketplace().getOfertasActivas();
            List<LogEntry> logs = sistema.getMarketplace().getLogs();
            
            System.out.println("ESTADÍSTICAS DEL MARKETPLACE:");
            System.out.println("Ofertas activas: " + ofertasActivas.size());
            System.out.println("Logs registrados: " + logs.size());
            
            System.out.println("\n" + "═".repeat(50));
            System.out.println("1. Ver todas las ofertas activas");
            System.out.println("2. Inspeccionar oferta específica");
            System.out.println("3. Eliminar oferta (moderación)");
            System.out.println("4. Ver logs del Marketplace");
            System.out.println("5. Buscar ofertas por evento");
            System.out.println("0. Volver al menú principal");
            System.out.println("═".repeat(50));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione: ", 0, 5);
            
            switch (opcion) {
                case 1 -> verOfertasMarketplace();
                case 2 -> inspeccionarOferta();
                case 3 -> eliminarOfertaModeracion();
                case 4 -> verLogsMarketplace();
                case 5 -> buscarOfertasPorEvento();
                case 0 -> { return; }
            }
        }
    }
    
    private void verOfertasMarketplace() {
        InputValidator.mostrarHeader("TODAS LAS OFERTAS ACTIVAS EN MARKETPLACE");
        List<OfertaReventa> ofertas = sistema.getOfertasActivasMarketplace();
        
        if (ofertas.isEmpty()) {
            System.out.println("No hay ofertas activas en el Marketplace.");
        } else {
            System.out.println("Total ofertas: " + ofertas.size());
            System.out.println();
            
            for (int i = 0; i < ofertas.size(); i++) {
                OfertaReventa oferta = ofertas.get(i);
                System.out.println((i + 1) + ". ID " + oferta.getId());
                System.out.println(" Tiquete: " + oferta.getTiquete().getId());
                System.out.println("  Evento: " + oferta.getTiquete().getEvento().getNombre());
                System.out.println("  Precio: $" + oferta.getPrecioVenta());
                System.out.println("  Vendedor: " + oferta.getVendedor().getNombre());
                System.out.println("  Publicada: " + oferta.getFechaPublicacion());
                System.out.println("  Activa: " + (oferta.isActiva() ? "Sí" : "No"));
                System.out.println();
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void inspeccionarOferta() {
        InputValidator.mostrarHeader("INSPECCIONAR OFERTA ESPECÍFICA");
        List<OfertaReventa> ofertas = sistema.getOfertasActivasMarketplace();
        
        if (ofertas.isEmpty()) {
            System.out.println("No hay ofertas para inspeccionar.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("OFERTAS DISPONIBLES PARA INSPECCIÓN:");
        for (int i = 0; i < ofertas.size(); i++) {
            OfertaReventa oferta = ofertas.get(i);
            System.out.println((i + 1) + ". " + oferta.getId() + " - " + oferta.getTiquete().getEvento().getNombre());
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione la oferta a inspeccionar: ", 1, ofertas.size());
        OfertaReventa ofertaSeleccionada = ofertas.get(seleccion - 1);
        
        System.out.println("INFORMACIÓN DETALLADA DE LA OFERTA:");
        System.out.println("ID: " + ofertaSeleccionada.getId());
        System.out.println("Tiquete ID: " + ofertaSeleccionada.getTiquete().getId());
        System.out.println("Evento: " + ofertaSeleccionada.getTiquete().getEvento().getNombre());
        System.out.println("Fecha evento: " + ofertaSeleccionada.getTiquete().getEvento().getFechaHora());
        System.out.println("Precio de venta: $" + ofertaSeleccionada.getPrecioVenta());
        System.out.println("Precio original: $" + ofertaSeleccionada.getTiquete().calcularPrecioFinal());
        System.out.println("Vendedor: " + ofertaSeleccionada.getVendedor().getNombre());
        System.out.println("Login vendedor: " + ofertaSeleccionada.getVendedor().getLogin());
        System.out.println("Fecha publicación: " + ofertaSeleccionada.getFechaPublicacion());
        System.out.println("Activa: " + (ofertaSeleccionada.isActiva() ? "Sí" : "No"));
        
        InputValidator.presioneEnter();
    }
    
    private void eliminarOfertaModeracion() {
        InputValidator.mostrarHeader("ELIMINAR OFERTA - HERRAMIENTA DE MODERACIÓN");
        List<OfertaReventa> ofertas = sistema.getOfertasActivasMarketplace();
        
        if (ofertas.isEmpty()) {
            System.out.println("No hay ofertas para eliminar.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("OFERTAS ACTIVAS (ELIMINACIÓN ADMIN):");
        for (int i = 0; i < ofertas.size(); i++) {
            OfertaReventa oferta = ofertas.get(i);
            System.out.println((i + 1) + "  * " + oferta.getId());
            System.out.println("   * " + oferta.getTiquete().getEvento().getNombre());
            System.out.println("   * " + oferta.getPrecioVenta());
            System.out.println("   * " + oferta.getVendedor().getNombre());
            System.out.println();
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione la oferta a eliminar: ", 1, ofertas.size());
        OfertaReventa ofertaSeleccionada = ofertas.get(seleccion - 1);
        String motivo = InputValidator.leerTextoNoVacio("Motivo de la eliminación (para el log): ");
        
        boolean exito = sistema.eliminarOfertaAdministrador(ofertaSeleccionada.getId());
        if (exito) {
            sistema.getMarketplace().getLogs().add(new LogEntry(
                "ADMIN-" + System.currentTimeMillis(),
                adminActual,
                "OFERTA_ELIMINADA_MODERACION",
                "Oferta " + ofertaSeleccionada.getId() + " eliminada por admin. Motivo: " + motivo
            ));
            System.out.println("Oferta eliminada por moderación.");
            sistema.guardarDatosAutomaticamente();
        } else {
            System.out.println("Error al eliminar la oferta.");
        }
        InputValidator.presioneEnter();
    }
    
    private void verLogsMarketplace() {
        InputValidator.mostrarHeader("LOGS DEL MARKETPLACE");
        String logs = sistema.getLogMarketplace();
        System.out.println(logs);
        InputValidator.presioneEnter();
    }
    
    private void buscarOfertasPorEvento() {
        InputValidator.mostrarHeader("BUSCAR OFERTAS POR EVENTO");
        List<Evento> eventos = sistema.getEventos();
        
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos en el sistema.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("EVENTOS DISPONIBLES:");
        for (int i = 0; i < eventos.size(); i++) {
            Evento evento = eventos.get(i);
            System.out.println((i + 1) + ". " + evento.getNombre() + " (" + evento.getId() + ")");
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione el evento: ", 1, eventos.size());
        Evento eventoSeleccionado = eventos.get(seleccion - 1);
        
        List<OfertaReventa> ofertasEvento = sistema.getMarketplace().buscarOfertasPorEvento(eventoSeleccionado.getId());
        
        if (ofertasEvento.isEmpty()) {
            System.out.println("No hay ofertas para el evento: " + eventoSeleccionado.getNombre());
        } else {
            System.out.println("OFERTAS PARA " + eventoSeleccionado.getNombre() + ":");
            for (OfertaReventa oferta : ofertasEvento) {
                System.out.println("* " + oferta.getId());
                System.out.println("Precio: $" + oferta.getPrecioVenta());
                System.out.println("Vendedor: " + oferta.getVendedor().getNombre());
                System.out.println();
            }
        }
        InputValidator.presioneEnter();
    }

    // ===== MÉTODOS DEL SISTEMA =====
    
    private void salirDelSistema() {
        System.out.println("Guardando datos...");
        sistema.getPersistencia().guardarDatos();
        System.out.println("¡Hasta pronto!");
        System.exit(0);
    }
}