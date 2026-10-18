package persistencia;

/**
 * Interfaz que define los métodos básicos para la persistencia de datos.
 * Las clases que implementen esta interfaz deben encargarse de guardar
 * y cargar los datos del sistema.
 */
public interface IPersistencia {
    
    /**
     * Carga todos los datos desde el medio de almacenamiento (archivos)
     */
    void cargarDatos();
    
    /**
     * Guarda todos los datos en el medio de almacenamiento (archivos)
     */
    void guardarDatos();
}