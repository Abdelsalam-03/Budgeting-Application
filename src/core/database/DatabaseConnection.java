package core.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public abstract class DatabaseConnection {
    
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=budgeting_application;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "abdoabdo";

    public static Connection getConnection() throws SQLException{
        try{
        Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            return con;
        } catch (SQLException e){
            System.out.println("Exception occurred while establishing the connection");
            throw e;
        }
        
    }
    
}
