package Banking_ATM_Simulator.src.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Users {
    private int userId;
    private String username;
    private String passwordHash;

    // For creating a NEW user (takes plain password and hashes it)
    public Users(int userId, String username, String plainPassword) {
        this.userId = userId;
        setUsername(username);
        setPassword(plainPassword); // This calls the hashing logic
    }

    // For loading from DB (takes existing hash)
    public Users(int userId, String username, String passwordHash, boolean isAlreadyHashed) {
        this.userId = userId;
        this.username = username;
        if (isAlreadyHashed) {
            this.passwordHash = passwordHash;
        } else {
            setPassword(passwordHash);
        }
    }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setUsername(String username) {
        // Allows letters, numbers, and underscores; 4-20 chars long
        String regex = "^[a-zA-Z0-9_]{4,20}$";
        if (username == null || !username.matches(regex)) {
            throw new IllegalArgumentException("Username must be 4-20 alphanumeric characters.");
        }
        this.username = username;
    }

    public void setPassword(String plainPassword) {
        // Min 8 chars, at least one letter and one number
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$";
        if (plainPassword == null || !plainPassword.matches(regex)) {
            throw new IllegalArgumentException("Password must be at least 8 characters and include a letter and a number.");
        }
        this.passwordHash = hashPassword(plainPassword);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public boolean verifyPassword(String inputPassword) {
        return this.passwordHash.equals(hashPassword(inputPassword));
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }

    @Override
    public String toString() {
        return "User{id=" + userId + ", username='" + username + "'}";
    }
}