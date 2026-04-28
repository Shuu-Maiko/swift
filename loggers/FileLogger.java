package loggers;

import interfaces.ILogger;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileLogger implements ILogger {
    private List<TransferLog> logs = new ArrayList<>();
    private String logFilePath;
    private boolean isCSVFormat;

    public FileLogger(String logFilePath, boolean isCSVFormat) {
        this.logFilePath = logFilePath;
        this.isCSVFormat = isCSVFormat;
        initializeLogFile();
    }

    private void initializeLogFile() {
        try {
            File file = new File(logFilePath);
            file.getParentFile().mkdirs();

            if (!file.exists()) {
                file.createNewFile();
                if (isCSVFormat) {
                    try (FileWriter fw = new FileWriter(logFilePath, true)) {
                        fw.write("Timestamp,Sender,Receiver,FileName,FileSize,Duration,TransferType,Success,ErrorMessage\n");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing log file: " + e.getMessage());
        }
    }

    @Override
    public void log(TransferLog transferLog) {
        logs.add(transferLog);
        writeToFile(transferLog);
    }

    private void writeToFile(TransferLog transferLog) {
        try (FileWriter fw = new FileWriter(logFilePath, true)) {
            if (isCSVFormat) {
                fw.write(transferLog.toCSV() + "\n");
            } else {
                fw.write(transferLog.toString() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    @Override
    public List<TransferLog> getLogHistory() {
        return new ArrayList<>(logs);
    }

    @Override
    public void clearLogs() {
        logs.clear();
        try {
            try (FileWriter fw = new FileWriter(logFilePath, false)) {
                if (isCSVFormat) {
                    fw.write("Timestamp,Sender,Receiver,FileName,FileSize,Duration,TransferType,Success,ErrorMessage\n");
                }
            }
            System.out.println("[LOG] File logs cleared.");
        } catch (IOException e) {
            System.err.println("Error clearing log file: " + e.getMessage());
        }
    }
}
