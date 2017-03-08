package com.trebo;
import java.sql.*;
import java.util.Vector;

public class Database {
    private String address, database, username, password;
    private Connection con = null;
    public Database(String address, String database, String username, String password) {
        this.address = address;
        this.username = username;
        this.database = database;
        this.password = password;
        try {
            this.con = DriverManager.getConnection(this.address, this.username, this.password);
        } catch (SQLException e) {
            System.out.println("Couldn't connect to server: " + this.address);
            System.out.println(e);
            System.exit(1);
        }
    }
    
    //functions:
    public int addTreningsøkt(long Tidspunkt, int Varighet, int Form, short Prestasjon, Integer Temperatur, String Værtype) throws SQLException {
        return addTreningsøkt(Tidspunkt, Varighet, Form, Prestasjon, Temperatur, Værtype, null, null);
    }
    public int addTreningsøkt(long Tidspunkt, int Varighet, int Form, short Prestasjon, Integer Temperatur, String Værtype, Integer MålDenne, Integer MålNeste) throws SQLException {//INSERT, UPDATE or DELETE
        assert Værtype.length() <= 20;
    
        PreparedStatement pstmt = this.con.prepareStatement(
                "INSERT INTO Treningsøkt" +
                        "(Tidspunkt, Varighet, Form, Prestasjon, Temperatur, Værtype, MålDenne, MålNeste)" +
                        "VALUES (?,?,?,?,?,?,?,?)");
    
        pstmt.setLong(1, Tidspunkt);
        pstmt.setInt(2, Varighet);
        pstmt.setInt(3, Form);
        pstmt.setInt(4, Prestasjon);
        pstmt.setObject(5, Temperatur);
        pstmt.setString(6, Værtype);
        pstmt.setObject(7, MålDenne);
        pstmt.setObject(8, MålNeste);
    
        int out = -1;
        try {
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            out = rs.getInt(1);
        } finally {
            pstmt.close();
        }
        return out;
    }
    
    public void addGeodata(int TreningsøktID, long Tid, short puls, double lengdegrad, double breddegrad, short moh) throws SQLException {//INSERT, UPDATE or DELETE
        PreparedStatement pstmt = this.con.prepareStatement(
                "INSERT INTO Geodata " +
                        "(TreningsøktID, Tid, Puls, Lengdegrad, Breddegrad, Moh) " +
                        "VALUES (?,?,?,?,?,?)");
        
        pstmt.setInt(1, TreningsøktID);
        pstmt.setLong(2, Tid);
        pstmt.setShort(3, puls);
        pstmt.setDouble(4, lengdegrad);
        pstmt.setDouble(5, breddegrad);
        pstmt.setShort(6, moh);
        
        try {
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }
    public ResultSet getGeodata(int TreningsøktID) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Geodata " +
                        "WHERE TreningsøktID = ?" +
                        "ORDER BY GeodataID ASC ");
        
        pstmt.setInt(1, TreningsøktID);
        
        ResultSet out;
        try {
            out = pstmt.executeQuery();
        } finally {
            pstmt.close();
        }
        
