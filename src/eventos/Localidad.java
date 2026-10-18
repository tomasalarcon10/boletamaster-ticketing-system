package eventos;

import java.util.ArrayList;
import java.util.List;

public class Localidad {

    private String nombre;
    private double precioBase;
    private boolean numerada;
    private int capacidad;
    private int tiquetesVendidos;
    private List<Oferta> ofertas;
    private String tipoTiquetes;
    private Evento evento; // referencia al evento al que pertenece

    /**
     * Constructor de la localidad
     */
    public Localidad(String nombre, double precioBase, boolean numerada,
                     int capacidad, String tipoTiquetes, Evento evento) {
        this.nombre = nombre;
        this.precioBase = Math.max(0, precioBase);
        this.numerada = numerada;
        this.capacidad = Math.max(0, capacidad);
        this.tipoTiquetes = tipoTiquetes;
        this.evento = evento;
        this.tiquetesVendidos = 0;
        this.ofertas = new ArrayList<>();
    }
 // ====== MÉTODOS DE APOYO PARA ORGANIZADOR ======

 // Calcula las ganancias de esta localidad (ingresos)
 public double calcularGananciasOrganizador() {
     return calcularIngresos();
 }

 // Calcula el porcentaje de ventas en esta localidad
 public double calcularPorcentajeVentas() {
     if (capacidad == 0) return 0.0;
     return (tiquetesVendidos * 100.0) / capacidad;
 }
    // ====== GETTERS ======
    public String getNombre() { return nombre; }
    public double getPrecioBase() { return precioBase; }
    public boolean isNumerada() { return numerada; }
    public int getCapacidad() { return capacidad; }
    public int getTiquetesVendidos() { return tiquetesVendidos; }
    public String getTipoTiquetes() { return tipoTiquetes; }
    public List<Oferta> getOfertas() { return ofertas; }
    public Evento getEvento() { return evento; }

    // ====== SETTERS ======
    public void setNombre(String nombre) { this.nombre = nombre; }

    public void setPrecioBase(double precioBase) {
        if (precioBase >= 0) {
            this.precioBase = precioBase;
        }
    }

    public void setTipoTiquetes(String tipoTiquetes) {
        this.tipoTiquetes = tipoTiquetes;
    }

    public void setCapacidad(int nuevaCapacidad) {
        if (nuevaCapacidad >= tiquetesVendidos) {
            this.capacidad = nuevaCapacidad;
        } else {
            System.out.println("Error: No se puede reducir la capacidad por debajo de los tiquetes ya vendidos.");
        }
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    // ====== LÓGICA DE VENTA ======
    public void venderTiquete() {
        if (tiquetesVendidos < capacidad) {
            tiquetesVendidos++;
        } else {
            System.out.println("Error: No hay más tiquetes disponibles en esta localidad.");
        }
    }

    public boolean venderTiquetes(int cantidad) {
        if (tiquetesVendidos + cantidad <= capacidad) {
            tiquetesVendidos += cantidad;
            return true;
        } else {
            System.out.println("Error: No hay suficientes tiquetes disponibles.");
            return false;
        }
    }

    public void cancelarVenta() {
        if (tiquetesVendidos > 0) {
            tiquetesVendidos--;
        }
    }

    // ====== DISPONIBILIDAD ======
    public boolean hayDisponibles() {
        return tiquetesVendidos < capacidad;
    }

    public boolean hayDisponibles(int cantidad) {
        return (tiquetesVendidos + cantidad) <= capacidad;
    }

    public int getTiquetesDisponibles() {
        return capacidad - tiquetesVendidos;
    }

    // ====== OFERTAS ======
    public void aplicarOferta(Oferta oferta) {
        if (oferta != null) {
            this.ofertas.add(oferta);
        }
    }

    public boolean eliminarOferta(String idOferta) {
        for (int i = 0; i < ofertas.size(); i++) {
            if (ofertas.get(i).getId().equals(idOferta)) {
                ofertas.remove(i);
                return true;
            }
        }
        return false;
    }

    public double getPrecioFinal() {
        double precioFinal = precioBase;
        for (Oferta oferta : ofertas) {
            if (oferta.estaVigente()) {
                precioFinal = oferta.aplicarDescuento(precioBase);
                break; // aplica solo la primera oferta vigente
            }
        }
        return precioFinal;
    }

    public Oferta getOfertaVigente() {
        for (Oferta oferta : ofertas) {
            if (oferta.estaVigente()) {
                return oferta;
            }
        }
        return null;
    }

    // ====== CÁLCULOS ======
    public double calcularPorcentajeOcupacion() {
        if (capacidad == 0) return 0.0;
        return (tiquetesVendidos * 100.0) / capacidad;
    }

    public double calcularIngresos() {
        return tiquetesVendidos * getPrecioFinal();
    }

    // ====== VISUALIZACIÓN ======
    public void mostrarInformacion() {
        System.out.println("=== LOCALIDAD: " + nombre + " ===");
        if (evento != null) {
            System.out.println("Evento: " + evento.getNombre());
        }
        System.out.println("Precio base: $" + String.format("%.2f", precioBase));

        Oferta ofertaActual = getOfertaVigente();
        if (ofertaActual != null) {
            System.out.println("¡OFERTA ACTIVA! " + ofertaActual.getDescuento() + "% de descuento");
            System.out.println("Precio con descuento: $" + String.format("%.2f", getPrecioFinal()));
        } else {
            System.out.println("Precio actual: $" + String.format("%.2f", getPrecioFinal()));
        }

        System.out.println("Tipo: " + (numerada ? "Numerada" : "General"));
        System.out.println("Tipo de tiquetes: " + tipoTiquetes);
        System.out.println("Disponibles: " + getTiquetesDisponibles() + "/" + capacidad);
        System.out.println("Vendidos: " + tiquetesVendidos);
        System.out.println("Ocupación: " + String.format("%.1f", calcularPorcentajeOcupacion()) + "%");
        System.out.println("Ingresos: $" + String.format("%.2f", calcularIngresos()));
    }

    public void mostrarResumen() {
        System.out.println(nombre + " | $" + String.format("%.0f", getPrecioFinal()) +
                " | " + getTiquetesDisponibles() + "/" + capacidad + " disponibles");
    }

    @Override
    public String toString() {
        return nombre + " - $" + getPrecioFinal() + " (" + getTiquetesDisponibles() + " disponibles)";
    }
}