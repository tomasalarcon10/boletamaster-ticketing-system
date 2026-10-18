package sistema;

import usuarios.*;
import eventos.*;
import tiquetes.*;
import finanzas.*;
import persistencia.GestorPersistencia;
import marketplace.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SistemaVentaTiquetes {

    private List<Usuario> usuarios;
    private List<Evento> eventos;
    private List<Venue> venues;
    private List<Transaccion> transacciones;
    private List<Rembolso> reembolsos;

    private ConfiguracionCargos configuracionCargos;
    private GestorPersistencia persistencia;
    private Usuario usuarioActual;
    private Marketplace marketplace;

    public SistemaVentaTiquetes() {
        this.usuarios = new ArrayList<>();
        this.eventos = new ArrayList<>();
        this.venues = new ArrayList<>();
        this.transacciones = new ArrayList<>();
        this.reembolsos = new ArrayList<>();
        this.configuracionCargos = new ConfiguracionCargos(10.0, 8.0, 12.0, 5.0, 5000.0);
        this.persistencia = new GestorPersistencia("./datos_sistema", this);
        this.usuarioActual = null;
        this.marketplace = new Marketplace();
    }
    
 // En SistemaVentaTiquetes.java, corregir guardarDatosAutomaticamente:
    public void guardarDatosAutomaticamente() {
        try {
            getPersistencia().guardarDatos();
            System.out.println("✅ Cambios guardados automáticamente - " + 
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        } catch (Exception e) {
            System.err.println("❌ Error al guardar datos: " + e.getMessage());
            // No usar JOptionPane aquí, solo log en consola
        }
    }
    
    public String generarResumenEvento(Evento evento) {
        if (evento == null) return "Evento no encontrado";

        StringBuilder sb = new StringBuilder();
        sb.append("\n===== RESUMEN DEL EVENTO =====\n");
        sb.append("ID: ").append(evento.getId()).append("\n");
        sb.append("Nombre: ").append(evento.getNombre()).append("\n");
        sb.append("Tipo: ").append(evento.getTipo()).append("\n");
        sb.append("Fecha: ").append(evento.getFechaHora()).append("\n");
        sb.append("Venue: ").append(evento.getVenue().getNombre()).append("\n");
        sb.append("Organizador: ").append(evento.getOrganizador().getNombre()).append("\n");
        sb.append("Estado: ").append(evento.getEstado()).append("\n\n");

        sb.append("Capacidad total: ").append(evento.getCapacidadTotal()).append("\n");
        sb.append("Tiquetes vendidos: ").append(evento.getTiquetesVendidosTotal()).append("\n");
        sb.append("Porcentaje de ventas: ")
          .append(String.format("%.2f", evento.calcularPorcentajeVentas())).append("%\n\n");

        sb.append("--- Localidades ---\n");
        for (Localidad loc : evento.getLocalidades()) {
            sb.append("• ").append(loc.getNombre())
              .append(" | Vendidos: ").append(loc.getTiquetesVendidos())
              .append("/").append(loc.getCapacidad())
              .append(" | Precio: $").append(String.format("%.2f", loc.getPrecioFinal()))
              .append("\n");
        }
        sb.append("===============================\n");

        return sb.toString();
    }
    
    public Venue buscarVenue(String id) {
        for (Venue v : venues) {
            if (v.getId().equals(id)) {
                return v;
            }
        }
        return null;
    }
    
    public Evento buscarEvento(String id) {
        for (Evento evento : eventos) {
            if (evento.getId().equals(id)) {
                return evento;
            }
        }
        return null;
    }

    // ===== GETTERS Y SETTERS =====
    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }

    public List<Evento> getEventos() { return eventos; }
    public void setEventos(List<Evento> eventos) { this.eventos = eventos; }

    public List<Venue> getVenues() { return venues; }
    public void setVenues(List<Venue> venues) { this.venues = venues; }

    public List<Transaccion> getTransacciones() { return transacciones; }
    public void setTransacciones(List<Transaccion> transacciones) { this.transacciones = transacciones; }

    public List<Rembolso> getReembolsos() { return reembolsos; }
    public void setReembolsos(List<Rembolso> reembolsos) { this.reembolsos = reembolsos; }
    
    public Marketplace getMarketplace() {
        return marketplace;
    }
    
    // ===== USUARIOS =====

    public Usuario registrarUsuario(String login, String password, String nombre, String tipo) {
        if (buscarUsuario(login) != null) {
            System.out.println("Error: el login '" + login + "' ya está registrado");
            return null;
        }

        Usuario usuario = null;
        switch (tipo.toLowerCase()) {
            case "cliente" -> usuario = new Cliente(login, password, nombre);
            case "organizador" -> usuario = new Organizador(login, password, nombre);
            case "administrador" -> {
                usuario = new Administrador(login, password, nombre);
                ((Administrador) usuario).setConfiguracionCargos(configuracionCargos);
            }
            default -> {
                System.out.println("Tipo de usuario inválido");
                return null;
            }
        }

        usuarios.add(usuario);
        System.out.println("Usuario registrado: " + nombre + " (" + tipo + ")");
        return usuario;
    }

    public Usuario autenticar(String login, String password) {
        System.out.println("Buscando usuario: " + login); // Debug
        
        Usuario u = buscarUsuario(login);
        if (u == null) {
            System.out.println("Usuario no encontrado: " + login);
            return null;
        }
        
        System.out.println("Usuario encontrado: " + u.getNombre() + ", verificando contraseña...");
        
        if (!u.validarPassword(password)) {
            System.out.println("Contraseña incorrecta para: " + login);
            return null;
        }
        
        usuarioActual = u;
        System.out.println("¡Bienvenido, " + u.getNombre() + "!");
        return u;
    }

    public void cerrarSesion() {
        if (usuarioActual != null) {
            System.out.println("Hasta luego, " + usuarioActual.getNombre());
            usuarioActual = null;
        }
    }

    public Usuario buscarUsuario(String login) {
        return usuarios.stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst()
                .orElse(null);
    }

    // ===== VENUES =====

    public void registrarVenue(Venue venue) {
        if (venue != null && !venues.contains(venue)) {
            venues.add(venue);
            System.out.println("Venue registrado: " + venue.getNombre());
        }
    }

    public List<Venue> getVenuesPendientes() {
        return venues.stream().filter(v -> !v.isAprobado()).collect(Collectors.toList());
    }

    public boolean aprobarVenue(String id) {
        if (!(usuarioActual instanceof Administrador admin)) {
            System.out.println("Solo el administrador puede aprobar venues");
            return false;
        }

        Venue v = venues.stream().filter(ve -> ve.getId().equals(id)).findFirst().orElse(null);
        if (v == null) return false;

        return admin.aprobarVenue(v);
    }

    // ===== EVENTOS =====

    public boolean registrarEvento(Evento e) {
        if (e == null || !e.getVenue().isAprobado()) {
            System.out.println("Error: el venue debe estar aprobado");
            return false;
        }

        boolean conflicto = eventos.stream().anyMatch(ev ->
                ev.getVenue().equals(e.getVenue()) &&
                ev.getFechaHora().toLocalDate().equals(e.getFechaHora().toLocalDate()) &&
                ev.getEstado().equals("activo"));

        if (conflicto) {
            System.out.println("Ya hay un evento activo ese día en el mismo venue");
            return false;
        }

        eventos.add(e);
        System.out.println("Evento registrado: " + e.getNombre());
        return true;
    }

    // ===== COMPRAS =====

    public Transaccion procesarCompra(Cliente comprador, List<Tiquete> tiquetes, String metodoPago) {
        if (comprador == null || tiquetes == null || tiquetes.isEmpty()) {
            System.out.println("Compra inválida");
            return null;
        }

        double total = tiquetes.stream().mapToDouble(Tiquete::calcularPrecioFinal).sum();
        Evento evento = tiquetes.get(0).getEvento();

        if (metodoPago.equalsIgnoreCase("saldo") && !comprador.deducirSaldo(total)) {
            System.out.println("Saldo insuficiente");
            return null;
        }

        tiquetes.forEach(comprador::comprarTiquete);

        String id = "TRANS-" + System.currentTimeMillis();
        Transaccion t = new Transaccion(id, comprador, evento, total, LocalDateTime.now(), "compra");
        transacciones.add(t);

        System.out.println("Compra exitosa (" + tiquetes.size() + " tiquetes) por $" + total);
        return t;
    }

    // ===== REEMBOLSOS =====

    public boolean procesarReembolsoIndividual(Tiquete tiquete, String motivo) {
        if (!(usuarioActual instanceof Administrador admin)) {
            System.out.println("Solo el administrador puede autorizar reembolsos");
            return false;
        }

        if (!admin.autorizarReembolsoUsuario(tiquete, motivo)) return false;

        Cliente cliente = tiquete.getDueño();
        double monto = tiquete.calcularPrecioFinal();
        cliente.recargarSaldo(monto);
        tiquete.setEstado("reembolsado");

        String id = "REMB-" + System.currentTimeMillis();
        Rembolso reembolso = new Rembolso(id, cliente, tiquete.getEvento(), monto, LocalDateTime.now());
        reembolsos.add(reembolso);

        System.out.println("Reembolso aprobado: $" + monto + " → " + cliente.getNombre());
        return true;
    }
    
    // ===== MÉTODOS DEL MARKETPLACE =====

    public boolean publicarOfertaReventa(String idTiquete, double precioVenta) {
        if (!(usuarioActual instanceof Cliente cliente)) {
            System.out.println("Solo los clientes pueden publicar ofertas de reventa");
            return false;
        }

        Tiquete tiquete = cliente.getTiquetes().stream()
                .filter(t -> t.getId().equals(idTiquete))
                .findFirst()
                .orElse(null);

        if (tiquete == null) {
            System.out.println("No se encontró el tiquete con ID: " + idTiquete);
            return false;
        }

        var oferta = marketplace.publicarOferta(tiquete, cliente, precioVenta);
        if (oferta != null) {
            System.out.println("Oferta publicada exitosamente: " + oferta.getId());
            // 🔥 GUARDADO AUTOMÁTICO AGREGADO
            guardarDatosAutomaticamente();
            return true;
        } else {
            System.out.println("No se pudo publicar la oferta");
            return false;
        }
    }

    public List<marketplace.OfertaReventa> getOfertasActivasMarketplace() {
        return marketplace.getOfertasActivas();
    }

    public boolean hacerContraOferta(String idOferta, double precioPropuesto) {
        if (!(usuarioActual instanceof Cliente comprador)) {
            System.out.println("Solo los clientes pueden hacer contraofertas");
            return false;
        }

        var oferta = marketplace.buscarOferta(idOferta);
        if (oferta == null) {
            System.out.println("No se encontró la oferta con ID: " + idOferta);
            return false;
        }

        var contraOferta = marketplace.hacerContraOferta(oferta, comprador, precioPropuesto);
        if (contraOferta != null) {
            System.out.println("Contraoferta realizada: " + contraOferta.getId());
            return true;
        } else {
            System.out.println("No se pudo realizar la contraoferta");
            return false;
        }
    }

    public List<marketplace.ContraOferta> getContraOfertasPendientes() {
        if (!(usuarioActual instanceof Cliente vendedor)) {
            return new ArrayList<>();
        }
        return marketplace.getContraOfertasPendientes(vendedor);
    }

    public boolean aceptarContraOferta(String idContraOferta) {
        System.out.println("SISTEMA: Intentando aceptar contraoferta " + idContraOferta);
        
        if (!(usuarioActual instanceof Cliente vendedor)) {
            System.out.println("Error: Solo el vendedor puede aceptar contraofertas");
            return false;
        }

        // Buscar la contraoferta
        var contraOferta = marketplace.getContraOfertasPendientes(vendedor).stream()
                .filter(co -> co.getId().equals(idContraOferta))
                .findFirst()
                .orElse(null);

        if (contraOferta == null) {
            System.out.println("Error: No se encontró la contraoferta con ID: " + idContraOferta);
            System.out.println("   Contraofertas disponibles para " + vendedor.getNombre() + ":");
            marketplace.getContraOfertasPendientes(vendedor).forEach(co -> 
                System.out.println("   - " + co.getId() + " | " + co.getComprador().getNombre() + " | $" + co.getPrecioPropuesto()));
            return false;
        }

        System.out.println("Contraoferta encontrada:");
        System.out.println("   ID: " + contraOferta.getId());
        System.out.println("   Comprador: " + contraOferta.getComprador().getNombre());
        System.out.println("   Precio: $" + contraOferta.getPrecioPropuesto());
        System.out.println("   Oferta: " + contraOferta.getOfertaReventa().getId());

        boolean resultado = marketplace.aceptarContraOferta(contraOferta, vendedor);
        
        if (resultado) {
            System.out.println("¡Contraoferta aceptada exitosamente!");
            // 🔥 GUARDADO AUTOMÁTICO AGREGADO
            guardarDatosAutomaticamente();
        } else {
            System.out.println("No se pudo aceptar la contraoferta. Revisa los mensajes anteriores.");
        }
        
        return resultado;
    }

    public void rechazarContraOferta(String idContraOferta) {
        if (!(usuarioActual instanceof Cliente vendedor)) {
            System.out.println("Solo el vendedor puede rechazar contraofertas");
            return;
        }

        var contraOferta = marketplace.getContraOfertasPendientes(vendedor).stream()
                .filter(co -> co.getId().equals(idContraOferta))
                .findFirst()
                .orElse(null);

        if (contraOferta != null) {
            marketplace.rechazarContraOferta(contraOferta, vendedor);
            System.out.println("Contraoferta rechazada");
        } else {
            System.out.println("No se encontró la contraoferta con ID: " + idContraOferta);
        }
    }

    public boolean eliminarMiOferta(String idOferta) {
        if (!(usuarioActual instanceof Cliente vendedor)) {
            System.out.println("Solo los clientes pueden eliminar sus ofertas");
            return false;
        }

        boolean resultado = marketplace.eliminarOferta(idOferta, vendedor);
        if (resultado) {
            System.out.println("Oferta eliminada exitosamente");
            // 🔥 GUARDADO AUTOMÁTICO AGREGADO
            guardarDatosAutomaticamente();
        } else {
            System.out.println("No se pudo eliminar la oferta");
        }
        return resultado;
    }

    public boolean eliminarOfertaAdministrador(String idOferta) {
        if (!(usuarioActual instanceof Administrador admin)) {
            System.out.println("Solo el administrador puede eliminar ofertas de otros usuarios");
            return false;
        }

        boolean resultado = marketplace.eliminarOfertaAdministrador(idOferta, admin);
        if (resultado) {
            System.out.println("Oferta eliminada por administrador");
        } else {
            System.out.println("No se pudo eliminar la oferta");
        }
        return resultado;
    }

    public String getLogMarketplace() {
        if (!(usuarioActual instanceof Administrador)) {
            return "Acceso denegado: Solo el administrador puede ver los logs";
        }
        return marketplace.getLogCompleto();
    }

    // ===== REPORTES =====

    public String generarReporteGeneral() {
        return """
                ===== REPORTE GENERAL =====
                Usuarios: %d
                Eventos: %d
                Venues: %d
                Transacciones: %d
                Reembolsos: %d
                """.formatted(
                usuarios.size(), eventos.size(), venues.size(),
                transacciones.size(), reembolsos.size()
        );
    }

    @Override
    public String toString() {
        return "SistemaVentaTiquetes{" +
                "usuarios=" + usuarios.size() +
                ", eventos=" + eventos.size() +
                ", venues=" + venues.size() +
                '}';
    }
    public GestorPersistencia getPersistencia() { return persistencia; }
    
    public Evento buscarEventoPorNombre(String nombre) {
        for (Evento evento : eventos) {
            if (evento.getNombre().equals(nombre)) {
                return evento;
            }
        }
        return null;
    }
}