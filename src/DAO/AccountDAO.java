package Banking_ATM_Simulator.src.DAO;

import Banking_ATM_Simulator.src.model.Accounts;
import Banking_ATM_Simulator.src.model.Transaction;
import Banking_ATM_Simulator.src.util.DBConnection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDateTime;

public class AccountDAO {

    private final TransactionDAO transactionDAO = new TransactionDAO();

    /**
     * Retrieves an account associated with a specific user.
     */
    public Accounts getAccountByUserId(int userid) {
        String query = "SELECT * FROM accounts WHERE user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Accounts(
                            rs.getInt("account_id"),
                            new BigInteger(rs.getString("account_number")),
                            rs.getInt("user_id"),
                            rs.getString("pin"),
                            rs.getBigDecimal("balance")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates account balance for a deposit and logs the transaction.
     */
    public boolean deposit(BigInteger accountNumber, BigDecimal amount) {
        String updateQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String selectQuery = "SELECT account_id FROM accounts WHERE account_number = ?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) return false;

            // 1. Update Balance
            try (PreparedStatement psUpdate = connection.prepareStatement(updateQuery)) {
                psUpdate.setBigDecimal(1, amount);
                psUpdate.setString(2, accountNumber.toString());
                if (psUpdate.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // 2. Get account_id for logging
            int accountId = -1;
            try (PreparedStatement psSelect = connection.prepareStatement(selectQuery)) {
                psSelect.setString(1, accountNumber.toString());
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) accountId = rs.getInt("account_id");
                }
            }

            if (accountId == -1) {
                connection.rollback();
                return false;
            }

            // 3. Log Transaction
            Transaction txn = new Transaction(0, accountId, amount, Transaction.TransactionType.DEPOSIT, LocalDateTime.now());
            if (transactionDAO.insert(txn, connection)) {
                connection.commit();
                return true;
            }

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createAccount(Accounts account) {
        String query = "INSERT INTO accounts(account_number, user_id, pin, balance) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, account.getAccountNumber().toString());
            ps.setInt(2, account.getUserId());
            ps.setString(3, account.getPinHash());
            ps.setBigDecimal(4, account.getBalance());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Validates PIN and balance before deducting funds and logging transaction.
     */
    public boolean withdraw(BigInteger accountNumber, BigDecimal amount, String inputPin) {
        String fetchQuery = "SELECT * FROM accounts WHERE account_number = ?";
        String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            // 1. Fetch and Verify
            PreparedStatement psFetch = connection.prepareStatement(fetchQuery);
            psFetch.setString(1, accountNumber.toString());
            try (ResultSet rs = psFetch.executeQuery()) {
                if (!rs.next()) return false;

                Accounts acc = new Accounts(
                        rs.getInt("account_id"), accountNumber, rs.getInt("user_id"),
                        rs.getString("pin"), rs.getBigDecimal("balance")
                );

                if (!acc.verifyPin(inputPin) || acc.getBalance().compareTo(amount) < 0) {
                    connection.rollback();
                    return false;
                }

                // 2. Execute Update
                try (PreparedStatement psUpdate = connection.prepareStatement(updateQuery)) {
                    psUpdate.setBigDecimal(1, amount);
                    psUpdate.setString(2, accountNumber.toString());

                    if (psUpdate.executeUpdate() > 0) {
                        Transaction txn = new Transaction(0, acc.getAccountId(), amount, Transaction.TransactionType.WITHDRAW, LocalDateTime.now());
                        if (transactionDAO.insert(txn, connection)) {
                            connection.commit();
                            return true;
                        }
                    }
                }
            }
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Moves money between two accounts atomically.
     */
    public boolean transferMoney(BigInteger fromAccount, BigInteger toAccount, BigDecimal amount, String pin) {
        if (fromAccount.equals(toAccount)) return false;

        String selectQuery = "SELECT account_id, balance, pin FROM accounts WHERE account_number = ?";
        String updateBalance = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            // 1. Sender Validation
            PreparedStatement ps1 = connection.prepareStatement(selectQuery);
            ps1.setString(1, fromAccount.toString());
            ResultSet rs1 = ps1.executeQuery();
            if (!rs1.next()) return false;

            Accounts sender = new Accounts(rs1.getInt("account_id"), fromAccount, 0, rs1.getString("pin"), rs1.getBigDecimal("balance"));
            if (!sender.verifyPin(pin) || sender.getBalance().compareTo(amount) < 0) {
                connection.rollback();
                return false;
            }

            // 2. Receiver Validation
            PreparedStatement ps2 = connection.prepareStatement(selectQuery);
            ps2.setString(1, toAccount.toString());
            ResultSet rs2 = ps2.executeQuery();
            if (!rs2.next()) {
                connection.rollback();
                return false;
            }
            int receiverId = rs2.getInt("account_id");

            // 3. Update Balances
            try (PreparedStatement debit = connection.prepareStatement(updateBalance);
                 PreparedStatement credit = connection.prepareStatement(updateBalance)) {

                debit.setBigDecimal(1, amount.negate());
                debit.setString(2, fromAccount.toString());

                credit.setBigDecimal(1, amount);
                credit.setString(2, toAccount.toString());

                if (debit.executeUpdate() > 0 && credit.executeUpdate() > 0) {
                    // 4. Log Transactions
                    Transaction t1 = new Transaction(0, sender.getAccountId(), amount, Transaction.TransactionType.TRANSFER, LocalDateTime.now());
                    Transaction t2 = new Transaction(0, receiverId, amount, Transaction.TransactionType.TRANSFER, LocalDateTime.now());

                    if (transactionDAO.insert(t1, connection) && transactionDAO.insert(t2, connection)) {
                        connection.commit();
                        return true;
                    }
                }
            }
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}