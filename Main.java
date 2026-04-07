import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    System.out.println("swift live!!!");
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter role (receiver/sender): ");
    String role = scanner.nextLine().trim().toLowerCase();
    int Port = 8888;
    if (role.equals("receiver")) {
      FileReceiver receiver = new FileReceiver(Port);
      receiver.start();
    } else if (role.equals("sender")) {
      FileSender sender = new FileSender("localhost", Port);
      sender.start();
    } else {
      System.out.println("Unknown role: " + role);
    }

    scanner.close();
  }
}
