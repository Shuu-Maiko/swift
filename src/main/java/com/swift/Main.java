package com.swift;

import com.swift.core.*;
import com.swift.utils.FileManager;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter your name: ");
    String userName = scanner.nextLine().trim();
    System.out.print("1: receiver, 2: sender: ");
    int role = scanner.nextInt();
    scanner.nextLine();
    int Port = 8888;

    if (role == 1) {
      new FileReceiver(userName, Port).start();
    } else if (role == 2) {
      System.out.print("ip: ");
      String ip = scanner.nextLine().trim();
      System.out.print("path: ");
      String path = scanner.nextLine().trim();
      if (FileManager.isFileValid(path)) {
        new FileSender(userName, ip, Port, path).start();
      } else {
        System.err.println("invalid");
      }
    }
    
    scanner.close();
  }
}
