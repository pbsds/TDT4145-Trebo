package com.trebo;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
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
            String in = reader.readLine().trim().split(" ")[0].toLowerCase();
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

    private Integer inputInteger() throws IOException {
        return inputInteger(false);
    }

    private Integer inputInteger(boolean allownull) throws IOException {
        while (true) {
            try {
                String in = reader.readLine();
                if (allownull && in.trim().equals("")){
                    return null;
                }
                return Integer.parseInt(in.trim().split(" ")[0]);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private Integer inputRange(String prompt, Integer floor, Integer ceiling) throws IOException{
        return this.inputRange(prompt, floor, ceiling, false);
    }

    private Integer inputRange(String prompt, Integer floor, Integer ceiling, boolean allownull) throws IOException {
        assert(floor != null || ceiling != null);
        if (floor != null && ceiling != null) {
            assert(!floor.equals(ceiling));
        }

        System.out.print(prompt);
        if (allownull) {
            System.out.print(" (leave blank to skip)");
        }
        if (floor == null) {
            System.out.println(" (max " + ceiling + ")> ");
            while(true) {
                Integer in = this.inputInteger(allownull);
                if (allownull && in == null) {
                    return null;
                }
                if (in <= ceiling) {
                    return in;
                }
                System.out.println("Please enter a number below or equal to " + ceiling + ".");
            }
        } else if (ceiling == null) {
            System.out.println(" (min " + ceiling + ")> ");
            while(true) {
                Integer in = this.inputInteger(allownull);
                if (allownull && in == null) {
                    return null;
                }
                if (in >= floor) {
                    return in;
                }
                System.out.println("Please enter a number above or equal to " + floor + ".");
            }
        } else {
            System.out.println(" (" + floor + ", " + ceiling + ")> ");
            while(true) {
                Integer in = this.inputInteger(allownull);
                if (allownull && in == null) {
                    return null;
                }
                if (in < floor || in > ceiling) {
                    return in;
                }
                System.out.println("Please enter a number between or equal to " + floor + " and " + ceiling + ".");
            }
        }
    }

    private Short inputShortRange(String prompt, Integer floor, Integer ceiling) throws IOException {
        return this.inputShortRange(prompt, floor, ceiling, false);
    }

    private Short inputShortRange(String prompt, Integer floor, Integer ceiling, boolean allownull) throws IOException {
        Integer in = this.inputRange(prompt, floor, ceiling, allownull);
        if (allownull && in == null) {
            return null;
        }
        return in.shortValue();
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
            int in = this.inputInteger();
            if (in == 0) {
                return -2;
            } else if (in == 9 && choiceMap.size() == 8) {
                return -1;
            } else if (in < 0 || in > choices.size()) {
                System.out.println("Please enter a number corresponding to the above options.");
            } else {
                return choiceMap.get(choices.get(in - 1));
            }
        }
    }

    private int inputVarighet() throws IOException {
        while(true){
            System.out.println("Enter Varighet (0h 0m 0s)> ");
            String in = reader.readLine();
            Scanner scan = new Scanner(in);
            int hours = 0, minutes = 0, seconds = 0;
            if(scan.hasNext("\\d+[h]")){
                String num = scan.next("\\d+[h]");
                hours = Integer.parseInt(num.substring(0, num.length()-1));
            }
            if(scan.hasNext("\\d+[m]")){
                String num = scan.next("\\d+[m]");
                minutes = Integer.parseInt(num.substring(0, num.length()-1));
            }
            if(scan.hasNext("\\d+[s]")){
                String num = scan.next("\\d+[s]");
                seconds = Integer.parseInt(num.substring(0, num.length()-1));
            }
            scan.close();

            if (hours != 0 || minutes != 0 || seconds != 0) {
                if (confirmPrompt("Parsed as " + hours + "h " + minutes + "m " + seconds +
                        "s. Is this correct?")) {
                    return hours * 60 * 60 + minutes * 60 + seconds;
                }
            } else {
                System.out.println("Please enter a valid Varighet.");
            }
        }
    }

    private String toVarighet(int varighet) {
        if (varighet == 0) {
            return "";
        }
        int hours = varighet / (60 * 60);
        varighet %= 60 * 60;
        int minutes = varighet / 60;
        varighet %= 60;
        return hours + "h " + minutes + "m " + varighet + "s";
    }

    private ResultSet searchØvelse() throws SQLException, IOException {
        return this.searchØvelse(false);
    }

    private ResultSet searchØvelse(boolean allownull) throws SQLException, IOException {
        while(true) {
            System.out.print("Enter the name of an Øvelse."); // oh shit, it's på norsk
            if (allownull) {
                System.out.print(" (enter q to cancel)");
            }
            System.out.print("> ");
            String in = reader.readLine().trim().split(" ")[0].toLowerCase();
            if (allownull && in.equals("q")) {
                return null;
            }
            ResultSet res = db.getØvelseByName(in);
            if(!res.isBeforeFirst()){ // is empty
                System.out.println("Could not find Øvelse with that name.");
            }
            else if(res.next() && res.isLast()){ // is length 1
                if(this.confirmPrompt("Do you want to use " + res.getString("Navn") + "?")){
                    return res;
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
                                 res = db.getØvelse(øvelse);
                                 res.next();
                                 return res;
                             }
                        }
                    }
            }
            res.close();
        }
    }

    class Geodata{
        public int geodataid;
        public int treningsøktid;
        public long tid;
        public short puls;
        public float lengdegrad;
        public float breddegrad;
        public short moh;

        Geodata(int treningsøktid) {
            this.treningsøktid = treningsøktid;
        }

        Geodata(int geodataid, int treningsøktid, long tid, short puls, float lengdegrad, float breddegrad, short moh){
            //later
        }
    }

    class Øvelse{//gjennomføring?
        public int øvelseid;
        public String navn;
        public String beskrivelse;
        public int belastning;
        public Integer lengde;
        public Short repetisjoner;
        public Short sett;

        Øvelse(){}

        //loads from a Øvelse row.
        // repetisjoner, lengde and sett could be overwritten if you provide a øvelsegjennomføringid
        Øvelse(int øvelseid, Integer øvelsegjennomføringid) throws SQLException {//use the Øvelse as a template
            this(db.getØvelse(øvelseid), øvelsegjennomføringid);
        }
        Øvelse(ResultSet øvelsers, Integer øvelsegjennomføringid) throws SQLException{//use the Øvelse as a template
            if (øvelsers.next()){
                øvelseid = øvelsers.getInt("ØvelseID");
                navn = øvelsers.getString("Navn");
                beskrivelse = øvelsers.getString("Beskrivelse");
                belastning = øvelsers.getInt("Belastning");

                if (øvelsegjennomføringid != null) {
                    ResultSet ørs = db.getØvingsgjennomføring(øvelsegjennomføringid);
                    if (ørs.next()) {
                        øvelsers = ørs;
                    }
                }

                lengde = øvelsers.getInt("Lengde");
                if (øvelsers.wasNull()){lengde=null;}
                repetisjoner = øvelsers.getShort("Repetisjoner");
                if (øvelsers.wasNull()){repetisjoner=null;}
                sett = øvelsers.getShort("Sett");
                if (øvelsers.wasNull()){sett=null;}
            }
        }
    }

    class Treningsøkt{
        public int treningsøktid;
        public ArrayList<Øvelse> øvelser;
        public ArrayList<Geodata> geodatapunkter;
        public String tidspunkt;
        public int varighet;
        public short form;
        public short prestasjon;
        public Short temperatur;
        public String værtype;
        public Integer måldenne;
        public Integer målneste;
        public String notat;

        Treningsøkt(){
            this.øvelser = new ArrayList<>();
            this.temperatur = null;
            this.værtype = null;
            this.måldenne = null;
            this.målneste = null;
            this.notat = null;
        }

        Treningsøkt(int treningsøktid) throws SQLException{
            ResultSet rs = db.getTreningsøkt(treningsøktid);
            if (rs.next()){
                this.treningsøktid = treningsøktid;
                tidspunkt = rs.getString("Tidspunkt");
                varighet = rs.getInt("Varighet");
                form = rs.getShort("Form");
                prestasjon = rs.getShort("Prestasjon");
                temperatur = rs.getShort("Temperatur");
                if (rs.wasNull()){temperatur=null;}
                værtype = rs.getString("Værtype");
                if (rs.wasNull()){værtype=null;}
                måldenne = rs.getInt("MålDenne");
                if (rs.wasNull()){måldenne=null;}
                målneste = rs.getInt("MålNeste");
                if (rs.wasNull()){målneste=null;}

                øvelser = new ArrayList<Øvelse>();
                ResultSet ørs = db.getØvingsgjennomføringer(treningsøktid);
                while (rs.next()){
                    Øvelse ø = new Øvelse(
                            ørs.getInt("ØvelseID"),
                            ørs.getInt("ØvelsegjennomføringID")
                    );
                    øvelser.add(ø);
                }

                this.notat = db.getNotat(this.treningsøktid);
                if (this.notat.equals("Ingen notat")) {
                    this.notat = null;
                }


            }
        }

        public void print(){
            System.out.println();
            System.out.println("=Treningsøkt=");
            int i = 1;
            for (Øvelse ø: this.øvelser) {
                System.out.println("Øvelse #" + i + ": " + ø.navn);
                i++;
            }
            System.out.println("Tidspunkt: " + this.tidspunkt);
            System.out.println("Varighet: " + toVarighet(this.varighet));
            System.out.println("Form: " + this.form);
            System.out.println("Prestasjon: " + this.prestasjon);
            System.out.println("Temperatur: " + this.temperatur);
            System.out.println("Værtype: " + this.værtype);
            System.out.println();
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
        System.out.println("How long did you train?");
        økt.varighet = this.inputVarighet();

        økt.print();
        System.out.println("Were you in good shape? Higher numbers are better.");
        økt.form = this.inputShortRange("Please enter an integer.", 0, 10);

        økt.print();
        System.out.println("How well do you feel you did? Higher numbers are better.");
        økt.prestasjon = this.inputShortRange("Please enter an integer.", 0, 10);

        økt.print();
        System.out.println("What was the average temperature?");
        økt.temperatur = this.inputShortRange("Please enter an integer.", (int)Short.MIN_VALUE, (int)Short.MAX_VALUE, true);

        økt.print();
        System.out.println("What was today's weather? (leave blank to skip)> ");
        økt.værtype = reader.readLine();
        if (økt.værtype.trim().equals("")){
            økt.værtype = null;
        }

        økt.print();
        System.out.println("Now it is time to add Øvelser to your session.");
        økt.øvelser.add(new Øvelse(this.searchØvelse(), null));
        while(true){
            if (!this.confirmPrompt("Do you want to add another øvelse?")){
                break;
            }
            ResultSet øvelse = this.searchØvelse(true);
            if (øvelse != null) {
                økt.øvelser.add(new Øvelse(øvelse, null));
            }
        }
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
        this.addTreningsøktMenu();
    }
}