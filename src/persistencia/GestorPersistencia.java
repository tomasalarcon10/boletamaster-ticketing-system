package persistencia;

import sistema.SistemaVentaTiquetes;
import usuarios.*;
import eventos.*;
import finanzas.*;
import tiquetes.*;
import marketplace.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Clase que maneja la persistencia del sistema (archivos planos).
 * EXTENDIDA para soportar el Marketplace
 */
public class GestorPersistencia implements IPersistencia {

    private final String rutaBase;
    private final SistemaVentaTiquetes sistema;
    private final String SEP = " | ";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter FMT_DETAILED = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public GestorPersistencia(String rutaBase, SistemaVentaTiquetes sistema) {
        this.rutaBase = rutaBase;
        this.sistema = sistema;
        crearDirectorio();
    }

    private void crearDirectorio() {
        File dir = new File(rutaBase);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Carpeta de datos creada: " + rutaBase);
        }
    }

    // ===========================
    // MÉTODOS PRINCIPALES
    // ===========================

    @Override
    public void guardarDatos() {
        System.out.println("Guardando datos en: " + rutaBase);
        
        try {
            guardarUsuarios(sistema.getUsuarios());
            guardarVenues(sistema.getVenues());
            guardarEventos(sistema.getEventos());
            guardarTransacciones(sistema.getTransacciones());
            guardarReembolsos(sistema.getReembolsos());
            
            guardarOfertasReventa(sistema.getMarketplace().getOfertasActivas());
            guardarContraOfertas(sistema.getMarketplace().getTodasContraOfertas());
            guardarLogsMarketplace(sistema.getMarketplace().getLogs());
            
            System.out.println("Todos los datos guardados correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al guardar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void cargarDatos() {
        System.out.println("Cargando datos...");
        
        File carpetaDatos = new File(rutaBase);
        File archivoUsuarios = new File(rutaBase, "usuarios.txt");
        
        if (!carpetaDatos.exists() || !archivoUsuarios.exists()) {
            System.out.println("No se encontraron datos existentes, creando datos por defecto...");
            inicializarDatosPorDefecto();
        } else {
            try {
                if (archivoUsuarios.length() == 0) {
                    System.out.println("Archivo de usuarios vacío, creando datos por defecto...");
                    inicializarDatosPorDefecto();
                }
            } catch (Exception e) {
                System.out.println("Error verificando archivos, creando datos por defecto...");
                inicializarDatosPorDefecto();
            }
        }
        
        try {
            sistema.setUsuarios(cargarUsuarios());
            sistema.setVenues(cargarVenues());
            sistema.setEventos(cargarEventos());
            sistema.setTransacciones(cargarTransacciones());
            sistema.setReembolsos(cargarReembolsos());
            
            cargarOfertasReventa();
            cargarContraOfertas();
            cargarLogsMarketplace();
            
            System.out.println("Datos cargados correctamente. Usuarios: " + sistema.getUsuarios().size());
            
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===========================
    // MARKETPLACE
    // ===========================

    private void guardarOfertasReventa(List<OfertaReventa> ofertas) throws IOException {
        File f = new File(rutaBase, "ofertasReventa.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (OfertaReventa oferta : ofertas) {
                if (oferta.isActiva()) {
                    pw.println(oferta.getId() + SEP + 
                              oferta.getTiquete().getId() + SEP + 
                              oferta.getVendedor().getLogin() + SEP + 
                              oferta.getPrecioVenta() + SEP + 
                              oferta.getFechaPublicacion().format(FMT) + SEP + 
                              oferta.isActiva());
                }
            }
        }
    }

    private void guardarContraOfertas(List<ContraOferta> contraOfertas) throws IOException {
        File f = new File(rutaBase, "contraofertas.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (ContraOferta co : contraOfertas) {
                pw.println(co.getId() + SEP + 
                          co.getOfertaReventa().getId() + SEP + 
                          co.getComprador().getLogin() + SEP + 
                          co.getPrecioPropuesto() + SEP + 
                          co.getFecha().format(FMT) + SEP + 
                          co.getEstado());
            }
        }
    }

    private void guardarLogsMarketplace(List<LogEntry> logs) throws IOException {
        File f = new File(rutaBase, "logMarketplace.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (LogEntry log : logs) {
                pw.println(log.getId() + SEP + 
                          log.getFechaHora().format(FMT_DETAILED) + SEP + 
                          log.getUsuario().getLogin() + SEP + 
                          log.getAccion() + SEP + 
                          log.getDetalles());
            }
        }
    }

    private void cargarOfertasReventa() throws IOException {
        File f = new File(rutaBase, "ofertasReventa.txt");
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split("\\s*\\|\\s*");
                if (p.length < 6) continue;

                String idOferta = p[0];
                String idTiquete = p[1];
                String loginVendedor = p[2];
                double precioVenta = Double.parseDouble(p[3]);
                LocalDateTime fechaPublicacion = LocalDateTime.parse(p[4], FMT);
                boolean activa = Boolean.parseBoolean(p[5]);

                Usuario vendedor = sistema.buscarUsuario(loginVendedor);
                Tiquete tiquete = buscarTiqueteEnSistema(idTiquete);

                if (vendedor instanceof Cliente && tiquete != null) {
                    OfertaReventa oferta = new OfertaReventa(idOferta, tiquete, (Cliente) vendedor, precioVenta);
                    oferta.setFechaPublicacion(fechaPublicacion); // SETTER DIRECTAMENTE
                    oferta.setActiva(activa);
                    
                    if (activa) {
                        sistema.getMarketplace().getOfertasActivas().add(oferta);
                    }
                }
            }
        }
    }

    private void cargarContraOfertas() throws IOException {
        File f = new File(rutaBase, "contraofertas.txt");
        if (!f.exists()) {
            System.out.println("No se encontró archivo de contraofertas, se creará uno nuevo.");
            return;
        }

        System.out.println("Cargando contraofertas...");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            int count = 0;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split("\\s*\\|\\s*");
                if (p.length < 6) {
                    System.out.println("Línea inválida en contraofertas: " + linea);
                    continue;
                }

                String idContraOferta = p[0];
                String idOfertaReventa = p[1];
                String loginComprador = p[2];
                double precioPropuesto = Double.parseDouble(p[3]);
                LocalDateTime fecha = LocalDateTime.parse(p[4], FMT);
                String estado = p[5];

                Usuario comprador = sistema.buscarUsuario(loginComprador);
                OfertaReventa ofertaReventa = sistema.getMarketplace().buscarOferta(idOfertaReventa);

                if (comprador instanceof Cliente && ofertaReventa != null) {
                    ContraOferta contraOferta = new ContraOferta(idContraOferta, ofertaReventa, (Cliente) comprador, precioPropuesto);
                    contraOferta.setFecha(fecha);
                    contraOferta.setEstado(estado);
                    
                    if ("PENDIENTE".equals(estado)) {
                        sistema.getMarketplace().agregarContraOfertaPendiente(contraOferta);
                        count++;
                    }
                } else {
                    System.out.println("No se pudo cargar contraoferta " + idContraOferta + 
                                     " - Comprador: " + (comprador != null) + 
                                     ", Oferta: " + (ofertaReventa != null));
                }
            }
            System.out.println("* " + count + " contraofertas cargadas");
        }
    }

    private void cargarLogsMarketplace() throws IOException {
        File f = new File(rutaBase, "logMarketplace.txt");
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split("\\s*\\|\\s*");
                if (p.length < 5) continue;

                String idLog = p[0];
                LocalDateTime fechaHora = LocalDateTime.parse(p[1], FMT_DETAILED);
                String loginUsuario = p[2];
                String accion = p[3];
                String detalles = p[4];

                Usuario usuario = sistema.buscarUsuario(loginUsuario);
                if (usuario != null) {
                    LogEntry logEntry = new LogEntry(idLog, usuario, accion, detalles);
                    logEntry.setFechaHora(fechaHora); // SETTER DIRECTAMENTE
                    sistema.getMarketplace().getLogs().add(logEntry);
                }
            }
        }
    }

    // ===========================
    // MÉTODOS AUXILIARES
    // ===========================

    private Tiquete buscarTiqueteEnSistema(String idTiquete) {
        for (Usuario usuario : sistema.getUsuarios()) {
            if (usuario instanceof Cliente) {
                Cliente cliente = (Cliente) usuario;
                for (Tiquete tiquete : cliente.getTiquetes()) {
                    if (tiquete.getId().equals(idTiquete)) {
                        return tiquete;
                    }
                }
            }
        }
        return null;
    }

    // ===========================
    // MÉTODOS de PROYECTO 1
    // ===========================

    private void guardarUsuarios(List<Usuario> lista) throws IOException {
        File f = new File(rutaBase, "usuarios.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Usuario u : lista) {
                String tipo = u.getClass().getSimpleName().toLowerCase();
                pw.println(u.getLogin() + SEP + u.getPassword() + SEP +
                           u.getNombre() + SEP + tipo + SEP + u.getSaldoVirtual());
            }
        }
    }

    private List<Usuario> cargarUsuarios() throws IOException {
        List<Usuario> lista = new ArrayList<>();
        File f = new File(rutaBase, "usuarios.txt");
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split("\\s*\\|\\s*");
                if (p.length < 5) continue;

                String login = p[0], pass = p[1], nombre = p[2], tipo = p[3];
                double saldo = 0.0;
                
                try {
                    saldo = Double.parseDouble(p[4]);
                    // Asegurar que el saldo no sea negativo
                    if (saldo < 0) {
                        saldo = 0.0;
                        System.out.println("Saldo negativo corregido para usuario: " + login);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error parseando saldo para " + login + ", usando 0.0: " + p[4]);
                    saldo = 0.0;
                }

                Usuario u = switch (tipo) {
                    case "cliente" -> new Cliente(login, pass, nombre);
                    case "organizador" -> new Organizador(login, pass, nombre);
                    case "administrador" -> new Administrador(login, pass, nombre);
                    default -> null;
                };
                
                if (u != null) {
                    // En lugar de usar recargarSaldo que valida positivo, asignar directamente
                    u.setSaldoVirtual(saldo);
                    lista.add(u);
                }
            }
        }
        return lista;
    }
    
    private void guardarVenues(List<Venue> lista) throws IOException {
        File f = new File(rutaBase, "venues.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Venue v : lista) {
                pw.println(v.getId() + SEP + v.getNombre() + SEP + v.getUbicacion() + SEP +
                           v.getCapacidadTotal() + SEP + v.getRestricciones() + SEP + v.isAprobado());
            }
        }
    }

    private List<Venue> cargarVenues() throws IOException {
        List<Venue> lista = new ArrayList<>();
        File f = new File(rutaBase, "venues.txt");
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split("\\s*\\|\\s*");
                if (p.length < 6) continue;

                Venue v = new Venue(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4]);
                v.setAprobado(Boolean.parseBoolean(p[5]));
                lista.add(v);
            }
        }
        return lista;
    }

    private void guardarEventos(List<Evento> lista) throws IOException {
        File f = new File(rutaBase, "eventos.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Evento e : lista) {
                pw.println(e.getId() + SEP + e.getNombre() + SEP +
                           e.getFechaHora().format(FMT) + SEP + e.getTipo() + SEP +
                           e.getOrganizador().getLogin() + SEP + e.getVenue().getId() + SEP +
                           e.getEstado());
            }
        }
    }

    private List<Evento> cargarEventos() throws IOException {
        List<Evento> lista = new ArrayList<>();
        File f = new File(rutaBase, "eventos.txt");
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split("\\s*\\|\\s*");
                if (p.length < 7) continue;

                String id = p[0], nombre = p[1];
                LocalDateTime fecha = LocalDateTime.parse(p[2], FMT);
                String tipo = p[3];
                String idOrg = p[4];
                String idVenue = p[5];
                String estado = p[6];

                Organizador org = (Organizador) sistema.buscarUsuario(idOrg);
                Venue venue = sistema.buscarVenue(idVenue);

                Evento e = new Evento(id, nombre, fecha, tipo, org, venue);
                e.setEstado(estado);
                lista.add(e);
            }
        }
        return lista;
    }

    private void guardarTransacciones(List<Transaccion> lista) throws IOException {
        File f = new File(rutaBase, "transacciones.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Transaccion t : lista) {
                pw.println(t.getId() + SEP + t.getCliente().getLogin() + SEP +
                           t.getEvento().getId() + SEP + t.getMonto() + SEP +
                           t.getFecha().format(FMT) + SEP + t.getTipo());
            }
        }
    }

    private List<Transaccion> cargarTransacciones() throws IOException {
        List<Transaccion> lista = new ArrayList<>();
        File f = new File(rutaBase, "transacciones.txt");
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split("\\s*\\|\\s*");
                if (p.length < 6) continue;

                String id = p[0], login = p[1], idEvento = p[2];
                double monto = Double.parseDouble(p[3]);
                LocalDateTime fecha = LocalDateTime.parse(p[4], FMT);
                String tipo = p[5];

                Usuario u = sistema.buscarUsuario(login);
                Evento e = sistema.buscarEvento(idEvento);
                if (u instanceof Cliente c && e != null) {
                    lista.add(new Transaccion(id, c, e, monto, fecha, tipo));
                }
            }
        }
        return lista;
    }

    private void guardarReembolsos(List<Rembolso> lista) throws IOException {
        File f = new File(rutaBase, "reembolsos.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Rembolso r : lista) {
                pw.println(r.getId() + SEP + r.getCliente().getLogin() + SEP +
                           r.getEvento().getId() + SEP + r.getMontoReembolsado() + SEP +
                           r.getFecha().format(FMT));
            }
        }
    }

    private List<Rembolso> cargarReembolsos() throws IOException {
        List<Rembolso> lista = new ArrayList<>();
        File f = new File(rutaBase, "reembolsos.txt");
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split("\\s*\\|\\s*");
                if (p.length < 5) continue;

                String id = p[0];
                String login = p[1];
                String idEvento = p[2];
                double monto = Double.parseDouble(p[3]);
                LocalDateTime fecha = LocalDateTime.parse(p[4], FMT);

                Usuario u = sistema.buscarUsuario(login);
                Evento e = sistema.buscarEvento(idEvento);

                if (u instanceof Cliente && e != null) {
                    lista.add(new Rembolso(id, (Cliente) u, e, monto, fecha));
                }
            }
        }
        return lista;
    }
    
    private void inicializarDatosPorDefecto() {
        System.out.println("Inicializando datos por defecto...");
        
        try {
            // Crear usuarios por defecto con saldos positivos
            File usuariosFile = new File(rutaBase, "usuarios.txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(usuariosFile))) {
                pw.println("cliente1 | 123 | Juan Pérez | cliente | 100000.0");
                pw.println("cliente2 | 123 | María García | cliente | 50000.0");
                pw.println("organizador1 | 123 | Carlos Eventos | organizador | 0.0");
                pw.println("admin | 123 | Admin Principal | administrador | 0.0");
            }
            
            // Crear venues por defecto
            File venuesFile = new File(rutaBase, "venues.txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(venuesFile))) {
                pw.println("v1 | Estadio Atanasio | Medellín | 50000 | Ninguna | true");
                pw.println("v2 | Teatro Metro | Bogotá | 2000 | Solo eventos culturales | true");
                pw.println("v3 | Plaza de Toros | Cali | 15000 | Pendiente | false");
            }
            
            // Crear eventos por defecto con fechas futuras
            File eventosFile = new File(rutaBase, "eventos.txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(eventosFile))) {
                // Usar fechas futuras
                String fecha1 = java.time.LocalDateTime.now().plusDays(30).format(FMT);
                String fecha2 = java.time.LocalDateTime.now().plusDays(15).format(FMT);
                
                pw.println("e1 | Concierto Rock 2024 | " + fecha1 + " | musical | organizador1 | v1 | activo");
                pw.println("e2 | Festival Cultural | " + fecha2 + " | cultural | organizador1 | v2 | activo");
            }
            
            // Crear archivos vacíos para los demás
            new File(rutaBase, "transacciones.txt").createNewFile();
            new File(rutaBase, "reembolsos.txt").createNewFile();
            new File(rutaBase, "ofertasReventa.txt").createNewFile();
            new File(rutaBase, "contraofertas.txt").createNewFile();
            new File(rutaBase, "logMarketplace.txt").createNewFile();
            new File(rutaBase, "tiquetes.txt").createNewFile();
            
            System.out.println("Datos por defecto creados exitosamente");
            
        } catch (IOException e) {
            System.err.println("Error al crear datos por defecto: " + e.getMessage());
        }
    }
}