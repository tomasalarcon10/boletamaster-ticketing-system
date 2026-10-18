package Consola;

import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import tiquetes.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de consola para Organizadores con gestión completa de eventos
 */
public class MainOrganizador {
    private SistemaVentaTiquetes sistema;
    private Organizador organizadorActual;

    public MainOrganizador(SistemaVentaTiquetes sistema) {
        this.sistema = sistema;
    }

    public void ejecutar() {
        if (organizadorActual == null) {
            if (!autenticar()) {
                return;
            }
        }
        
        mostrarMenuPrincipal();
    }

    private boolean autenticar() {
        while (true) {
            InputValidator.mostrarHeader("SISTEMA DE VENTA DE TIQUETES - ORGANIZADOR");
            System.out.println("INICIAR SESIÓN - ORGANIZADOR");
            System.out.println("=".repeat(40));
            
            int opcion = InputValidator.leerEnteroEnRango(
                "1. Iniciar sesión\n2. Registrarse\n3. Volver al menú principal\nSeleccione: ", 1, 3);
            
            switch (opcion) {
                case 1 -> { 
                    if (iniciarSesion()) {
                        return true;
                    }
                    System.out.println("No se pudo iniciar sesión. Intenta de nuevo.");
                    InputValidator.presioneEnter();
                }
                case 2 -> { 
                    if (registrarOrganizador()) {
                        return true;
                    }
                    System.out.println("No se pudo registrar. Intenta de nuevo.");
                    InputValidator.presioneEnter();
                }
                case 3 -> { 
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
        if (usuario instanceof Organizador) {
            organizadorActual = (Organizador) usuario;
            System.out.println("¡Bienvenido, " + organizadorActual.getNombre() + "!");
            InputValidator.presioneEnter();
            return true;
        } else {
            System.out.println("Credenciales inválidas o no es un organizador.");
            InputValidator.presioneEnter();
            return false;
        }
    }

    private boolean registrarOrganizador() {
        System.out.println("\n REGISTRAR NUEVO ORGANIZADOR");
        String login = InputValidator.leerTextoNoVacio("Usuario: ");
        String password = InputValidator.leerPassword("Contraseña: ");
        String nombre = InputValidator.leerTextoNoVacio("Nombre completo: ");
        
        Usuario usuario = sistema.registrarUsuario(login, password, nombre, "organizador");
        if (usuario instanceof Organizador) {
            organizadorActual = (Organizador) usuario;
            System.out.println("¡Registro exitoso! Bienvenido, " + nombre + ".");
            InputValidator.presioneEnter();
            return true;
        } else {
            System.out.println("Error en el registro. El usuario ya existe.");
            InputValidator.presioneEnter();
            return false;
        }
    }

    private void mostrarMenuPrincipal() {
        while (organizadorActual != null) {
            InputValidator.mostrarHeader("MENÚ PRINCIPAL - " + organizadorActual.getNombre().toUpperCase());
            
            System.out.println("ESTADÍSTICAS:");
            System.out.println("Eventos activos: " + organizadorActual.getEventosActivos().size());
            System.out.println("Ganancias totales: $" + organizadorActual.calcularGananciasTotales());
            System.out.println("% Ventas global: " + String.format("%.1f", organizadorActual.calcularPorcentajeVentasGlobal()) + "%");
            
            System.out.println("\n" + "═".repeat(60));
            System.out.println("1. Ver mi perfil");
            System.out.println("2. Gestionar mis eventos");
            System.out.println("3. Sugerir nuevo venue");
            System.out.println("4. Reportes y estadísticas");
            System.out.println("5. Cerrar sesión");
            System.out.println("0. Salir del sistema");
            System.out.println("═".repeat(60));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione una opción: ", 0, 5);
            
            switch (opcion) {
                case 1 -> verPerfil();
                case 2 -> menuGestionEventos();
                case 3 -> sugerirVenue();
                case 4 -> menuReportes();
                case 5 -> { 
                    System.out.println("Cerrando sesión de " + organizadorActual.getNombre() + "...");
                    organizadorActual = null;
                    return;
                }
                case 0 -> salirDelSistema();
            }
        }
    }

    // ===== MÉTODOS DE PERFIL =====
    
    private void verPerfil() {
        InputValidator.mostrarHeader("MI PERFIL - ORGANIZADOR");
        System.out.println(organizadorActual.mostrarPerfil());
        InputValidator.presioneEnter();
    }

    // ===== GESTIÓN DE EVENTOS =====
    
    private void menuGestionEventos() {
        while (true) {
            InputValidator.mostrarHeader("GESTIÓN DE MIS EVENTOS");
            
            List<Evento> misEventos = organizadorActual.getEventosActivos();
            System.out.println("Tienes " + misEventos.size() + " eventos activos");
            
            System.out.println("\n" + "═".repeat(50));
            System.out.println("1. Crear nuevo evento");
            System.out.println("2. Ver mis eventos");
            System.out.println("3. Agregar localidad a evento");
            System.out.println("4. Crear oferta especial");
            System.out.println("5. Solicitar cancelación de evento");
            System.out.println("0. Volver al menú principal");
            System.out.println("═".repeat(50));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione: ", 0, 5);
            
            switch (opcion) {
                case 1 -> crearEvento();
                case 2 -> verMisEventos();
                case 3 -> agregarLocalidad();
                case 4 -> crearOfertaEspecial();
                case 5 -> solicitarCancelacionEvento();
                case 0 -> { return; }
            }
        }
    }
    
    private void crearEvento() {
        InputValidator.mostrarHeader("CREAR NUEVO EVENTO");
        
        List<Venue> venuesAprobados = sistema.getVenues().stream()
                .filter(Venue::isAprobado)
                .toList();
        
        if (venuesAprobados.isEmpty()) {
            System.out.println("No hay venues aprobados disponibles.");
            System.out.println("Primero debes sugerir un venue y esperar su aprobación.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("VENUES APROBADOS DISPONIBLES:");
        for (int i = 0; i < venuesAprobados.size(); i++) {
            Venue venue = venuesAprobados.get(i);
            System.out.println((i + 1) + ". " + venue.getNombre() + 
                             " - " + venue.getUbicacion() + 
                             " (Capacidad: " + venue.getCapacidadMaxima() + ")");
        }
        
        int seleccionVenue = InputValidator.leerEnteroEnRango("Seleccione el venue: ", 1, venuesAprobados.size());
        Venue venueSeleccionado = venuesAprobados.get(seleccionVenue - 1);
        
        System.out.print("Ingrese la fecha del evento ");
        LocalDateTime fechaEvento = InputValidator.leerFechaFutura("");
        
        if (!venueSeleccionado.estaDisponible(fechaEvento.toLocalDate())) {
            System.out.println("El venue no está disponible en esa fecha.");
            InputValidator.presioneEnter();
            return;
        }
        
        String idEvento = InputValidator.leerTextoNoVacio("ID del evento: ");
        String nombreEvento = InputValidator.leerTextoNoVacio("Nombre del evento: ");
        String tipoEvento = InputValidator.leerTextoNoVacio("Tipo de evento (musical/cultural/deportivo/religioso): ");
        
        Evento nuevoEvento = new Evento(idEvento, nombreEvento, fechaEvento, tipoEvento, organizadorActual, venueSeleccionado);
        
        if (sistema.registrarEvento(nuevoEvento)) {
            System.out.println("¡Evento creado exitosamente!");
            
            sistema.guardarDatosAutomaticamente();
            
            boolean agregarLocalidades = InputValidator.leerSiNo("¿Desea agregar localidades ahora?");
            while (agregarLocalidades) {
                agregarLocalidadAEvento(nuevoEvento);
                agregarLocalidades = InputValidator.leerSiNo("¿Desea agregar otra localidad?");
            }
        } else {
            System.out.println("Error al crear el evento. Verifique que el ID sea único.");
        }
        InputValidator.presioneEnter();
    }
    
    private void agregarLocalidadAEvento(Evento evento) {
        System.out.println("\n AGREGAR LOCALIDAD AL EVENTO: " + evento.getNombre());
        
        String nombreLocalidad = InputValidator.leerTextoNoVacio("Nombre de la localidad: ");
        double precioBase = InputValidator.leerDoublePositivo("Precio base: $");
        boolean numerada = InputValidator.leerSiNo("¿Es localidad numerada?");
        int capacidad = InputValidator.leerEntero("Capacidad: ");
        String tipoTiquete = InputValidator.leerTextoNoVacio("Tipo de tiquete (basico/numerado): ");
        
        organizadorActual.agregarLocalidad(evento, nombreLocalidad, precioBase, numerada, capacidad, tipoTiquete);
        System.out.println("Localidad '" + nombreLocalidad + "' agregada exitosamente.");
        
        sistema.guardarDatosAutomaticamente();
    }
    
    private void verMisEventos() {
        InputValidator.mostrarHeader("MIS EVENTOS ACTIVOS");
        List<Evento> misEventos = organizadorActual.getEventosActivos();
        
        if (misEventos.isEmpty()) {
            System.out.println("No tienes eventos activos.");
        } else {
            for (int i = 0; i < misEventos.size(); i++) {
                Evento evento = misEventos.get(i);
                System.out.println((i + 1) + ". * " + evento.getNombre());
                System.out.println("   ID: " + evento.getId());
                System.out.println("   Fecha: " + evento.getFechaHora());
                System.out.println("   Tipo: " + evento.getTipo());
                System.out.println("   Venue: " + evento.getVenue().getNombre());
                System.out.println("   Localidades: " + evento.getLocalidades().size());
                System.out.println("   Vendidos: " + evento.getTiquetesVendidosTotal() + "/" + evento.getCapacidadTotal());
                System.out.println("   Ganancias: $" + String.format("%.2f", evento.calcularGananciasOrganizador()));
                System.out.println();
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void agregarLocalidad() {
        InputValidator.mostrarHeader("AGREGAR LOCALIDAD A EVENTO EXISTENTE");
        List<Evento> misEventos = organizadorActual.getEventosActivos();
        
        if (misEventos.isEmpty()) {
            System.out.println("No tienes eventos activos.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("TUS EVENTOS ACTIVOS:");
        for (int i = 0; i < misEventos.size(); i++) {
            Evento evento = misEventos.get(i);
            System.out.println((i + 1) + ". " + evento.getNombre() + " - " + evento.getFechaHora());
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione el evento: ", 1, misEventos.size());
        Evento eventoSeleccionado = misEventos.get(seleccion - 1);
        
        agregarLocalidadAEvento(eventoSeleccionado);
        InputValidator.presioneEnter();
    }
    
    private void crearOfertaEspecial() {
        InputValidator.mostrarHeader("CREAR OFERTA ESPECIAL");
        List<Evento> misEventos = organizadorActual.getEventosActivos();
        
        if (misEventos.isEmpty()) {
            System.out.println("No tienes eventos activos.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("TUS EVENTOS ACTIVOS:");
        for (int i = 0; i < misEventos.size(); i++) {
            Evento evento = misEventos.get(i);
            System.out.println((i + 1) + ". " + evento.getNombre());
        }
        
        int seleccionEvento = InputValidator.leerEnteroEnRango("Seleccione el evento: ", 1, misEventos.size());
        Evento eventoSeleccionado = misEventos.get(seleccionEvento - 1);
        
        List<Localidad> localidades = eventoSeleccionado.getLocalidades();
        if (localidades.isEmpty()) {
            System.out.println("El evento no tiene localidades configuradas.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("LOCALIDADES DEL EVENTO:");
        for (int i = 0; i < localidades.size(); i++) {
            Localidad localidad = localidades.get(i);
            System.out.println((i + 1) + ". " + localidad.getNombre() + " - $" + localidad.getPrecioBase());
        }
        
        int seleccionLocalidad = InputValidator.leerEnteroEnRango("Seleccione la localidad: ", 1, localidades.size());
        Localidad localidadSeleccionada = localidades.get(seleccionLocalidad - 1);
        
        double descuento = InputValidator.leerDouble("Porcentaje de descuento (%): ");
        System.out.print("Fecha de inicio de la oferta ");
        LocalDateTime fechaInicio = InputValidator.leerFechaFutura("");
        System.out.print("Fecha de fin de la oferta ");
        LocalDateTime fechaFin = InputValidator.leerFechaFutura("");
        
        boolean exito = organizadorActual.crearOferta(eventoSeleccionado, localidadSeleccionada.getNombre(), descuento, fechaInicio, fechaFin);
        if (exito) {
            System.out.println("¡Oferta especial creada exitosamente!");
            System.out.println("Descuento: " + descuento + "% en " + localidadSeleccionada.getNombre());
            
            sistema.guardarDatosAutomaticamente();
        } else {
            System.out.println("Error al crear la oferta.");
        }
        InputValidator.presioneEnter();
    }
    
    private void solicitarCancelacionEvento() {
        InputValidator.mostrarHeader("SOLICITAR CANCELACIÓN DE EVENTO");
        List<Evento> misEventos = organizadorActual.getEventosActivos();
        
        if (misEventos.isEmpty()) {
            System.out.println("No tienes eventos activos.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("TUS EVENTOS ACTIVOS:");
        for (int i = 0; i < misEventos.size(); i++) {
            Evento evento = misEventos.get(i);
            System.out.println((i + 1) + ". " + evento.getNombre() + 
                             " - Tiquetes vendidos: " + evento.getTiquetesVendidosTotal());
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione el evento a cancelar: ", 1, misEventos.size());
        Evento eventoSeleccionado = misEventos.get(seleccion - 1);
        
        boolean exito = organizadorActual.solicitarCancelacionEvento(eventoSeleccionado);
        if (exito) {
            System.out.println("Solicitud de cancelación enviada al administrador.");
            System.out.println("Esperando autorización...");
        } else {
            System.out.println("Error al enviar la solicitud de cancelación.");
        }
        InputValidator.presioneEnter();
    }

    // ===== SUGERIR VENUE =====
    
    private void sugerirVenue() {
        InputValidator.mostrarHeader("SUGERIR NUEVO VENUE");
        
        String idVenue = InputValidator.leerTextoNoVacio("ID del venue: ");
        String nombreVenue = InputValidator.leerTextoNoVacio("Nombre del venue: ");
        String ubicacion = InputValidator.leerTextoNoVacio("Ubicación: ");
        int capacidad = InputValidator.leerEntero("Capacidad máxima: ");
        String restricciones = InputValidator.leerTexto("Restricciones (opcional): ");
        
        Venue venueSugerido = organizadorActual.sugerirVenue(idVenue, nombreVenue, ubicacion, capacidad, restricciones);
        sistema.registrarVenue(venueSugerido);
        
        System.out.println("¡Venue sugerido exitosamente!");
        System.out.println("Esperando aprobación del administrador...");
        
        sistema.guardarDatosAutomaticamente();
        
        InputValidator.presioneEnter();
    }

    // ===== REPORTES =====
    
    private void menuReportes() {
        while (true) {
            InputValidator.mostrarHeader("REPORTES Y ESTADÍSTICAS");
            
            System.out.println("REPORTES DISPONIBLES:");
            System.out.println("\n" + "═".repeat(50));
            System.out.println("1. Reporte financiero general");
            System.out.println("2. Reporte por evento específico");
            System.out.println("3. Reporte por localidad");
            System.out.println("4. Estadísticas generales");
            System.out.println("0. Volver al menú principal");
            System.out.println("═".repeat(50));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione: ", 0, 4);
            
            switch (opcion) {
                case 1 -> reporteFinancieroGeneral();
                case 2 -> reportePorEvento();
                case 3 -> reportePorLocalidad();
                case 4 -> estadisticasGenerales();
                case 0 -> { return; }
            }
        }
    }
    
    private void reporteFinancieroGeneral() {
        InputValidator.mostrarHeader("REPORTE FINANCIERO GENERAL");
        
        double gananciasTotales = organizadorActual.calcularGananciasTotales();
        double porcentajeVentas = organizadorActual.calcularPorcentajeVentasGlobal();
        int totalEventos = organizadorActual.getEventosActivos().size();
        
        System.out.println("GANANCIAS TOTALES: $" + String.format("%.2f", gananciasTotales));
        System.out.println("PORCENTAJE DE VENTAS GLOBAL: " + String.format("%.2f", porcentajeVentas) + "%");
        System.out.println("TOTAL EVENTOS ACTIVOS: " + totalEventos);
        
        InputValidator.presioneEnter();
    }
    
    private void reportePorEvento() {
        InputValidator.mostrarHeader("REPORTE POR EVENTO");
        List<Evento> misEventos = organizadorActual.getEventosActivos();
        
        if (misEventos.isEmpty()) {
            System.out.println("No tienes eventos activos.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("TUS EVENTOS:");
        for (int i = 0; i < misEventos.size(); i++) {
            Evento evento = misEventos.get(i);
            System.out.println((i + 1) + ". " + evento.getNombre());
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione el evento: ", 1, misEventos.size());
        Evento eventoSeleccionado = misEventos.get(seleccion - 1);
        
        String reporte = organizadorActual.verReportePorEvento(eventoSeleccionado);
        System.out.println(reporte);
        InputValidator.presioneEnter();
    }
    
    private void reportePorLocalidad() {
        InputValidator.mostrarHeader("REPORTE POR LOCALIDAD");
        // Implementación simplificada
        System.out.println("Para ver reportes por localidad, selecciona un evento específico.");
        InputValidator.presioneEnter();
    }
    
    private void estadisticasGenerales() {
        InputValidator.mostrarHeader("ESTADÍSTICAS GENERALES");
        
        List<Evento> misEventos = organizadorActual.getEventosActivos();
        int totalTiquetesVendidos = misEventos.stream()
                .mapToInt(Evento::getTiquetesVendidosTotal)
                .sum();
        int totalCapacidad = misEventos.stream()
                .mapToInt(Evento::getCapacidadTotal)
                .sum();
        
        System.out.println("ESTADÍSTICAS GLOBALES:");
        System.out.println("Eventos activos: " + misEventos.size());
        System.out.println("Tiquetes vendidos: " + totalTiquetesVendidos);
        System.out.println("Capacidad total: " + totalCapacidad);
        System.out.println("Ocupación promedio: " + 
                          (totalCapacidad > 0 ? String.format("%.1f", (totalTiquetesVendidos * 100.0 / totalCapacidad)) : "0") + "%");
        System.out.println("Ganancias totales: $" + String.format("%.2f", organizadorActual.calcularGananciasTotales()));
        
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