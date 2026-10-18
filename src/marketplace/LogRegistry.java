package marketplace;

import usuarios.Usuario;
import java.util.ArrayList;
import java.util.List;

public class LogRegistry {
    private List<LogEntry> entries;

    public LogRegistry() {
        this.entries = new ArrayList<>();
    }

    public void log(Usuario usuario, String accion, String detalles) {
        String id = "LOG-" + System.currentTimeMillis();
        LogEntry entry = new LogEntry(id, usuario, accion, detalles);
        entries.add(entry);
        System.out.println("LOG: " + entry.toString());
    }

    public List<LogEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public String getLogAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LOG DEL MARKETPLACE ===\n");
        for (LogEntry entry : entries) {
            sb.append(entry.toString()).append("\n");
        }
        sb.append("===========================\n");
        return sb.toString();
    }
    
    public void clear() {
        entries.clear();
    }
}