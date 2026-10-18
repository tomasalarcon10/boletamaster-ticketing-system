package Consola;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Clase utilitaria para validar entradas del usuario de manera robusta
 */
public class InputValidator {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ===== VALIDACIÓN DE NÚMEROS =====
    
    public static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número entero válido.");
            }
        }
    }
    
    public static int leerEnteroEnRango(String mensaje, int min, int max) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= min && valor <= max) {
                return valor;
            }
            System.out.println("Error: El valor debe estar entre " + min + " y " + max);
        }
    }
    
    public static double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número decimal válido.");
            }
        }
    }
    
    public static double leerDoublePositivo(String mensaje) {
        while (true) {
            double valor = leerDouble(mensaje);
            if (valor > 0) {
                return valor;
            }
            System.out.println("Error: El valor debe ser mayor a 0.");
        }
    }

    // ===== VALIDACIÓN DE TEXTO =====
    
    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        while (texto.isEmpty()) {
            System.out.println("Error: Este campo no puede estar vacío.");
            System.out.print(mensaje);
            texto = scanner.nextLine().trim();
        }
        return texto;
    }
    
    public static String leerTextoNoVacio(String mensaje) {
        String texto;
        do {
            texto = leerTexto(mensaje);
            if (texto.isEmpty()) {
                System.out.println("Error: El texto no puede estar vacío.");
            }
        } while (texto.isEmpty());
        return texto;
    }

    // ===== VALIDACIÓN DE FECHAS =====
    
    public static LocalDateTime leerFecha(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje + " (formato: yyyy-MM-dd HH:mm): ");
                String fechaStr = scanner.nextLine().trim();
                return LocalDateTime.parse(fechaStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Error: Formato de fecha inválido. Use: yyyy-MM-dd HH:mm");
            }
        }
    }
    
    public static LocalDateTime leerFechaFutura(String mensaje) {
        while (true) {
            LocalDateTime fecha = leerFecha(mensaje);
            if (fecha.isAfter(LocalDateTime.now())) {
                return fecha;
            }
            System.out.println("Error: La fecha debe ser futura.");
        }
    }

    // ===== VALIDACIÓN DE OPCIONES SÍ/NO =====
    
    public static boolean leerSiNo(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                return true;
            } else if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }
            System.out.println("Error: Responda 's' para Sí o 'n' para No.");
        }
    }

    // ===== VALIDACIÓN DE CONTRASEÑA =====
    
    public static String leerPassword(String mensaje) {
        while (true) {
            String password = leerTexto(mensaje);
            if (password.length() < 3) {
                System.out.println("Error: La contraseña debe tener al menos 3 caracteres.");
                continue;
            }
            return password;
        }
    }

    // ===== MÉTODOS DE UTILIDAD =====
    
    public static void presioneEnter() {
        System.out.println("\n Presione Enter para continuar...");
        scanner.nextLine();
    }
    
    public static void limpiarPantalla() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    
    public static void mostrarHeader(String titulo) {
        limpiarPantalla();
        System.out.println("=".repeat(60));
        System.out.println("* " + titulo);
        System.out.println("=".repeat(60));
    }
}