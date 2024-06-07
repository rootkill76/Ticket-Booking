import java.io.*;
import java.net.*;
import java.util.*;

public class TrainTicketServer {
    private static final int PORT = 12345;
    private static final int TOTAL_SEATS = 50;
    private static int availableSeats = TOTAL_SEATS;
    private static Map<String, String> users = new HashMap<>();
    private static Map<String, List<Booking>> userBookings = new HashMap<>();
    private static List<Train> trains = new ArrayList<>();

    static {
        // Initialize sample train data
        trains.add(new Train("Express", "8:00 AM", 20));
        trains.add(new Train("Local", "10:00 AM", 30));
        trains.add(new Train("Express", "2:00 PM", 10));

        // Add some sample users
        users.put("user1", "password1");
        users.put("user2", "password2");
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected.");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized boolean bookTicket(int trainIndex, String username) {
        if (availableSeats > 0 && trainIndex >= 0 && trainIndex < trains.size()) {
            Train train = trains.get(trainIndex);
            if (train.getAvailableSeats() > 0) {
                train.bookSeat();
                availableSeats--;

                Booking booking = new Booking(train.getName(), train.getDepartureTime());
                userBookings.computeIfAbsent(username, k -> new ArrayList<>()).add(booking);

                return true;
            }
        }
        return false;
    }

    private static synchronized List<Booking> getBookingHistory(String username) {
        return userBookings.getOrDefault(username, new ArrayList<>());
    }

    private static synchronized boolean cancelBooking(int index, String username) {
        List<Booking> bookings = userBookings.get(username);
        if (bookings != null && index >= 0 && index < bookings.size()) {
            Booking booking = bookings.remove(index);
            for (Train train : trains) {
                if (train.getName().equals(booking.getTrainName()) && train.getDepartureTime().equals(booking.getDepartureTime())) {
                    train.cancelSeat();
                    availableSeats++;
                    return true;
                }
            }
        }
        return false;
    }

    private static synchronized int getAvailableSeats() {
        return availableSeats;
    }

    private static synchronized List<Train> getAvailableTrains() {
        return trains;
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String inputLine;
                boolean authenticated = false;
                String username = null;

                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: " + inputLine);
                    if (!authenticated) {
                        String[] credentials = inputLine.split(":");
                        if (credentials.length == 2 && users.containsKey(credentials[0]) && users.get(credentials[0]).equals(credentials[1])) {
                            authenticated = true;
                            username = credentials[0];
                            out.println("Authenticated");
                        } else {
                            out.println("Authentication failed");
                        }
                    } else {
                        if (inputLine.equals("book")) {
                            List<Train> availableTrains = getAvailableTrains();
                            StringBuilder response = new StringBuilder("Available Trains:\n");
                            for (int i = 0; i < availableTrains.size(); i++) {
                                response.append(i).append(". ").append(availableTrains.get(i)).append("\n");
                            }
                            out.println(response.toString());
                        } else if (inputLine.startsWith("book ")) {
                            int trainIndex = Integer.parseInt(inputLine.substring(5));
                            boolean success = bookTicket(trainIndex, username);
                            out.println(success ? "Ticket booked successfully." : "Failed to book ticket.");
                        } else if (inputLine.equals("check")) {
                            out.println("Available seats: " + getAvailableSeats());
                        } else if (inputLine.equals("history")) {
                            List<Booking> bookingHistory = getBookingHistory(username);
                            StringBuilder response = new StringBuilder("Booking History:\n");
                            for (int i = 0; i < bookingHistory.size(); i++) {
                                response.append(i).append(". ").append(bookingHistory.get(i)).append("\n");
                            }
                            out.println(response.toString());
                        } else if (inputLine.startsWith("cancel ")) {
                            int index = Integer.parseInt(inputLine.substring(7));
                            boolean success = cancelBooking(index, username);
                            out.println(success ? "Booking canceled successfully." : "Failed to cancel booking.");
                        } else if (inputLine.equals("exit")) {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Train {
        private String name;
        private String departureTime;
        private int availableSeats;

        public Train(String name, String departureTime, int availableSeats) {
            this.name = name;
            this.departureTime = departureTime;
            this.availableSeats = availableSeats;
        }

        public int getAvailableSeats() {
            return availableSeats;
        }

        public void bookSeat() {
            availableSeats--;
        }

        public void cancelSeat() {
            availableSeats++;
        }

        public String getName() {
            return name;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        @Override
        public String toString() {
            return name + " - Departure Time: " + departureTime + ", Available Seats: " + availableSeats;
        }
    }

    private static class Booking {
        private String trainName;
        private String departureTime;

        public Booking(String trainName, String departureTime) {
            this.trainName = trainName;
            this.departureTime = departureTime;
        }

        public String getTrainName() {
            return trainName;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        @Override
        public String toString() {
            return "Train: " + trainName + ", Departure Time: " + departureTime;
        }
    }
}
