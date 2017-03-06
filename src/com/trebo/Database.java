package com.trebo;
import java.sql.*;

public class Database {
    public Database(string address, string username, string password){
        database =;
    }
    public static void UpdateCarNum(int carNo, int empNo) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = DriverManager.getConnection(
                    "jdbc:default:connection");

            pstmt = con.prepareStatement(
                    "UPDATE EMPLOYEES " +
                            "SET CAR_NUMBER = ? " +
                            "WHERE EMPLOYEE_NUMBER = ?");

            pstmt.setInt(1, carNo);
            pstmt.setInt(2, empNo);
            pstmt.executeUpdate();
        }
        finally {
            if (pstmt != null) pstmt.close();
        }
    }
}