# 🚗 College Carpool Coordinator

A JavaFX desktop application that helps college students share rides efficiently, split fares, and manage trip history. Built using Java, JavaFX, and MySQL with a focus on simplicity and database-backed interaction.

---

## 📁 Folder Structure

CollegeCarpoolCoordinator/
├── src/
│ ├── client/
│ │ ├── CarpoolClient.java
│ │ └── controllers/
│ │ ├── LoginController.java
│ │ └── DashboardController.java
│ ├── database/
│ │ ├── DBConnection.java
│ │ ├── UserAuth.java
│ │ ├── TripManager.java
│ │ └── BookingManager.java
├── resources/
│ └── client/ui/
│ ├── Login.fxml
│ └── Dashboard.fxml
├── lib/
│ ├── javafx-controls.jar
│ ├── javafx-fxml.jar
│ └── mysql-connector-java.jar
├── .classpath
├── .vscode/
│ └── launch.json
└── README.md

yaml
Copy code

---

## ⚙️ Technologies Used

- **JavaFX** – UI framework for Java
- **MySQL** – Relational database
- **JDBC** – Java database connectivity
- **VS Code** – IDE with Java Extension Pack

---

## 🚀 How to Run the Project

1. **Install Requirements**:
   - Java 17+ JDK
   - MySQL Server
   - VS Code with Java Extension Pack

2. **Set Up Project**:
   - Place JavaFX JARs and MySQL connector in `lib/`
   - Use the `.classpath` file provided
   - Use the `launch.json` file in `.vscode/` to run the app

3. **Run from VS Code**:
   - Use the “Run CarpoolClient” configuration in VS Code
   - Ensure JavaFX VM arguments are configured correctly

---


## 🧠 Database Schema

Run the provided SQL script `college_carpool_db.sql` to create:
- `users`
- `trips`
- `bookings`
- `ride_history`

You can execute it using MySQL CLI or GUI tools like MySQL Workbench.

---

## ✨ Features

- User registration & login with password hashing
- Create and search trips
- Book rides and view bookings
- Fare splitting and trip history tracking

---
Run (Windows)
cd scripts
build-run-windows.bat

Run (Linux)
cd scripts
chmod +x build-run-linux.sh
./build-run-linux.sh

Run (macOS)
cd scripts
chmod +x build-run-mac.sh
./build-run-mac.sh

## 🙌 Credits

Developed by **[Rohith ]** as part of a college semester project.

---

## 📬 Contact

For queries or contributions, feel free to contact via [rohithnrdvy@gmail.com].
