package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;


    private DBConnection() {

        try {

            Class.forName("com.mysql.jdbc.Driver");
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            System.setProperty("com.mysql.cj.jdbc.Driver", "com.mysql.cj.jdbc.Driver");


            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/billingsystem","root","password");



        } catch (ClassNotFoundException | SQLException e) {

            throw new RuntimeException(e);
        }


    }

    public static DBConnection getInstance() {

        return (dbConnection == null) ? dbConnection = new DBConnection() : dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }

}
