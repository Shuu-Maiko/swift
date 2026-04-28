package core;

import interfaces.IProgressTrackable;
import java.io.*;
import java.net.*;

public class FolderSender extends Peer implements IProgressTrackable {
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

    try (Socket s = new Socket(ip, port)) {
      DataOutputStream dos = new DataOutputStream(s.getOutputStream());

      dos.writeUTF(name);
      dos.writeUTF(folder.getName());
      dos.writeInt(files.length);

      System.out.println("Sending folder '" + folder.getName() + "' (" + files.length + " files) -> " + ip);

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
      System.out.println("\nDone.");
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  @Override
  public void updateProgress(long current, long total) {
    int percent = (int) ((current * 100) / total);
    System.out.print("\rSending: [" + "#".repeat(percent / 10) + " ".repeat(10 - percent / 10) + "] " + percent + "%");
  }
}