        return out;
    }
    
    public void addØvingsgjennomføring(Integer repetisjoner, Integer sett, Integer lengde, int TreningsøktID, int ØvelseID) throws SQLException {//INSERT, UPDATE or DELETE
        PreparedStatement pstmt = this.con.prepareStatement(
                "INSERT INTO Øvelsegjennomføring " +
                        "(Repetisjoner, Sett, Lengde, TreningsøktID, ØvelseID) " +
                        "VALUES (?,?,?,?,?)");
    
        pstmt.setObject(1, repetisjoner);
        pstmt.setObject(2, sett);
        pstmt.setObject(3, lengde);
        pstmt.setInt(4, TreningsøktID);
        pstmt.setInt(5, ØvelseID);
    
        try {
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }
    
    public ResultSet getØvelse(int ØvelseID) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Øvelse " +
                        "WHERE ØvelseID = ?");
        
        pstmt.setInt(1, ØvelseID);
        
        ResultSet out;
        out = pstmt.executeQuery();
        
        return out;
    }
    public ResultSet getAllØvelser() throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Øvelse ");
        
        ResultSet out;
        out = pstmt.executeQuery();
        
        return out;
    }
    public ResultSet getØvelseByName(String navn) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Øvelse " +
                        "WHERE Navn LIKE ?");
        
        pstmt.setString(1, "%" + navn + "%");
        
        ResultSet out;
        out = pstmt.executeQuery();
        
        return out;
    }
    
    public class GruppelisteElement {//totally not a struct
        public String label;
        public boolean isØvelse = false;
        public int ID;
    }
    public Vector<GruppelisteElement> getGruppeliste(GruppelisteElement gruppe) throws SQLException {//inkluderer øvelser som er bed i gruppen
        Vector<GruppelisteElement> outVec = new Vector<GruppelisteElement>();
        if (gruppe.isØvelse){
            return outVec;
        }
        
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT *" +
                        "FROM Gruppe " +
                        "WHERE GruppeID in (SELECT UndergruppeID FROM GruppeSubgruppe WHERE GruppeID = ?)");
        
        pstmt.setInt(1, gruppe.ID);
        
        ResultSet out;
        out = pstmt.executeQuery();
        
        GruppelisteElement e;
        while (out.next()){
            e = new GruppelisteElement();
            e.label = out.getString("Navn");
            e.isØvelse = false;
            e.ID    = out.getInt("GruppeID");
            outVec.add(e);
        }
    
        for (GruppelisteElement es : getØvelserInGruppe(gruppe)){
            outVec.add(es);
        }
         
        return outVec;
    }
    public Vector<GruppelisteElement> getØvelserInGruppe(GruppelisteElement gruppe) throws SQLException {//kun øvelsene i gruppen
        Vector<GruppelisteElement> outVec = new Vector<GruppelisteElement>();
        if (gruppe.isØvelse){
            return outVec;
        }
        
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT *" +
                        "FROM Øvelse " +
                        "WHERE ØvelseID in (SELECT ØvelseID FROM ØvelseGruppe WHERE GruppeID = ?)");
        
        pstmt.setInt(1, gruppe.ID);
        
        ResultSet out;
        out = pstmt.executeQuery();
    
        GruppelisteElement e;
        while (out.next()){
            e = new GruppelisteElement();
            e.label = out.getString("Navn");
            e.isØvelse = true;
            e.ID = out.getInt("ØvelseID");
            outVec.add(e);
        }
        
        return outVec;
    }
    
    public void addNotat(int treningsøktID, String notat) throws SQLException {//INSERT, UPDATE or DELETE
        assert notat.length() <= 600;
    
        PreparedStatement pstmt = this.con.prepareStatement(
                "INSERT INTO Notat " +
                        "(TreningsøktID, Notat)" +
                        "VALUES (?,?)");
    
        pstmt.setInt(1, treningsøktID);
        pstmt.setString(2, notat);
    
        try {
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }
    public String getNotat(int TreningsøktID) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT Notat " +
                        "FROM Notat " +
                        "WHERE TreningsøktID = ?");
        
        pstmt.setInt(1, TreningsøktID);
        
        ResultSet out;
        out = pstmt.executeQuery();
        
        if (out.next()) {
            return out.getString("Notat");
        } else {
            return "Ingen notat";
        }
    }
    
    
    //templates:
    public void template_update() throws SQLException {//INSERT, UPDATE or DELETE
        PreparedStatement pstmt = this.con.prepareStatement(
                "UPDATE tabell " +
                        "SET attrib = ? " +
                        "WHERE ID = ?");
    
        pstmt.setInt(1, 33);
        pstmt.setInt(2, 44);
    
        try {
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }
    
    public void template_insert() throws SQLException {//INSERT, UPDATE or DELETE
        PreparedStatement pstmt = this.con.prepareStatement(
                "INSERT INTO tabell " +
                        "(par_A, par_B) " +
                        "VALUES (?,?)");
        
        pstmt.setInt(1, 33);
        pstmt.setInt(2, 44);
        
        try {
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }
    
    public ResultSet template_select() throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM  " +
                        "WHERE EMPLOYEE_NUMBER = ?");

        pstmt.setInt(1, 1337);

        ResultSet out;
        out = pstmt.executeQuery();
        
        return out;
    }
}