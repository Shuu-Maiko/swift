package com.swift.core;

import com.swift.utils.FileManager;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

public abstract class Peer {
  String name;
  String ip;
  int port;

  public Peer(String name, String ip, int port) {
    this.name = name;
    this.ip = ip;
    this.port = port;
  }

  public abstract void start();
}

class FileSender extends Peer {
  Socket s;
  String filePath;

  public FileSender(String userName, String targetIp, int port, String filePath) {
    super(userName, targetIp, port);
    this.filePath = filePath;
  }

  @Override
  public void start() {
    try {
      String pin = String.format("%04d", new Random().nextInt(10000));
      System.out.println("Pairing PIN: " + pin);
      System.out.println("Share this code with the receiver.");

      s = new Socket(ip, port);
      DataOutputStream dos = new DataOutputStream(s.getOutputStream());
      DataInputStream dis = new DataInputStream(s.getInputStream());

      String fileName = FileManager.getFileName(filePath);
      long fileSize = FileManager.getFileSize(filePath);

      // Send Handshake Metadata
      dos.writeUTF(name);
      dos.writeUTF(fileName);
      dos.writeLong(fileSize);

      // Wait for PIN verification from receiver
      String receivedPin = dis.readUTF();
      if (!pin.equals(receivedPin)) {
        System.err.println("Verification failed! PIN mismatch.");
        dos.writeBoolean(false); // Tell receiver to abort
        return;
      }
      dos.writeBoolean(true); // Verification success

      System.out.println("Handshake successful. Starting transfer...");
      long start = System.nanoTime();

      try (FileInputStream fis = FileManager.getInputStream(filePath)) {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
          dos.write(buffer, 0, bytesRead);
        }
      }
      long end = System.nanoTime();
      long duration = (end - start);
      System.out.println("file send in : " + duration / 1000000 + "ms");
      System.out.println("done");
    } catch (UnknownHostException u) {
      System.err.println(u.getMessage());
    } catch (IOException i) {
      System.err.println(i.getMessage());
    }
  }
}

class FileReceiver extends Peer {
  ServerSocket s;

  public FileReceiver(String userName, int port) {
    super(userName, "localhost", port);
  }

  @Override
  public void start() {
    try (Scanner scanner = new Scanner(System.in)) {
      s = new ServerSocket(port);
      System.out.println("Waitng for connection on port " + port + "...");
      Socket clientSocket = s.accept();
      DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

      String senderName = dis.readUTF();
      String fileName = dis.readUTF();
      long fileSize = dis.readLong();

      System.out.println("\n--- Incoming Transfer ---");
      System.out.println("From: " + senderName);
      System.out.println("File: " + fileName);
      System.out.println("Size: " + (fileSize / 1024) + " KB");
      System.out.print("\nAccept this transfer? (y/n): ");
      
      String choice = scanner.nextLine().trim().toLowerCase();
      if (!choice.equals("y")) {
        System.out.println("Transfer rejected.");
        clientSocket.close();
        s.close();
        return;
      }

      System.out.print("Enter the 4-digit PIN from " + senderName + ": ");
      String enteredPin = scanner.nextLine().trim();
      dos.writeUTF(enteredPin);

      boolean verified = dis.readBoolean();
      if (!verified) {
        System.err.println("Verification failed! PIN is incorrect. Aborting.");
        clientSocket.close();
        s.close();
        return;
      }

      System.out.println("Verification success. Starting download...");

      try (FileOutputStream fos = FileManager.getOutputStream(fileName)) {
        byte[] buffer = new byte[4096];
        long totalRead = 0;
        int bytesRead;
        while (totalRead < fileSize
            && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead))) != -1) {
          fos.write(buffer, 0, bytesRead);
          totalRead += bytesRead;
        }
      }
      System.out.println("done");
    } catch (IOException i) {
      System.err.println(i.getMessage());
    }
  }
}
