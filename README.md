# 📚 ATM Banking Management System

[![Java](https://img.shields.io/badge/Java-17+-blue)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8+-green)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
A simple **Java-based ATM Banking Management System** that allows users to add, view, search, update, and delete contacts. This project uses **JDBC** to connect with a **MySQL database** and demonstrates a full CRUD application using **Java**, **DAO pattern**, and **service layer architecture**.
---

### 📌 Project Overview

The ATM Banking Management System is a Java-based application that simulates real-world ATM operations.
It uses JDBC for database connectivity and secure password hashing to protect sensitive user data.

This project demonstrates:
- 🔹 Core Java concepts
- 🔹 JDBC integration
- 🔹 Database design
- 🔹 Secure authentication system
---

🚀 Features

- 🔐 Secure User Authentication (Hashed Password & PIN)
- 💰 Balance Inquiry
- 💸 Cash Withdrawal
- 💵 Deposit Money
- 🧾 Transaction History
- 🗄️ MySQL Database Integration (JDBC)
- 🚪 Safe Exit System
---

### 🛠️ Technologies Used

- Java (Core Java)
- JDBC (Java Database Connectivity)
- MySQL
- OOP (Object-Oriented Programming)
- Password Hashing (SHA-256 / BCrypt)

---

### 🔐 Security Implementation
- Passwords are not stored in plain text
- Passwords and PINs are hashed before storing
- Protects against unauthorized access
- Improves real-world security practices

---

### 🗄️ Database Schema (MySQL)
```sql
🔹 Users Table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
🔹 Accounts Table
CREATE TABLE accounts (
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    account_number BIGINT UNIQUE NOT NULL,
    user_id INT NOT NULL,
    pin VARCHAR(255) NOT NULL,
    balance DECIMAL(10,2) NOT NULL CHECK(balance >= 0),
    FOREIGN KEY(user_id) REFERENCES users(user_id)
);
🔹 Transactions Table
CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    account_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK(amount > 0),
    transaction_type ENUM('DEPOSIT','WITHDRAW','TRANSFER') NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(account_id) REFERENCES accounts(account_id)
);

```

----

### 🔗 Database Relationships
- One User → Multiple Accounts
- One Account → Multiple Transactions

---

### 📂 Project Structure
ATM-Banking-Management-Systems/
│
├── src/
│ ├── Banking_ATM_Simulator/
│ │ ├── DAO/
| | | ├── AccountDAO.java
| | | ├── TransactionDAO.java
│ │ │ └── UserDAO.java
│ │ ├── model/
| | | ├── Accounts.java
│ │ │ └── Users.java
│ │ ├── util/
│ │ │ └── DBConnection.java
│ │ └── Main.java
│
├── README.md
└── pom.xml (if using Maven)


---

### ⚙️ How to Run the Project
Clone the repository:
git clone https://github.com/your-username/ATM-Banking-Management-System.git
Setup Database:
Create database:
CREATE DATABASE atm;
Use database:
USE atm;
Run table creation queries
Configure database credentials in DBConnection.java
Run the project:
Open in IntelliJ / Eclipse
Run Main.java

---

### 🧪 Sample Workflow
1. Enter Account Number & PIN
2. System verifies hashed credentials
3. Choose operation:
   - Check Balance
   - Deposit
   - Withdraw
4. Balance updates in real-time

----

### 🔮 Future Enhancements
- GUI Interface (Java Swing / JavaFX)
- OTP-based authentication
- Mini statement generation
- Admin dashboard
- REST API integration

---

## License 📝
This project is open-source and available under the MIT License.

Author

Mayank Kapoor
GitHub: https://github.com/kapoormayank9696

---

If you want, I can also **make a shorter, more attractive GitHub-ready version** that looks modern with badges for Java, MySQL, and license.  

Do you want me to do that version too?
