# 🚆 Train Ticket Booking System  
### A Java Socket-Based Client–Server Application

![Java](https://img.shields.io/badge/Java-17+-red?logo=java)
![Sockets](https://img.shields.io/badge/Network-Sockets-blue)
![Multi-threaded](https://img.shields.io/badge/Threads-Multi--client-green)
![Status](https://img.shields.io/badge/Status-Working-brightgreen)

A console-based **Train Ticket Booking System** built using **Java TCP sockets**.  
This project demonstrates:

- Client–server communication  
- Multi-threading  
- Authentication  
- Ticket booking, cancellation, and history  
- Shared resource synchronization  

---

## 📂 Project Structure
```bash
/TrainTicketBookingSystem
│
├── TrainTicketServer.java # Server-side logic
├── TrainTicketClient.java # Client-side interface
└── README.md # Documentation
```
---

## ✨ Features

### 🔐 User Authentication
Ensures only registered users can access the system.  
Default credentials:
user1 : password1
user2 : password2

### 🚆 Train Booking System
Users can:
- View available trains  
- Book a seat  
- Cancel a booking  
- View booking history  

All booking operations are thread-safe.

---

## 💬 Commands Supported

| Command | Description |
|--------|-------------|
| `book` | Show list of trains |
| `book <index>` | Book train by index |
| `history` | View user booking history |
| `cancel <index>` | Cancel booking |
| `exit` | Quit client |

---

## 🖥 How the System Works

### **Server**
- Waits for clients on port `12345`
- Handles each client in a separate thread  
- Maintains: user accounts, train list, booking histories  
- Ensures synchronized seat updates

### **Client**
- Connects using socket to the server  
- Prompts for login  
- Sends commands and displays server responses  
- Handles multi-line server output  

---

## ▶️ Running the Application

### **1️⃣ Start the Server**
```bash
javac TrainTicketServer.java
java TrainTicketServer

Enter username: user1
Enter password: password1
✔ Authentication successful!

> book
Available Trains:
0. Express | Departure: 8:00 AM | Seats: 20
1. Local   | Departure: 10:00 AM | Seats: 30
2. Express | Departure: 2:00 PM  | Seats: 10

> book 1
Ticket booked successfully.

> history
Booking History:
0. Train: Local, Time: 10:00 AM

> cancel 0
Booking canceled successfully.
```
---
## 🧠 Concepts Demonstrated

TCP socket programming

Input/Output streams

Multi-threading & concurrency

Resource synchronization (synchronized)

Command parsing

Console-based interactive system
