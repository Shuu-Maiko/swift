import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("role: ");
    String role = scanner.nextLine().trim().toLowerCase();
    int Port = 8888;

    if (role.equals("receiver")) {
      new FileReceiver(Port).start();
    } else if (role.equals("sender")) {
      System.out.print("path: ");
      String path = scanner.nextLine().trim();
      if (FileManager.isFileValid(path)) {
        new FileSender("localhost", Port, path).start();
      } else {
        System.err.println("invalid");
      }
    }
    
    scanner.close();
  }
}
