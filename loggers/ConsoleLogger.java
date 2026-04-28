package loggers;

import interfaces.ILogger;
import java.util.ArrayList;
import java.util.List;

public class ConsoleLogger implements ILogger {
    private List<TransferLog> logs = new ArrayList<>();

    @Override
    public void log(TransferLog transferLog) {
        logs.add(transferLog);
        System.out.println("[LOG] " + transferLog.toString());
    }

    @Override
    public List<TransferLog> getLogHistory() {
        return new ArrayList<>(logs);
    }

    @Override
    public void clearLogs() {
        logs.clear();
        System.out.println("[LOG] Console logs cleared.");
    }
}
