package Banking_ATM_Simulator.src;

import Banking_ATM_Simulator.src.DAO.AccountDAO;
import Banking_ATM_Simulator.src.DAO.UserDAO;
import Banking_ATM_Simulator.src.model.Accounts;
import Banking_ATM_Simulator.src.model.Users;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();
        AccountDAO accountDAO = new AccountDAO();

        while (true) {
            System.out.println("\n===== ATM SYSTEM =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {

                // 🔹 REGISTER
                case 1:
                    System.out.print("Enter username: ");
                    String regUser = sc.nextLine().toLowerCase();

                    System.out.print("Enter password: ");
                    String regPass = sc.nextLine();

                    try {
                        Users newUser = new Users(0, regUser, regPass);

                        if (userDAO.createUser(newUser)) {
                            System.out.println("✅ User registered successfully!");

                            // Create default account
                            BigInteger accNumber = new BigInteger(String.valueOf(System.currentTimeMillis()));
                            //Accounts acc = new Accounts(0, accNumber, newUser.getUserId(), "0000", new BigDecimal("0"));
                            Accounts acc = new Accounts(0, accNumber, newUser.getUserId(), "", new BigDecimal("0"));
                            acc.updatePin("0000"); // hashes PIN
                            accountDAO.createAccount(acc);


                            System.out.println("🏦 Account created! Account Number: " + accNumber);
                        } else {
                            System.out.println("❌ Registration failed.");
                        }
                    } catch (Exception e) {
                        System.out.println("❌ " + e.getMessage());
                    }
                    break;

                // 🔹 LOGIN
                case 2:
                    System.out.print("Username: ");
                    String username = sc.nextLine().toLowerCase();

                    System.out.print("Password: ");
                    String password = sc.nextLine();

                    Users user = userDAO.loginUser(username, password);

                    if (user == null) {
                        System.out.println("❌ Invalid username or password.");
                        break;
                    }

                    System.out.println("✅ Login successful! Welcome " + user.getUsername());
                    System.out.println("User id is : " + user.getUserId());
                    Accounts account = accountDAO.getAccountByUserId(user.getUserId());


                    if (account == null) {
                        System.out.println("⚠️ No account found.");
                        break;
                    }

                    // ATM MENU
                    boolean running = true;
                    while (running) {
                        System.out.println("\n--- ATM MENU ---");
                        System.out.println("1. Balance");
                        System.out.println("2. Deposit");
                        System.out.println("3. Withdraw");
                        System.out.println("4. Exit");
                        System.out.print("Choose: ");

                        int opt = sc.nextInt();

                        switch (opt) {
                            case 1:
                                System.out.println("Balance: " + accountDAO.getAccountByUserId(user.getUserId()).getBalance());
                                break;

                            case 2:
                                System.out.print("Amount: ");
                                BigDecimal dep = sc.nextBigDecimal();
                                accountDAO.deposit(account.getAccountNumber(), dep);
                                break;

                            case 3:
                                System.out.print("Amount: ");
                                BigDecimal with = sc.nextBigDecimal();
                                System.out.print("PIN: ");
                                String pin = sc.next();
                                accountDAO.withdraw(account.getAccountNumber(), with, pin);
                                break;

                            case 4:
                                running = false;
                                break;
                        }
                    }
                    break;

                case 3:
                    System.out.println("Goodbye 👋");
                    System.exit(0);
            }
        }
    }

}
