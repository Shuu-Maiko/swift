package core;

import interfaces.IProgressTrackable;
import java.io.*;
import java.net.*;

public class FolderReceiver extends Peer implements IProgressTrackable {
  public static final int BUFFER_SIZE = 65536;

  public FolderReceiver(String userName, int port) {
    super(userName, "localhost", port);
  }

  @Override
  public void start() {
    try (ServerSocket server = new ServerSocket(port)) {
      System.out.println("Folder mode: waiting for connection on port " + port + "...");
      Socket clientSocket = server.accept();
      DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

      String senderName = dis.readUTF();
      String folderName = dis.readUTF();
      int fileCount = dis.readInt();

      File outDir = new File(folderName);
      outDir.mkdirs();

      System.out.println("Receiving folder '" + folderName + "' (" + fileCount + " files) from " + senderName);

      for (int i = 0; i < fileCount; i++) {
        String fileName = dis.readUTF();
        long fileSize = dis.readLong();

        System.out.println("\n[" + (i + 1) + "/" + fileCount + "] " + fileName);
        File outFile = new File(outDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
          byte[] buffer = new byte[BUFFER_SIZE];
          long totalRead = 0;
          int bytesRead;
          while (totalRead < fileSize
              && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead))) != -1) {
            fos.write(buffer, 0, bytesRead);
            totalRead += bytesRead;
            updateProgress(totalRead, fileSize);
          }
        }
      }
      System.out.println("\nDone.");
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  @Override
  public void updateProgress(long current, long total) {
    int percent = (int) ((current * 100) / total);
    System.out.print("\rReceiving: [" + "#".repeat(percent / 10) + " ".repeat(10 - percent / 10) + "] " + percent + "%");
  }
}
