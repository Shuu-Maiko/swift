import java.io.*;
import java.net.*;

public abstract class Peer {
  String ip;
  int port;

  public Peer(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  public abstract void start();
}

class FileSender extends Peer {
  Socket s;
  String filePath;

  public FileSender(String targetIp, int port, String filePath) {
    super(targetIp, port);
    this.filePath = filePath;
  }

  @Override
  public void start() {
    try {
      s = new Socket(ip, port);
      DataOutputStream dos = new DataOutputStream(s.getOutputStream());
      
      String fileName = FileManager.getFileName(filePath);
      long fileSize = FileManager.getFileSize(filePath);
      
      dos.writeUTF(fileName);
      dos.writeLong(fileSize);
      
      System.out.println(fileName + " " + fileSize);
    } catch (UnknownHostException u) {
      System.err.println(u.getMessage());
    } catch (IOException i) {
      System.err.println(i.getMessage());
    }
  }
}

class FileReceiver extends Peer {
  ServerSocket s;

  public FileReceiver(int port) {
    super("localhost", port);
  }

  @Override
  public void start() {
    try {
      s = new ServerSocket(port);
      Socket clientSocket = s.accept();
      DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
      
      String fileName = dis.readUTF();
      long fileSize = dis.readLong();
      
      System.out.println(fileName + " " + fileSize);
    } catch (IOException i) {
      System.err.println(i.getMessage());
    }
  }
}
