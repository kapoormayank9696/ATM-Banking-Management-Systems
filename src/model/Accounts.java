package Banking_ATM_Simulator.src.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Accounts {
    private final int accountId;
    private BigInteger accountNumber;
    private int userId;
    private String pinHash;
    private BigDecimal balance;

    // 1. Constructor for Database Loading (Receives the ALREADY hashed PIN)
    public Accounts(int accountId, BigInteger accountNumber, int userId, String pinHash, BigDecimal balance) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.pinHash = pinHash;
        this.balance = balance;
    }

    // 2. Logic for handling PIN Security
    public void updatePin(String plainPin) {
        if (plainPin == null || !plainPin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must be exactly 4 digits!");
        }
        this.pinHash = hashPin(plainPin);
    }

    public String getPinHash() {
        return pinHash;
    }

    private String hashPin(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(pin.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public boolean verifyPin(String inputPin) {
        return hashPin(inputPin).equals(this.pinHash);
    }

    // 3. Setters for other fields
    public void setAccountNumber(BigInteger accountNumber) {
        if(accountNumber == null || accountNumber.equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("Account number cannot be null or zero!");
        }
        this.accountNumber = accountNumber;
    }

    public void setUserId(int userId) {
        if(userId <= 0) {
            throw new IllegalArgumentException("User Id must be positive!");
        }
        this.userId = userId;
    }

    public void setBalance(BigDecimal balance) {
        if(balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative!");
        }
        this.balance = balance;
    }

    // 4. Getters
    public int getAccountId() { return accountId; }
    public BigInteger getAccountNumber() { return accountNumber; }
    public int getUserId() { return userId; }
    public BigDecimal getBalance() { return balance; }

    @Override
    public String toString() {
        return "Account{id=" + accountId +
                ", number=" + accountNumber +
                ", userId=" + userId +
                ", balance=" + balance + "}";
    }
}