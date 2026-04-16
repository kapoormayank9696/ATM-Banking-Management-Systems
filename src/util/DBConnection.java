package Banking_ATM_Simulator.src.util;

import java.sql.*;

public class DBConnection {
    static final String url="jdbc:mysql://127.0.0.1:3306/atm";
    static final String username="root";
    static final String password="9689";
    // Function to connect with mysql by using jdbc
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url,username,password);
    }
}
