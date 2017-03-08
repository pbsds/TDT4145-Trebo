package com.trebo;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.*;

public class Menu{
    private Database db;
    private BufferedReader reader;

    public Menu(Database db) {
        this.db = db;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    private boolean confirmPrompt(String prompt) throws IOException{
        while(true) {
            System.out.print(prompt.concat(" [Y/n]: "));
            String in = reader.readLine().trim().split(" ")[0];
            switch(in) {
                case "":
                case "y":
                case "yes":
                    return true;
                case "n":
                case "no":
                    return false;
                default:
                    System.out.println("Please enter yes or no.");
                    break;
            }
        }
    }

    private Integer choicePrompt(Map<String, Integer> choiceMap) throws IOException {
        ArrayList<String> choices = new ArrayList<>();
        for(String key: choiceMap.keySet()){
            choices.add(key);
        }
        System.out.println("Please choose one of the following:");
        for(int i = 1; i <= choiceMap.size(); i++){
            System.out.println(i + ") " + choices.get(i - 1));
        }
        if (choiceMap.size() == 8) {
            System.out.println("9) More results...");
        }
        System.out.println("0) Cancel search");
        while(true) {
            System.out.print("> ");
            try {
                int in = Integer.parseInt(reader.readLine().trim().split(" ")[0]);
                if (in == 0) {
                    return -2;
                } else if (in == 9 && choiceMap.size() == 8) {
                    return -1;
                } else if (in < 0 || in > choices.size()) {
                    System.out.println("Please enter a number corresponding to the above options.");
                } else {
                    return choiceMap.get(choices.get(in - 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

    }


    private void inputVarighet() throws IOException {
        System.out.println("Enter Varighet: ");
    }

    private int searchØvelse() throws SQLException, IOException {
        while(true) {
            System.out.println("Enter the name of an Øvelse."); // oh shit, it's på norsk
            System.out.print("> ");
            String in = reader.readLine().trim().split(" ")[0];
            ResultSet res = db.getØvelseByName(in);
            if(!res.isBeforeFirst()){ // is empty
                System.out.println("Could not find Øvelse with that name.");
            }
            else if(res.next() && res.isLast()){ // is length 1
                if(this.confirmPrompt("Do you want to use " + res.getString("Navn") + "?")){
                    return res.getInt("ØvelseID");
                }
            } else {
                //res.previous();
                Map<String, Integer> øvelser = new LinkedHashMap<>(8);
                    while (!res.isAfterLast()) {
                        øvelser.put(res.getString("Navn"), res.getInt("ØvelseID"));
                        res.next();
                        if (øvelser.size() == 8 || res.isAfterLast()) {
                             int øvelse = this.choicePrompt(øvelser);
                             if(øvelse == -1) {
                                 øvelser.clear();
                             } else if(øvelse == -2) {
                                 break;
                             } else {
                                 return øvelse;
                             }
                        }
                    }
            }
            res.close();
        }
    }

    class Øvelse{
        public int øvelseid;
        public String navn;
        public String beskrivelse;
        public String belastning;
        public int lengde;
        public short repetisjoner;
        public short sett;

        Øvelse(){};
    }

    class Treningsøkt{
        public int treningsøktid;
        public Øvelse[] øvelser;
        //public Geodata[] geodatapunkter;
        public String tidspunkt;
        public int varighet;
        public short form;
        public short prestasjon;
        public short temperatur;
        public String værtype;
        public int måldenne;
        public int målneste;

        Treningsøkt(){};

        public void print(){
            System.out.println("=Treningsøkt=");
            int i = 1;
            for (Øvelse ø: this.øvelser) {
                System.out.println("Øvelse #" + i + ": " + ø.navn);
                i++;
            }
            System.out.println("Tidspunkt: " + this.tidspunkt);
            System.out.println("Varighet: " + this.varighet);
            System.out.println("Form: " + this.form);
            System.out.println("Prestasjon: " + this.prestasjon);
            System.out.println("Temperatur: " + this.temperatur);
            System.out.println("Værtype: " + this.værtype);
        }
    }

    private void addTreningsøktMenu() throws SQLException, IOException {
        Treningsøkt økt = new Treningsøkt();
        System.out.println("=== Add Treningsøkt ===");
        økt.print();
        if (!this.confirmPrompt("Use current time for Tidspunkt?")) {
            System.out.println("Feature not yet added. Using current time for Tidspunkt.");
        }
        økt.tidspunkt = "Now";
        økt.print();


    }

    /*
    public interface MenuElement {
        void show();
    }

    class TreningsøktMenu {
        void run() {

        }
    }

    TreningsøktMenu treningsøkt = new TreningsøktMenu();

    MenuElement[] menuelements = new MenuElement[] {
            new MenuElement() { public void show() { treningsøkt.run(); } },
            new MenuElement() { public void show() { treningsøkt.run(); } },
    };

    public boolean mainMenu(){
        return true;
    }

    */
    public void run() throws SQLException, IOException {
        int øvelse = this.searchØvelse();
    }
}