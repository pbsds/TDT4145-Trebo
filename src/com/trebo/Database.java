package com.trebo;
import java.sql.*;
import java.util.ArrayList;

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
    public int       addTreningsøkt(Menu.Treningsøkt Treningsøkt) throws SQLException{
        long tid;
        if (Treningsøkt.tidspunkt == "Now"){
            tid = System.currentTimeMillis()/1000L - Treningsøkt.varighet;
        } else {
            tid = 0;
        }
        
        int ID = this.addTreningsøkt(
                tid,
                Treningsøkt.varighet,
                Treningsøkt.form,
                Treningsøkt.prestasjon,
                Treningsøkt.temperatur,
                Treningsøkt.værtype,
                Treningsøkt.måldenne,
                Treningsøkt.målneste
        );
        
        for (Menu.Øvelse ø : Treningsøkt.øvelser){
            addØvelsegjennomføring(
                    ø.repetisjoner,
                    ø.sett,
                    ø.lengde,
                    ID,
                    ø.øvelseid
            );
        }
        
        for (Menu.Geodata g : Treningsøkt.geodatapunkter){
            addGeodata(g);
        }
        
        if (Treningsøkt.notat!=null && Treningsøkt.notat.length() > 0){
            addNotat(ID, Treningsøkt.notat);
        }
        
        return ID;
    }
    public int       addTreningsøkt(long Tidspunkt, int Varighet, int Form, short Prestasjon, Short Temperatur, String Værtype) throws SQLException {
        return addTreningsøkt(Tidspunkt, Varighet, Form, Prestasjon, Temperatur, Værtype, null, null);
    }
    public int       addTreningsøkt(long Tidspunkt, int Varighet, int Form, short Prestasjon, Short Temperatur, String Værtype, Integer MålDenne, Integer MålNeste) throws SQLException {//INSERT, UPDATE or DELETE
        assert Værtype.length() <= 20;
    
        if (MålDenne==null){
            ResultSet rs = getTreningsøkt();
            if (rs.next()){
                MålDenne = rs.getInt("MålDenne");
            }
        }
        if (MålNeste==null){
            MålNeste = MålDenne;
        }
        
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
    public ResultSet getTreningsøkt() throws SQLException{
        return getTreningsøkt(null);
    }
    public ResultSet getTreningsøkt(Integer TreningsøktID) throws SQLException {
        PreparedStatement pstmt;
        if (TreningsøktID==null){
             pstmt = this.con.prepareStatement(
                    "SELECT * " +
                            "FROM Treningsøkt " +
                            "ORDER BY TreningsøktID DESC " +
                            "LIMIT 1");
        } else{
            pstmt = this.con.prepareStatement(
                    "SELECT * " +
                            "FROM Treningsøkt " +
                            "WHERE TreningsøktID = ?");
            pstmt.setInt(1, TreningsøktID);
        }
        
        return pstmt.executeQuery();
    }
    
    public void      addGeodata(Menu.Geodata Geodata) throws SQLException{
        addGeodata(
                Geodata.treningsøktid,
                Geodata.tid,
                Geodata.puls,
                Geodata.lengdegrad,
                Geodata.breddegrad,
                Geodata.moh
        );
    }
    public void      addGeodata(int TreningsøktID, long Tid, short puls, double lengdegrad, double breddegrad, short moh) throws SQLException {//INSERT, UPDATE or DELETE
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
    
    public void addØvelsegjennomføring(Short repetisjoner, Short sett, Integer lengde, int TreningsøktID, int ØvelseID) throws SQLException {//INSERT, UPDATE or DELETE
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
    public ResultSet getØvelsegjennomføring(int ØvelsegjennomføringID) throws SQLException{
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Øvelsegjennomføring " +
                        "WHERE ØvelsesgjennomføringID = ?");
        
        pstmt.setInt(1, ØvelsegjennomføringID);
        
        return pstmt.executeQuery();
    }
    public ResultSet getØvingsgjennomføringer(int TreningsøktID) throws SQLException{
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Øvelsegjennomføring " +
                        "WHERE TreningsøktID = ?");
        
        pstmt.setInt(1, TreningsøktID);
        
        return pstmt.executeQuery();
    }
    
    public ResultSet getØvelse(int ØvelseID) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Øvelse " +
                        "WHERE ØvelseID = ?");
        
        pstmt.setInt(1, ØvelseID);
        
        return pstmt.executeQuery();
    }
    public ResultSet getØvelser(int TreningsøktID) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Øvelse " +
                        "WHERE Øvelse.ØvelseID in (SELECT  Øvelsegjennomføring.ØvelseID " +
                                                    "FROM  Øvelsegjennomføring " +
                                                    "WHERE Øvelsegjennomføring.ØvelseID = ?)");
        
        pstmt.setInt(1, TreningsøktID);
        
        return pstmt.executeQuery();
    }
    public ResultSet getAllØvelser() throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Øvelse ");
        
        return pstmt.executeQuery();
    }
    public ResultSet getØvelserByName(String navn) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Øvelse " +
                        "WHERE Navn LIKE ?" +
                        "ORDER BY Navn");
        
        pstmt.setString(1, "%" + navn + "%");
        
        return pstmt.executeQuery();
    }
    public ResultSet getØvelserByMål(int MålID) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Øvelse AS ø " +
                        "WHERE ø.ØvelseID IN (SELECT m.ØvelseID FROM MålØvelse AS m WHERE MålID = ?)");
        
        pstmt.setInt(1, MålID);
        return pstmt.executeQuery();
    }
    public ResultSet getØvelserRelatedToØvelse(int ØvelseID) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Øvelse AS ø " +
                        "WHERE ø.ØvelseID IN (SELECT RelatertØvelseID FROM ØvelseRelaterte AS R WHERE R.ØvelseID = ?)");
        
        pstmt.setInt(1, ØvelseID);
        return pstmt.executeQuery();
    }
    
    public class GruppelisteElement {//totally not a struct
        public String label;
        public boolean isØvelse = false;
        public int ID;
    }
    public ArrayList<GruppelisteElement> getGruppeliste() throws SQLException {//inkluderer øvelser som er bed i gruppen
        GruppelisteElement e = new GruppelisteElement();
        e.ID = 1;//root
        return getGruppeliste(e);
    }
    public ArrayList<GruppelisteElement> getGruppeliste(GruppelisteElement gruppe) throws SQLException {//inkluderer øvelser som er bed i gruppen
        ArrayList<GruppelisteElement> outVec = new ArrayList<GruppelisteElement>();
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
    public ArrayList<GruppelisteElement> getØvelserInGruppe(GruppelisteElement gruppe) throws SQLException {//kun øvelsene i gruppen
        ArrayList<GruppelisteElement> outVec = new ArrayList<GruppelisteElement>();
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
    
    public void   addNotat(int TreningsøktID, String notat) throws SQLException {//INSERT, UPDATE or DELETE
        assert notat.length() <= 600;
    
        PreparedStatement pstmt = this.con.prepareStatement(
                "INSERT INTO Notat " +
                        "(TreningsøktID, Notat)" +
                        "VALUES (?,?)");
    
        pstmt.setInt(1, TreningsøktID);
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
            return null;
        }
    }
    
    public int       addMål(long dato, ArrayList<Integer> ØvelseIDer) throws SQLException {//INSERT, UPDATE or DELETE
        PreparedStatement pstmt = this.con.prepareStatement(
                "INSERT INTO Mål " +
                        "(Dato) " +
                        "VALUES (?)");
        
        pstmt.setLong(1, dato);
    
        int ID = -1;
        try {
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            ID = rs.getInt(1);
        } finally {
            pstmt.close();
        }
        
        //add MålØvelse relations:
        for (Integer ØvelseID : ØvelseIDer){
            if (ØvelseID == null){
                continue;
            }
            PreparedStatement øpstmt = this.con.prepareStatement(
                    "INSERT INTO MålØvelse " +
                            "(MålID, ØvelseID) " +
                            "VALUES (?,?)");
    
            øpstmt.setInt(1, ID);
            øpstmt.setInt(2, ØvelseID);
    
            try {
                øpstmt.executeUpdate();
            } finally {
                øpstmt.close();
            }
        }
        
        return ID;
    }
    public ResultSet getAllMåls() throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Mål");
        
        return pstmt.executeQuery();
    }
    public ResultSet getMål(int MålID) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement(
                "SELECT * " +
                        "FROM Mål " +
                        "WHERE MålID = ?");
        
        pstmt.setInt(1, MålID);
        
        return pstmt.executeQuery();
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

        return pstmt.executeQuery();
    }
}