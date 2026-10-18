package tests;

/**
 * Clase de utilidades para simplificar las pruebas sin usar JUnit.
 * Permite hacer comparaciones básicas e imprimir resultados de cada test.
 */
public class TestUtil {
    private static int ok = 0;
    private static int fail = 0;

    // Verifica que una condición sea verdadera
    public static void assertTrue(boolean cond, String msg) {
        if (cond) {
            ok++;
            System.out.println("OK  - " + msg);
        } else {
            fail++;
            System.out.println("FAIL- " + msg);
        }
    }

    // Compara dos objetos por igualdad
    public static void assertEquals(Object expected, Object actual, String msg) {
        boolean cond = (expected == null) ? (actual == null) : expected.equals(actual);
        assertTrue(cond, msg + " (expected=" + expected + ", actual=" + actual + ")");
    }

    // Compara dos valores numéricos con tolerancia
    public static void assertDoubleEquals(double expected, double actual, double tol, String msg) {
        boolean cond = Math.abs(expected - actual) <= tol;
        assertTrue(cond, msg + " (expected=" + expected + ", actual=" + actual + ")");
    }

    // Muestra un resumen del test actual
    public static void summary(String testName) {
        System.out.println("\n---- " + testName + " ----");
        System.out.println("OK: " + ok + " | FAIL: " + fail);
        System.out.println("-------------------------\n");
        ok = 0;
        fail = 0;
    }
}
