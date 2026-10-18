package Consola;

import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import tiquetes.*;
import finanzas.*;
import marketplace.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Interfaz de consola para clientes con funcionalidades completas del Marketplace
 */
public class MainCliente {
    private SistemaVentaTiquetes sistema;
    private Cliente clienteActual;

    public MainCliente(SistemaVentaTiquetes sistema) {
        this.sistema = sistema;
    }

    public void ejecutar() {
        if (clienteActual == null) {
            if (!autenticar()) {
                return;
            }
        }
        
        mostrarMenuPrincipal();
    }

    private boolean autenticar() {
        while (true) {
            InputValidator.mostrarHeader("SISTEMA DE VENTA DE TIQUETES - CLIENTE");
            System.out.println("INICIAR SESIÓN - CLIENTE");
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
                    if (registrarCliente()) {
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
        if (usuario instanceof Cliente) {
            clienteActual = (Cliente) usuario;
            System.out.println("¡Bienvenido, " + clienteActual.getNombre() + "!");
            InputValidator.presioneEnter();
            return true;
        } else {
            System.out.println("Credenciales inválidas o no es un cliente.");
            InputValidator.presioneEnter();
            return false;
        }
    }

    private boolean registrarCliente() {
        System.out.println("\n REGISTRAR NUEVO CLIENTE");
        String login = InputValidator.leerTextoNoVacio("Usuario: ");
        String password = InputValidator.leerPassword("Contraseña: ");
        String nombre = InputValidator.leerTextoNoVacio("Nombre completo: ");
        
        Usuario usuario = sistema.registrarUsuario(login, password, nombre, "cliente");
        if (usuario instanceof Cliente) {
            clienteActual = (Cliente) usuario;
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
        while (clienteActual != null) {
            InputValidator.mostrarHeader("MENÚ PRINCIPAL - " + clienteActual.getNombre());
            System.out.println("Saldo disponible: $" + clienteActual.getSaldoVirtual());
            System.out.println("Tiquetes en posesión: " + clienteActual.getTiquetes().size());
            
            System.out.println("\n" + "═".repeat(50));
            System.out.println("1. Ver mi perfil");
            System.out.println("2. Recargar saldo");
            System.out.println("3. Ver eventos disponibles");
            System.out.println("4. Comprar tiquetes");
            System.out.println("5. Ver mis tiquetes");
            System.out.println("6. Transferir tiquete");
            System.out.println("7. MARKETPLACE - Reventa de tiquetes");
            System.out.println("8. Ver reportes");
            System.out.println("9. Cerrar sesión");
            System.out.println("0. Salir del sistema");
            System.out.println("═".repeat(50));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione una opción: ", 0, 9);
            
            switch (opcion) {
                case 1 -> verPerfil();
                case 2 -> recargarSaldo();
                case 3 -> verEventosDisponibles();
                case 4 -> comprarTiquetes();
                case 5 -> verMisTiquetes();
                case 6 -> transferirTiquete();
                case 7 -> menuMarketplace();
                case 8 -> verReportes();
                case 9 -> { 
                    System.out.println("👋 Cerrando sesión de " + clienteActual.getNombre() + "...");
                    clienteActual = null;
                    return;
                }
                case 0 -> salirDelSistema();
            }
        }
    }

    // ===== MÉTODOS DEL SISTEMA BASE =====
    
    private void verPerfil() {
        InputValidator.mostrarHeader("MI PERFIL");
        System.out.println(clienteActual.mostrarPerfil());
        InputValidator.presioneEnter();
    }
    
    private void recargarSaldo() {
        InputValidator.mostrarHeader("RECARGAR SALDO");
        double monto = InputValidator.leerDoublePositivo("Monto a recargar: $");
        clienteActual.recargarSaldo(monto);
        System.out.println("Saldo recargado exitosamente. Nuevo saldo: $" + clienteActual.getSaldoVirtual());
        
        sistema.guardarDatosAutomaticamente();
        
        InputValidator.presioneEnter();
    }
    
    private void verEventosDisponibles() {
        InputValidator.mostrarHeader("EVENTOS DISPONIBLES");
        List<Evento> eventos = sistema.getEventos();
        
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos disponibles en este momento.");
        } else {
            for (int i = 0; i < eventos.size(); i++) {
                Evento evento = eventos.get(i);
                if ("activo".equals(evento.getEstado()) && evento.hayTiquetesDisponibles()) {
                    System.out.println((i + 1) + ". " + evento.getNombre() + 
                                     " - " + evento.getFechaHora() + 
                                     " - $" + evento.getLocalidades().get(0).getPrecioFinal());
                }
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void comprarTiquetes() {
        InputValidator.mostrarHeader("COMPRAR TIQUETES - MODO PRUEBA");
        
        Venue venuePrueba = sistema.getVenues().get(0);
        Organizador orgPrueba = (Organizador) sistema.buscarUsuario("organizador1");
        
        Evento eventoPrueba = new Evento("E-PRUEBA", "Concierto de Prueba", 
            LocalDateTime.now().plusDays(7), "musical", orgPrueba, venuePrueba);
        
        Localidad localidad = new Localidad("General", 50000, false, 100, "basico", eventoPrueba);
        eventoPrueba.agregarLocalidad(localidad);
        
        Tiquete tiquetePrueba = new TiqueteBasico("TQ-PRUEBA", 50000, 5000, 2000, clienteActual, localidad);
        
        if (clienteActual.deducirSaldo(57000)) {
            clienteActual.agregarTiquete(tiquetePrueba);
            System.out.println("¡Tiquete comprado exitosamente!");
            System.out.println("ID Tiquete: TQ-PRUEBA");
            System.out.println("Evento: Concierto de Prueba");
            System.out.println("Precio pagado: $57,000");
            System.out.println("Saldo restante: $" + clienteActual.getSaldoVirtual());
            
            sistema.guardarDatosAutomaticamente();
        } else {
            System.out.println("Saldo insuficiente");
        }
        InputValidator.presioneEnter();
    }
    
    private void verMisTiquetes() {
        InputValidator.mostrarHeader("MIS TIQUETES");
        List<Tiquete> tiquetes = clienteActual.getTiquetes();
        
        if (tiquetes.isEmpty()) {
            System.out.println("No tienes tiquetes en tu posesión.");
        } else {
            for (int i = 0; i < tiquetes.size(); i++) {
                Tiquete tiquete = tiquetes.get(i);
                System.out.println((i + 1) + ". " + tiquete.getId() + 
                                 " - " + tiquete.getEvento().getNombre() +
                                 " - $" + tiquete.calcularPrecioFinal() +
                                 " - " + tiquete.getEstado());
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void transferirTiquete() {
        InputValidator.mostrarHeader("TRANSFERIR TIQUETE");
        System.out.println("Función de transferencia de tiquetes - Para implementación completa");
        InputValidator.presioneEnter();
    }
    
    private void verReportes() {
        InputValidator.mostrarHeader("MIS REPORTES");
        System.out.println("Total de tiquetes: " + clienteActual.getTiquetes().size());
        System.out.println("Saldo actual: $" + clienteActual.getSaldoVirtual());
        
        long tiquetesRevendibles = clienteActual.getTiquetes().stream()
                .filter(Tiquete::esRevendible)
                .count();
        System.out.println("Tiquetes revendibles: " + tiquetesRevendibles);
        
        InputValidator.presioneEnter();
    }

    // ===== MÉTODOS DEL MARKETPLACE =====
    
    private void menuMarketplace() {
        while (true) {
            InputValidator.mostrarHeader("MARKETPLACE - REVENTA DE TIQUETES");
            System.out.println("Ofertas activas en el sistema: " + 
                             sistema.getMarketplace().getOfertasActivas().size());
            
            System.out.println("\n" + "═".repeat(50));
            System.out.println("1. Publicar tiquete para reventa");
            System.out.println("2. Ver ofertas activas disponibles");
            System.out.println("3. Hacer contraoferta");
            System.out.println("4. Ver mis ofertas publicadas");
            System.out.println("5. Eliminar mi oferta");
            System.out.println("6. Ver contraofertas recibidas");
            System.out.println("7. Aceptar contraoferta");
            System.out.println("8. Rechazar contraoferta");
            System.out.println("9. Estadísticas del Marketplace");
            System.out.println("0. Volver al menú principal");
            System.out.println("═".repeat(50));
            
            int opcion = InputValidator.leerEnteroEnRango("Seleccione una opción: ", 0, 9);
            
            switch (opcion) {
                case 1 -> publicarOfertaReventa();
                case 2 -> verOfertasActivas();
                case 3 -> hacerContraOferta();
                case 4 -> verMisOfertas();
                case 5 -> eliminarMiOferta();
                case 6 -> verContraOfertasRecibidas();
                case 7 -> aceptarContraOferta();
                case 8 -> rechazarContraOferta();
                case 9 -> verEstadisticasMarketplace();
                case 0 -> { return; }
            }
        }
    }
    
    private void publicarOfertaReventa() {
        InputValidator.mostrarHeader("PUBLICAR OFERTA DE REVENTA");
        
        List<Tiquete> tiquetesRevendibles = clienteActual.getTiquetes().stream()
                .filter(Tiquete::esRevendible)
                .filter(t -> !"reembolsado".equals(t.getEstado()))
                .filter(t -> !"usado".equals(t.getEstado()))
                .toList();
        
        if (tiquetesRevendibles.isEmpty()) {
            System.out.println("No tienes tiquetes que puedas revender.");
            System.out.println("Los tiquetes deben ser revendibles y no estar reembolsados o usados.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("TUS TIQUETES REVENDIBLES:");
        for (int i = 0; i < tiquetesRevendibles.size(); i++) {
            Tiquete tiquete = tiquetesRevendibles.get(i);
            System.out.println((i + 1) + ". " + tiquete.getId() + 
                             " - " + tiquete.getEvento().getNombre() +
                             " - Precio original: $" + tiquete.calcularPrecioFinal());
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("\nSeleccione el tiquete a revender: ", 
                                                        1, tiquetesRevendibles.size());
        Tiquete tiqueteSeleccionado = tiquetesRevendibles.get(seleccion - 1);
        
        double precioVenta = InputValidator.leerDoublePositivo("Precio de venta: $");
        
        boolean exito = sistema.publicarOfertaReventa(tiqueteSeleccionado.getId(), precioVenta);
        if (exito) {
            System.out.println("¡Oferta publicada exitosamente!");
        } else {
            System.out.println("Error al publicar la oferta.");
        }
        InputValidator.presioneEnter();
    }
    
    private void verOfertasActivas() {
        InputValidator.mostrarHeader("OFERTAS ACTIVAS EN MARKETPLACE");
        List<OfertaReventa> ofertas = sistema.getOfertasActivasMarketplace();
        
        if (ofertas.isEmpty()) {
            System.out.println("No hay ofertas activas en el Marketplace en este momento.");
        } else {
            System.out.println("Total de ofertas: " + ofertas.size());
            System.out.println();
            
            for (int i = 0; i < ofertas.size(); i++) {
                OfertaReventa oferta = ofertas.get(i);
                // No mostrar las ofertas propias
                if (!oferta.getVendedor().equals(clienteActual)) {
                    System.out.println((i + 1) + ". ID " + oferta.getId());
                    System.out.println("   Tiquete: " + oferta.getTiquete().getId());
                    System.out.println("   Evento: " + oferta.getTiquete().getEvento().getNombre());
                    System.out.println("   Precio: $" + oferta.getPrecioVenta());
                    System.out.println("   Vendedor: " + oferta.getVendedor().getNombre());
                    System.out.println("   Publicada: " + oferta.getFechaPublicacion());
                    System.out.println();
                }
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void hacerContraOferta() {
        InputValidator.mostrarHeader("HACER CONTRAOFERTA");
        List<OfertaReventa> ofertas = sistema.getOfertasActivasMarketplace().stream()
                .filter(o -> !o.getVendedor().equals(clienteActual))
                .toList();
        
        if (ofertas.isEmpty()) {
            System.out.println("No hay ofertas disponibles para hacer contraofertas.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("OFERTAS DISPONIBLES PARA CONTRAOFERTAR:");
        for (int i = 0; i < ofertas.size(); i++) {
            OfertaReventa oferta = ofertas.get(i);
            System.out.println((i + 1) + ". ID: " + oferta.getId());
            System.out.println("   * " + oferta.getTiquete().getEvento().getNombre());
            System.out.println("   Precio actual: $" + oferta.getPrecioVenta());
            System.out.println("   Vendedor: " + oferta.getVendedor().getNombre());
            System.out.println();
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione la oferta: ", 1, ofertas.size());
        OfertaReventa ofertaSeleccionada = ofertas.get(seleccion - 1);
        
        double precioPropuesto = InputValidator.leerDoublePositivo("Su precio propuesto: $");
        
        boolean exito = sistema.hacerContraOferta(ofertaSeleccionada.getId(), precioPropuesto);
        if (exito) {
            System.out.println("¡Contraoferta enviada exitosamente!");
        } else {
            System.out.println("Error al enviar la contraoferta.");
        }
        InputValidator.presioneEnter();
    }
    
    private void verMisOfertas() {
        InputValidator.mostrarHeader("MIS OFERTAS PUBLICADAS");
        List<OfertaReventa> misOfertas = sistema.getOfertasActivasMarketplace().stream()
                .filter(o -> o.getVendedor().equals(clienteActual))
                .toList();
        
        if (misOfertas.isEmpty()) {
            System.out.println("No tienes ofertas publicadas.");
        } else {
            System.out.println("Tus ofertas activas:");
            for (int i = 0; i < misOfertas.size(); i++) {
                OfertaReventa oferta = misOfertas.get(i);
                System.out.println((i + 1) + ". ID: " + oferta.getId());
                System.out.println("   Tiquete: " + oferta.getTiquete().getId());
                System.out.println("   Evento: " + oferta.getTiquete().getEvento().getNombre());
                System.out.println("   Precio: $" + oferta.getPrecioVenta());
                System.out.println("   Publicada: " + oferta.getFechaPublicacion());
                System.out.println();
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void eliminarMiOferta() {
        InputValidator.mostrarHeader("ELIMINAR MI OFERTA");
        List<OfertaReventa> misOfertas = sistema.getOfertasActivasMarketplace().stream()
                .filter(o -> o.getVendedor().equals(clienteActual))
                .toList();
        
        if (misOfertas.isEmpty()) {
            System.out.println("No tienes ofertas para eliminar.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("TUS OFERTAS PARA ELIMINAR:");
        for (int i = 0; i < misOfertas.size(); i++) {
            OfertaReventa oferta = misOfertas.get(i);
            System.out.println((i + 1) + ". ID: " + oferta.getId() + 
                             " - " + oferta.getTiquete().getEvento().getNombre() +
                             " - $" + oferta.getPrecioVenta());
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione la oferta a eliminar: ", 
                                                        1, misOfertas.size());
        OfertaReventa ofertaSeleccionada = misOfertas.get(seleccion - 1);
        
        boolean exito = sistema.eliminarMiOferta(ofertaSeleccionada.getId());
        if (exito) {
            System.out.println("Oferta eliminada exitosamente");
        } else {
            System.out.println("No se pudo eliminar la oferta");
        }
        InputValidator.presioneEnter();
    }
    
    private void verContraOfertasRecibidas() {
        InputValidator.mostrarHeader("CONTRAOFERTAS RECIBIDAS");
        List<ContraOferta> contraOfertas = sistema.getContraOfertasPendientes();
        
        if (contraOfertas.isEmpty()) {
            System.out.println("No tienes contraofertas pendientes.");
        } else {
            System.out.println("Contraofertas pendientes:");
            for (int i = 0; i < contraOfertas.size(); i++) {
                ContraOferta co = contraOfertas.get(i);
                System.out.println((i + 1) + ". ID: " + co.getId());
                System.out.println("   Oferta: " + co.getOfertaReventa().getId());
                System.out.println("   Comprador: " + co.getComprador().getNombre());
                System.out.println("   Oferta: $" + co.getPrecioPropuesto());
                System.out.println("   Fecha: " + co.getFecha());
                System.out.println();
            }
        }
        InputValidator.presioneEnter();
    }
    
    private void aceptarContraOferta() {
        InputValidator.mostrarHeader("ACEPTAR CONTRAOFERTA");
        List<ContraOferta> contraOfertas = sistema.getContraOfertasPendientes();
        
        if (contraOfertas.isEmpty()) {
            System.out.println("No tienes contraofertas pendientes para aceptar.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("CONTRAOFERTAS PENDIENTES PARA ACEPTAR:");
        for (int i = 0; i < contraOfertas.size(); i++) {
            ContraOferta co = contraOfertas.get(i);
            System.out.println((i + 1) + ". ID: " + co.getId());
            System.out.println("   Comprador: " + co.getComprador().getNombre());
            System.out.println("   Oferta: $" + co.getPrecioPropuesto());
            System.out.println("   Evento: " + co.getOfertaReventa().getTiquete().getEvento().getNombre());
            System.out.println("   Tiquete: " + co.getOfertaReventa().getTiquete().getId());
            System.out.println();
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione la contraoferta a aceptar: ", 1, contraOfertas.size());
        ContraOferta contraOfertaSeleccionada = contraOfertas.get(seleccion - 1);
        
        System.out.println("Confirmando transacción...");
        System.out.println("   Vendedor: " + clienteActual.getNombre());
        System.out.println("   Comprador: " + contraOfertaSeleccionada.getComprador().getNombre());
        System.out.println("   Monto: $" + contraOfertaSeleccionada.getPrecioPropuesto());
        System.out.println("   Tiquete: " + contraOfertaSeleccionada.getOfertaReventa().getTiquete().getId());
        
        boolean confirmar = InputValidator.leerSiNo("¿Confirmar la aceptación de esta contraoferta?");
        
        if (confirmar) {
            boolean exito = sistema.aceptarContraOferta(contraOfertaSeleccionada.getId());
            if (exito) {
                System.out.println("¡Transacción completada exitosamente!");
            } else {
                System.out.println("La transacción no pudo completarse. Revisa los mensajes de error.");
            }
        } else {
            System.out.println("Transacción cancelada por el usuario.");
        }
        
        InputValidator.presioneEnter();
    }
    
    private void rechazarContraOferta() {
        InputValidator.mostrarHeader("RECHAZAR CONTRAOFERTA");
        List<ContraOferta> contraOfertas = sistema.getContraOfertasPendientes();
        
        if (contraOfertas.isEmpty()) {
            System.out.println("No tienes contraofertas pendientes para rechazar.");
            InputValidator.presioneEnter();
            return;
        }
        
        System.out.println("CONTRAOFERTAS PENDIENTES PARA RECHAZAR:");
        for (int i = 0; i < contraOfertas.size(); i++) {
            ContraOferta co = contraOfertas.get(i);
            System.out.println((i + 1) + ". ID " + co.getId());
            System.out.println("   Comprador: " + co.getComprador().getNombre());
            System.out.println("   Oferta: $" + co.getPrecioPropuesto());
            System.out.println();
        }
        
        int seleccion = InputValidator.leerEnteroEnRango("Seleccione la contraoferta a rechazar: ", 
                                                        1, contraOfertas.size());
        ContraOferta contraOfertaSeleccionada = contraOfertas.get(seleccion - 1);
        
        sistema.rechazarContraOferta(contraOfertaSeleccionada.getId());
        InputValidator.presioneEnter();
    }
    
    private void verEstadisticasMarketplace() {
        InputValidator.mostrarHeader("ESTADÍSTICAS DEL MARKETPLACE");
        
        List<OfertaReventa> ofertasActivas = sistema.getOfertasActivasMarketplace();
        List<ContraOferta> misContraOfertas = sistema.getContraOfertasPendientes();
        
        long misOfertasActivas = ofertasActivas.stream()
                .filter(o -> o.getVendedor().equals(clienteActual))
                .count();
        
        System.out.println("ESTADÍSTICAS PERSONALES:");
        System.out.println("Mis ofertas activas: " + misOfertasActivas);
        System.out.println("Contraofertas pendientes: " + misContraOfertas.size());
        System.out.println("Saldo disponible: $" + clienteActual.getSaldoVirtual());
        
        System.out.println("\n ESTADÍSTICAS GENERALES:");
        System.out.println("Ofertas totales en Marketplace: " + ofertasActivas.size());
        
        InputValidator.presioneEnter();
    }

    // ===== MÉTODOS DEL SISTEMA =====
    
    private void salirDelSistema() {
        System.out.println(" Guardando datos...");
        sistema.getPersistencia().guardarDatos();
        System.out.println(" ¡Hasta pronto!");
        System.exit(0);
    }
}