package Banking_ATM_Simulator.src.model;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Transaction {
    // Private Access Modifier And Data Members
    private int transactionId;
    private int accountId;
    private BigDecimal amount;
    public enum TransactionType { DEPOSIT, WITHDRAW, TRANSFER }
    private TransactionType transactionType;
    private LocalDateTime transactionDate;

    // Default Constructor
    public Transaction() {
        transactionId=-1;
        this.transactionDate = LocalDateTime.now();
    }

    // Overloaded constructor for internal use (AccountDAO)
    public Transaction(int transactionId, int accountId, BigDecimal amount, TransactionType type, LocalDateTime date) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = type;
        this.transactionDate = date;
    }


    // Setters
    public void setTransactionId(int transactionId) {
        this.transactionId=transactionId;
    }
    public void setAccountId(int accountId) {
        if(accountId == 0){
            throw new IllegalArgumentException("Account Id never be empty!!");
        }
        this.accountId=accountId;
    }
    public void setAmount(BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount can not be negative");
        }
        this.amount=amount;
    }
    public void setTransactionType(String transactionType) {
        if(transactionType == null || transactionType.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction type cannot be null or empty");
        }
        try {
            this.transactionType = TransactionType.valueOf(transactionType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type. Must be DEPOSIT, WITHDRAW, or TRANSFER.");
        }
    }
    public void setTransactionDate(LocalDateTime transactionDate) {
        if(transactionDate == null) {
            throw new IllegalArgumentException("No mention transaction date");
        }
        this.transactionDate=transactionDate;
    }

    // Getters
    public int getTransactionId() {
        return transactionId;
    }
    public int getAccountId() {
        return accountId;
    }
    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    // Display
    @Override
    public String toString() {
        return String.format("%-10s | %-10s | %8.2f | %s",
                transactionDate.toLocalDate(),
                transactionType,
                amount,
                (transactionId == -1 ? "PENDING" : "#" + transactionId));
    }
}
