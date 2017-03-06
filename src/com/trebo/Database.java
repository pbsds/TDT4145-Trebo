package com.trebo;
import java.sql.*;

public class Database {
    private String address, database, username, password;
    Connection con = null;

    public Database(String address, String database, String username, String password){
        this.address = address;
        this.username = username;
        this.database = database;
        this.password = password;
        try {
            con = DriverManager.getConnection(this.address, this.username, this.password);
        }catch(SQLException e){
            System.out.println("Couldn't connect to server: " + this.address);
            System.out.println(e);
            System.exit(1);
        }
    }

    public void template_update() throws SQLException{//INSERT, UPDATE or DELETE
        PreparedStatement pstmt = this.con.prepareStatement(
                "UPDATE EMPLOYEES " +
                        "SET CAR_NUMBER = ? " +
                        "WHERE EMPLOYEE_NUMBER = ?");

        pstmt.setInt(1, 33);
        pstmt.setInt(2, 44);

        try {
            pstmt.executeUpdate();
        }
        finally {
            if (pstmt != null) pstmt.close();
        }
    }
    public void template_select() throws SQLException{
        PreparedStatement pstmt = this.con.prepareStatement(
                "UPDATE EMPLOYEES " +
                        "SET CAR_NUMBER = ? " +
                        "WHERE EMPLOYEE_NUMBER = ?");

        pstmt.setInt(1, 1337);
        pstmt.setInt(2, 42);

        ResultSet out;
        try {
            out = pstmt.executeQuery();
        }
        finally {
            pstmt.close();
        }

        //out
    }
}