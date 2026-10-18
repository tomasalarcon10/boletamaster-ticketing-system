package usuarios;

import tiquetes.Tiquete;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    private List<Tiquete> tiquetes;
    
    public Cliente(String login, String password, String nombre) {
        super(login, password, nombre);
        this.tiquetes = new ArrayList<>();
    }
    
    

public void agregarTiquete(Tiquete t) {
    if (t != null && !tiquetes.contains(t)) {
        tiquetes.add(t);
        t.setDueño(this);
    }
}
    
    public void comprarTiquete(Tiquete tiquete) {
        if (tiquete != null && !tiquetes.contains(tiquete)) {
            tiquetes.add(tiquete);
        }
    }
    
    public boolean transferirTiquete(Tiquete tiquete, Cliente destino) {
        if (tiquete == null || destino == null) {
            return false;
        }
        
        if (!tiquetes.contains(tiquete)) {
            System.out.println("Error: El tiquete no pertenece a este usuario");
            return false;
        }
        
        boolean transferido = tiquete.transferir(destino, this.getPassword());
        
        if (transferido) {
            tiquetes.remove(tiquete);
            destino.comprarTiquete(tiquete);
            System.out.println("Tiquete transferido exitosamente a " + destino.getNombre());
            return true;
        }
        
        return false;
    }
    
    

	public List<Tiquete> getTiquetes() {
        return new ArrayList<>(tiquetes);
    }
    
    public boolean removerTiquete(Tiquete tiquete) {
        return tiquetes.remove(tiquete);
    }
    
    @Override
    public String mostrarPerfil() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================\n");
        sb.append("PERFIL DE CLIENTE\n");
        sb.append("Login: ").append(getLogin()).append("\n");
        sb.append("Nombre: ").append(getNombre()).append("\n");
        sb.append("Saldo Virtual: $").append(String.format("%.2f", getSaldoVirtual())).append("\n");
        sb.append("Tiquetes: ").append(tiquetes.size()).append("\n");
        sb.append("=================================");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "Cliente: " + super.toString();
    }
    
    public boolean comprarEnMarketplace(double monto) {
        System.out.println("CLIENTE: Intentando comprar por $" + monto);
        System.out.println("   Saldo actual: $" + getSaldoVirtual());
        
        if (monto <= 0) {
            System.out.println("Error: Monto inválido");
            return false;
        }
        
        if (getSaldoVirtual() >= monto) {
            boolean resultado = deducirSaldo(monto);
            System.out.println("   Resultado deducción: " + resultado);
            System.out.println("   Nuevo saldo: $" + getSaldoVirtual());
            return resultado;
        }
        
        System.out.println("Saldo insuficiente");
        return false;
    }

    public void recibirPagoVenta(double monto) {
        System.out.println("CLIENTE: Recibiendo pago de $" + monto);
        System.out.println("   Saldo antes: $" + getSaldoVirtual());
        
        if (monto > 0) {
            recargarSaldo(monto);
            System.out.println("   Saldo después: $" + getSaldoVirtual());
        } else {
            System.out.println("Monto de pago inválido");
        }
    }
}