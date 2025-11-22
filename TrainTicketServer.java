import java.io.*;
import java.net.*;
import java.util.*;

public class TrainTicketServer {

    private static final int PORT = 12345;

    private static Map<String, String> users = new HashMap<>();
    private static Map<String, List<Booking>> userBookings = new HashMap<>();
    private static List<Train> trains = new ArrayList<>();

    static {
        trains.add(new Train("Express", "8:00 AM", 20));
        trains.add(new Train("Local", "10:00 AM", 30));
        trains.add(new Train("Express", "2:00 PM", 10));

        users.put("user1", "password1");
        users.put("user2", "password2");
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected.");

                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized boolean bookTicket(int trainIndex, String username) {
        if (trainIndex < 0 || trainIndex >= trains.size()) return false;

        Train train = trains.get(trainIndex);
        if (train.getAvailableSeats() <= 0) return false;

        train.bookSeat();

        Booking booking = new Booking(train.getName(), train.getDepartureTime());
        userBookings.computeIfAbsent(username, k -> new ArrayList<>()).add(booking);

        return true;
    }

    private static synchronized boolean cancelBooking(int index, String username) {
        List<Booking> bookings = userBookings.get(username);
        if (bookings == null || index < 0 || index >= bookings.size()) return false;

        Booking booking = bookings.remove(index);

        for (Train train : trains) {
            if (train.getName().equals(booking.getTrainName()) &&
                    train.getDepartureTime().equals(booking.getDepartureTime())) {

                train.cancelSeat();
                return true;
            }
        }
        return false;
    }

    private static synchronized List<Train> getTrains() {
        return trains;
    }

    private static synchronized List<Booking> getBookings(String username) {
        return userBookings.getOrDefault(username, new ArrayList<>());
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                String input;
                boolean loggedIn = false;
                String username = null;

                while ((input = in.readLine()) != null) {

                    if (!loggedIn) {
                        String[] parts = input.split(":");
                        if (parts.length == 2 && users.containsKey(parts[0]) &&
                                users.get(parts[0]).equals(parts[1])) {

                            loggedIn = true;
                            username = parts[0];
                            out.println("Authenticated");
                        } else {
                            out.println("Authentication failed");
                        }
                        continue;
                    }

                    if (input.equals("book")) {
                        StringBuilder sb = new StringBuilder("Available Trains:\n");
                        List<Train> list = getTrains();
                        for (int i = 0; i < list.size(); i++) {
                            sb.append(i).append(". ").append(list.get(i)).append("\n");
                        }
                        out.println(sb.toString());
                    }

                    else if (input.startsWith("book ")) {
                        try {
                            int index = Integer.parseInt(input.substring(5).trim());
                            boolean ok = bookTicket(index, username);
                            out.println(ok ? "Ticket booked successfully." : "Failed to book ticket.");
                        } catch (Exception e) {
                            out.println("Invalid train index.");
                        }
                    }

                    else if (input.equals("history")) {
                        List<Booking> bookings = getBookings(username);
                        StringBuilder sb = new StringBuilder("Booking History:\n");
                        for (int i = 0; i < bookings.size(); i++) {
                            sb.append(i).append(". ").append(bookings.get(i)).append("\n");
                        }
                        out.println(sb.toString());
                    }

                    else if (input.startsWith("cancel ")) {
                        try {
                            int index = Integer.parseInt(input.substring(7).trim());
                            boolean ok = cancelBooking(index, username);
                            out.println(ok ? "Booking canceled successfully." : "Failed to cancel booking.");
                        } catch (Exception e) {
                            out.println("Invalid booking index.");
                        }
                    }

                    else if (input.equals("exit")) {
                        out.println("Goodbye!");
                        break;
                    }

                    else {
                        out.println("Invalid command.");
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

        public Train(String name, String time, int seats) {
            this.name = name;
            this.departureTime = time;
            this.availableSeats = seats;
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
            return name + " | Departure: " + departureTime + " | Seats: " + availableSeats;
        }
    }

    private static class Booking {
        private String trainName;
        private String departureTime;

        public Booking(String trainName, String time) {
            this.trainName = trainName;
            this.departureTime = time;
        }

        public String getTrainName() {
            return trainName;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        @Override
        public String toString() {
            return "Train: " + trainName + ", Time: " + departureTime;
        }
    }
}
