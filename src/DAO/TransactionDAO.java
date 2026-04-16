package Banking_ATM_Simulator.src.DAO;

import Banking_ATM_Simulator.src.model.Transaction;
import Banking_ATM_Simulator.src.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // Note: We don't use try-with-resources for Connection here
    // because AccountDAO is managing this connection's lifecycle.
    public boolean insert(Transaction transaction, Connection connection) throws SQLException {
        String query = "INSERT INTO transactions(account_id, amount, transaction_type, transaction_date) VALUES(?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, transaction.getAccountId());
            ps.setBigDecimal(2, transaction.getAmount());

            // Ensuring we store the String representation of the Enum
            ps.setString(3, transaction.getTransactionType().toString());

            ps.setTimestamp(4, Timestamp.valueOf(transaction.getTransactionDate()));
            return ps.executeUpdate() > 0;
        }
    }

    public List<Transaction> getTransactionsByAccountId(int accountId) {
        List<Transaction> list = new ArrayList<>();
        // Added a LIMIT to keep the "Mini Statement" concise
        String query = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC LIMIT 5";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction txn = new Transaction(
                            rs.getInt("transaction_id"),
                            rs.getInt("account_id"),
                            rs.getBigDecimal("amount"),
                            // Convert String back to Enum if your model requires it
                            Transaction.TransactionType.valueOf(rs.getString("transaction_type").toUpperCase()),
                            rs.getTimestamp("transaction_date").toLocalDateTime()
                    );
                    list.add(txn);
                }
            }
        } catch (SQLException e) {
            // In a real app, use a logger (like SLF4J/Log4j) instead of printStackTrace
            System.err.println("Error fetching transactions: " + e.getMessage());
        }
        return list;
    }
}