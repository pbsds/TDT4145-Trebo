package com.trebo;
import java.sql.SQLException;

public class trebo {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        Database db = new Database("jdbc:mysql://mysql.stud.ntnu.no:3306/pederbs_trebodb",
                "pederbs_trebodb",
                "pederbs_trebo",
                "spismeg");

        /*
        try {
            db.template_update();
        } catch (SQLException e) {
            //lolno
        }
        */
    }

}
