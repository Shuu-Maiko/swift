package loggers;

import interfaces.ILogger;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileLogger implements ILogger, Closeable {
  private static final int MAX_LOGS = 500;
  private List<TransferLog> logs = new ArrayList<>();
  private String logFilePath;
  private boolean isCSVFormat;
  private FileWriter fileWriter;

  public FileLogger(String logFilePath, boolean isCSVFormat) throws IOException {
    this.logFilePath = logFilePath;
    this.isCSVFormat = isCSVFormat;
    initializeLogFile();
  }

  private void initializeLogFile() throws IOException {
    File file = new File(logFilePath);
    if (file.getParentFile() != null) {
      file.getParentFile().mkdirs();
    }

    boolean fileExists = file.exists();
    fileWriter = new FileWriter(file, true);

    if (!fileExists) {
      if (isCSVFormat) {
        fileWriter.write("Timestamp,Sender,Receiver,FileName,FileSize,Duration,TransferType,Success,ErrorMessage\n");
        fileWriter.flush();
      }
    }
  }

  @Override
  public void log(TransferLog transferLog) {
    logs.add(transferLog);
    if (logs.size() > MAX_LOGS) {
      logs.subList(0, logs.size() - MAX_LOGS).clear();
    }
    writeToFile(transferLog);
  }

  private void writeToFile(TransferLog transferLog) {
    try {
      if (isCSVFormat) {
        fileWriter.write(transferLog.toCSV() + "\n");
      } else {
        fileWriter.write(transferLog.toString() + "\n");
      }
      fileWriter.flush();
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
      fileWriter.close();
      try (FileWriter fw = new FileWriter(logFilePath, false)) {
        if (isCSVFormat) {
          fw.write("Timestamp,Sender,Receiver,FileName,FileSize,Duration,TransferType,Success,ErrorMessage\n");
        }
      }
      fileWriter = new FileWriter(logFilePath, true);
      System.out.println("[LOG] File logs cleared.");
    } catch (IOException e) {
      System.err.println("Error clearing log file: " + e.getMessage());
    }
  }

  @Override
  public void close() throws IOException {
    if (fileWriter != null) {
      fileWriter.close();
    }
  }
}
