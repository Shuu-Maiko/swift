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
};

class FileSender extends Peer {
  Socket s;

  public FileSender(String targetIp, int port) {
    super(targetIp, port);
  }

  @Override
  public void start() {
    try {
      s = new Socket(ip, port);
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
    } catch (IOException i) {
      System.err.println(i.getMessage());
    }
  }
}
