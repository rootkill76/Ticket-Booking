import java.io.*;
import java.net.*;

public class TrainTicketClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            String userInput;
            boolean authenticated = false;

            while (!authenticated) {
                System.out.println("Enter username: ");
                String username = stdIn.readLine();
                System.out.println("Enter password: ");
                String password = stdIn.readLine();

                out.println(username + ":" + password);
                String response = in.readLine();
                if (response.equals("Authenticated")) {
                    authenticated = true;
                    System.out.println("Authentication successful");
                } else {
                    System.out.println("Authentication failed. Please try again.");
                }
            }

            System.out.println("Authenticated. You can now interact with the server.");
            System.out.println("Enter 'book' to book a ticket, 'history' to view booking history, 'cancel' to cancel a booking, 'check' to view available seats, or 'exit' to quit.");

            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("Server response: " + in.readLine());
                if (userInput.equals("exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
