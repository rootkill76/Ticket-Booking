import java.io.*;
import java.net.*;

public class TrainTicketClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {

            String userInput;
            boolean authenticated = false;

            while (!authenticated) {
                System.out.print("Enter username: ");
                String username = stdIn.readLine();

                System.out.print("Enter password: ");
                String password = stdIn.readLine();

                out.println(username + ":" + password);
                String response = in.readLine();

                if ("Authenticated".equals(response)) {
                    authenticated = true;
                    System.out.println("✔ Authentication successful!");
                } else {
                    System.out.println("✘ Authentication failed. Try again.\n");
                }
            }

            System.out.println("\nYou can now use the following commands:");
            System.out.println(" - book            → View list of trains & book using `book <index>`");
            System.out.println(" - history         → View booking history");
            System.out.println(" - cancel <index>  → Cancel booking");
            System.out.println(" - exit            → Quit");
            System.out.println("\nEnter your commands below:\n");

            while (true) {
                System.out.print("> ");
                userInput = stdIn.readLine();

                if (userInput == null) break;

                out.println(userInput);

                StringBuilder serverResponse = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null && !line.trim().isEmpty()) {
                    serverResponse.append(line).append("\n");
                    if (!in.ready()) break;
                }

                System.out.println("\nSERVER:\n" + serverResponse);

                if (userInput.equals("exit")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Connection lost.");
            e.printStackTrace();
        }
    }
}
