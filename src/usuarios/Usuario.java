package usuarios;

public abstract class Usuario {
    private String login;
    private String password;
    private String nombre;
    private double saldoVirtual;
    
    public Usuario(String login, String password, String nombre) {
        this.login = login;
        this.password = password;
        this.nombre = nombre;
        this.saldoVirtual = 0.0;
    }
    
    // Getters
    public String getLogin() {
        return login;
    }
    
    public String getPassword() {
    	return password; 
    }
    public String getNombre() {
        return nombre;
    }
    
    public double getSaldoVirtual() {
        return saldoVirtual;
    }
    
    // Setters
    public void setLogin(String nuevoLogin) {
        this.login = nuevoLogin;
    }
    
    public void setPassword(String nuevaPassword) {
        this.password = nuevaPassword;
    }
    
    public void setSaldoVirtual(double nuevoSaldo) {
        this.saldoVirtual = nuevoSaldo;
    }
    
    public boolean deducirSaldo(double monto) {
        if (monto <= 0) return true;
        if (saldoVirtual >= monto) {
            saldoVirtual -= monto;
            return true;
        }
        return false;
    }
    
    
    
    // Métodos de funcionalidad
    public boolean validarPassword(String intento) {
        return this.password.equals(intento);
    }
    
    public void recargarSaldo(double monto) {
        if (monto > 0) {
            this.saldoVirtual += monto;
        } else {
            throw new IllegalArgumentException("El monto a recargar debe ser positivo.");
        }
    }
    
    public String mostrarPerfil() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================\n");
        sb.append("Login: ").append(login).append("\n");
        sb.append("Nombre: ").append(nombre).append("\n");
        sb.append("Saldo Virtual: $").append(String.format("%.2f", saldoVirtual)).append("\n");
        sb.append("=================================");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("%s (login: %s, saldo: $%.2f)", nombre, login, saldoVirtual);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return login.equals(usuario.login);
    }
    
    @Override
    public int hashCode() {
        return login.hashCode();
    }
}