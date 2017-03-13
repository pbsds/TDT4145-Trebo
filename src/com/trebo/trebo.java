package com.trebo;
import java.sql.SQLException;

public class trebo {
    public static void main(String[] args) {
        System.out.println("Starting up...");
        Database db = new Database("jdbc:mysql://mysql.stud.ntnu.no:3306/pederbs_trebodb?characterEncoding=latin1",
                "pederbs_trebodb",
                "pederbs_trebo",
                "spismeg");
        
        Menu menu = new Menu(db);
        try {
            menu.run();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
            System.exit(1);
        }
    }
}
