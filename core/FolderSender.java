package core;

import utils.FileManager;
import interfaces.*;
import java.io.*;
import java.net.*;
import java.util.Random;

public class FolderSender extends Peer implements IProgressTrackable, IHashable {
  public static final int BUFFER_SIZE = 65536;
  String folderPath;

  public FolderSender(String userName, String targetIp, int port, String folderPath) {
    super(userName, targetIp, port);
    this.folderPath = folderPath;
  }

  @Override
  public void start() {
    File folder = new File(folderPath);
    File[] files = folder.listFiles(File::isFile);
    if (files == null || files.length == 0) {
      System.err.println("Folder is empty or invalid.");
      return;
    }

    try {
      String pin = String.format("%04d", new Random().nextInt(10000));
      System.out.println("Pairing PIN: " + pin);
      System.out.println("Share this code with the receiver.");

      Socket s = new Socket(ip, port);
      DataOutputStream dos = new DataOutputStream(s.getOutputStream());
      DataInputStream dis = new DataInputStream(s.getInputStream());

      dos.writeUTF(name);
      dos.writeUTF(folder.getName());
      dos.writeInt(files.length);

      String receivedPin = dis.readUTF();
      if (!pin.equals(receivedPin)) {
        System.err.println("Verification failed! PIN mismatch.");
        dos.writeBoolean(false);
        s.close();
        return;
      }
      dos.writeBoolean(true);

      System.out.println("Handshake successful. Starting transfer...");

      long start = System.nanoTime();

      for (int i = 0; i < files.length; i++) {
        File f = files[i];
        long size = f.length();
        dos.writeUTF(f.getName());
        dos.writeLong(size);

        System.out.println("\n[" + (i + 1) + "/" + files.length + "] " + f.getName());
        try (FileInputStream fis = new FileInputStream(f)) {
          byte[] buffer = new byte[BUFFER_SIZE];
          int bytesRead;
          long totalSent = 0;
          while ((bytesRead = fis.read(buffer)) != -1) {
            dos.write(buffer, 0, bytesRead);
            totalSent += bytesRead;
            updateProgress(totalSent, size);
          }
        }
      }

      long end = System.nanoTime();
      long duration = (end - start);
      System.out.println();
      System.out.println("Folder sent in: " + duration / 1000000 + "ms");
      System.out.println("Done.");
      s.close();
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  @Override
  public void updateProgress(long current, long total) {
    int percent = (int) ((current * 100) / total);
    System.out.print("\rSending: [" + "#".repeat(percent / 10) + " ".repeat(10 - percent / 10) + "] " + percent + "%");
  }

  @Override
  public String generateHash(String filePath) {
    try {
      System.out.println("Calculating SHA-256 hash...");
      return FileManager.calculateSHA256(filePath);
    } catch (Exception e) {
      return "ERROR";
    }
  }

  @Override
  public boolean verifyHash(String filePath, String expectedHash) {
    return false;
  }
}
