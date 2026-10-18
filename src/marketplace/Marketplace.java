package marketplace;

import tiquetes.Tiquete;
import usuarios.Cliente;
import usuarios.Administrador;
import usuarios.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Marketplace {
    private List<OfertaReventa> ofertasActivas;
    private List<ContraOferta> contraOfertasPendientes;
    private LogRegistry logRegistry;

    public Marketplace() {
        this.ofertasActivas = new ArrayList<>();
        this.contraOfertasPendientes = new ArrayList<>();
        this.logRegistry = new LogRegistry();
    }

    // ===== OFERTAS DE REVENTA =====
    public OfertaReventa publicarOferta(Tiquete tiquete, Cliente vendedor, double precioVenta) {
        if (tiquete == null || vendedor == null || !tiquete.esRevendible()) {
            return null;
        }

        String id = "OF-" + System.currentTimeMillis();
        OfertaReventa oferta = new OfertaReventa(id, tiquete, vendedor, precioVenta);
        ofertasActivas.add(oferta);
        logRegistry.log(vendedor, "OFERTA_PUBLICADA", "Oferta " + id + " para tiquete " + tiquete.getId());
        return oferta;
    }

    public boolean eliminarOferta(String idOferta, Cliente solicitante) {
        OfertaReventa oferta = buscarOferta(idOferta);
        if (oferta != null && oferta.getVendedor().equals(solicitante)) {
            oferta.setActiva(false);
            ofertasActivas.remove(oferta);
            logRegistry.log(solicitante, "OFERTA_ELIMINADA", "Oferta " + idOferta + " eliminada");
            return true;
        }
        return false;
    }

    public boolean eliminarOfertaAdministrador(String idOferta, Administrador admin) {
        OfertaReventa oferta = buscarOferta(idOferta);
        if (oferta != null) {
            oferta.setActiva(false);
            ofertasActivas.remove(oferta);
            logRegistry.log(admin, "OFERTA_ELIMINADA_ADMIN", "Oferta " + idOferta + " eliminada por admin");
            return true;
        }
        return false;
    }

    public List<OfertaReventa> getOfertasActivas() {
        return new ArrayList<>(ofertasActivas);
    }

    // ===== CONTRAOFERTAS =====
    public ContraOferta hacerContraOferta(OfertaReventa oferta, Cliente comprador, double precioPropuesto) {
        if (oferta == null || comprador == null || comprador.equals(oferta.getVendedor())) {
            return null;
        }

        String id = "CO-" + System.currentTimeMillis();
        ContraOferta contraOferta = new ContraOferta(id, oferta, comprador, precioPropuesto);
        contraOfertasPendientes.add(contraOferta);
        logRegistry.log(comprador, "CONTRAOFERTA_CREADA", "Contraoferta " + id + " por $" + precioPropuesto);
        return contraOferta;
    }

    public boolean aceptarContraOferta(ContraOferta contraOferta, Cliente vendedor) {
        System.out.println("DEBUG: Iniciando aceptación de contraoferta " + contraOferta.getId());
        
        if (contraOferta == null) {
            System.out.println("Error: Contraoferta nula");
            return false;
        }
        
        if (!contraOferta.getOfertaReventa().getVendedor().equals(vendedor)) {
            System.out.println("Error: El vendedor no coincide");
            System.out.println("   Vendedor esperado: " + contraOferta.getOfertaReventa().getVendedor().getNombre());
            System.out.println("   Vendedor actual: " + vendedor.getNombre());
            return false;
        }

        OfertaReventa oferta = contraOferta.getOfertaReventa();
        Tiquete tiquete = oferta.getTiquete();
        Cliente comprador = contraOferta.getComprador();
        double precioFinal = contraOferta.getPrecioPropuesto();

        System.out.println("DEBUG: Tiquete: " + tiquete.getId());
        System.out.println("DEBUG: Comprador: " + comprador.getNombre());
        System.out.println("DEBUG: Precio final: $" + precioFinal);
        System.out.println("DEBUG: Saldo comprador: $" + comprador.getSaldoVirtual());

        // Verificar que el comprador tiene saldo suficiente
        if (!comprador.comprarEnMarketplace(precioFinal)) {
            System.out.println("Error: El comprador no tiene saldo suficiente");
            System.out.println("   Saldo disponible: $" + comprador.getSaldoVirtual());
            System.out.println("   Precio requerido: $" + precioFinal);
            return false;
        }

        // Verificar que el tiquete sigue siendo válido
        if (!tiquete.esRevendible()) {
            System.out.println("Error: El tiquete ya no es revendible");
            return false;
        }

        // Realizar la transferencia
        try {
            // Transferir propiedad del tiquete
            tiquete.setDueño(comprador);
            
            // Pagar al vendedor
            vendedor.recibirPagoVenta(precioFinal);
            
            // Marcar oferta como inactiva
            oferta.setActiva(false);
            ofertasActivas.remove(oferta);
            
            // Actualizar estado de la contraoferta
            contraOferta.setEstado("ACEPTADA");
            contraOfertasPendientes.remove(contraOferta);

            System.out.println("Transferencia exitosa:");
            System.out.println("   Tiquete " + tiquete.getId() + " transferido a " + comprador.getNombre());
            System.out.println("   $" + precioFinal + " transferido a " + vendedor.getNombre());
            System.out.println("   Nuevo saldo vendedor: $" + vendedor.getSaldoVirtual());
            System.out.println("   Nuevo saldo comprador: $" + comprador.getSaldoVirtual());

            logRegistry.log(vendedor, "CONTRAOFERTA_ACEPTADA", 
                "Tiquete " + tiquete.getId() + " vendido a " + comprador.getNombre() + " por $" + precioFinal);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error durante la transferencia: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void rechazarContraOferta(ContraOferta contraOferta, Cliente vendedor) {
        if (contraOferta != null && contraOferta.getOfertaReventa().getVendedor().equals(vendedor)) {
            contraOferta.setEstado("RECHAZADA");
            contraOfertasPendientes.remove(contraOferta);
            logRegistry.log(vendedor, "CONTRAOFERTA_RECHAZADA", "Contraoferta " + contraOferta.getId() + " rechazada");
        }
    }

    public List<ContraOferta> getContraOfertasPendientes(Cliente vendedor) {
        return contraOfertasPendientes.stream()
                .filter(co -> co.getOfertaReventa().getVendedor().equals(vendedor))
                .collect(Collectors.toList());
    }

    // ===== MÉTODOS PARA PERSISTENCIA =====
    public List<ContraOferta> getTodasContraOfertas() {
        return new ArrayList<>(contraOfertasPendientes);
    }

    public void agregarContraOfertaPendiente(ContraOferta contraOferta) {
        if (contraOferta != null && "PENDIENTE".equals(contraOferta.getEstado())) {
            this.contraOfertasPendientes.add(contraOferta);
        }
    }

    // ===== LOGS =====
    public String getLogCompleto() {
        return logRegistry.getLogAsString();
    }

    public List<LogEntry> getLogs() {
        return logRegistry.getEntries();
    }

    // ===== BÚSQUEDAS =====
    public OfertaReventa buscarOferta(String idOferta) {
        return ofertasActivas.stream()
                .filter(o -> o.getId().equals(idOferta))
                .findFirst()
                .orElse(null);
    }

    public List<OfertaReventa> buscarOfertasPorEvento(String idEvento) {
        return ofertasActivas.stream()
                .filter(o -> o.getTiquete().getEvento().getId().equals(idEvento))
                .collect(Collectors.toList());
    }
}