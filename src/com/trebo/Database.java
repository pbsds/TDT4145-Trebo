package com.trebo;
import java.sql.*;


public class Database {
    private String address, database, username, password;

    public Database(String address, String database, String username, String password){
        this.address = address;
        this.username = username;
        this.database = database;
        this.password = password;
    }
    public void template_update() throws SQLException{
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = DriverManager.getConnection(this.address, this.username, this.password);

            pstmt = con.prepareStatement(
                    "UPDATE EMPLOYEES " +
                            "SET CAR_NUMBER = ? " +
                            "WHERE EMPLOYEE_NUMBER = ?");

            pstmt.setInt(1, 33);
            pstmt.setInt(2, 44);

            //pstmt.executeUpdate();
        }
        finally {
            if (pstmt != null) pstmt.close();
        }
    }

}