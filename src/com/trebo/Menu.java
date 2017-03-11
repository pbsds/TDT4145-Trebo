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

    private Float inputFloat() throws IOException {
        return inputFloat(false);
    }
    private Float inputFloat(boolean allownull) throws IOException {
        while (true) {
            try {
                String in = reader.readLine();
                if (allownull && in.trim().equals("")){
                    return null;
                }
                return Float.parseFloat(in.trim().split(" ")[0]);
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
            System.out.print(" (max " + ceiling+ ")> ");
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
            System.out.print(" (min " + floor + ")> ");
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
            System.out.print(" (" + floor + ", " + ceiling + ")> ");
            while(true) {
                Integer in = this.inputInteger(allownull);
                if (allownull && in == null) {
                    return null;
                }
                if (in >= floor && in <= ceiling) {
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

    private Float inputFloatRange(String prompt, Float floor, Float ceiling) throws IOException{
        return this.inputFloatRange(prompt, floor, ceiling, false);
    }
    private Float inputFloatRange(String prompt, Float floor, Float ceiling, boolean allownull) throws IOException {
        assert(floor != null || ceiling != null);
        if (floor != null && ceiling != null) {
            assert(!floor.equals(ceiling));
        }

        System.out.print(prompt);
        if (allownull) {
            System.out.print(" (leave blank to skip)");
        }
        if (floor == null) {
            System.out.print(" (max " + ceiling+ ")> ");
            while(true) {
                Float in = this.inputFloat(allownull);
                if (allownull && in == null) {
                    return null;
                }
                if (in <= ceiling) {
                    return in;
                }
                System.out.println("Please enter a number below or equal to " + ceiling + ".");
            }
        } else if (ceiling == null) {
            System.out.print(" (min " + floor + ")> ");
            while(true) {
                Float in = this.inputFloat(allownull);
                if (allownull && in == null) {
                    return null;
                }
                if (in >= floor) {
                    return in;
                }
                System.out.println("Please enter a number above or equal to " + floor + ".");
            }
        } else {
            System.out.print(" (" + floor + ", " + ceiling + ")> ");
            while(true) {
                Float in = this.inputFloat(allownull);
                if (allownull && in == null) {
                    return null;
                }
                if (in >= floor && in <= ceiling) {
                    return in;
                }
                System.out.println("Please enter a number between or equal to " + floor + " and " + ceiling + ".");
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
        return inputVarighet(0);
    }

    private int inputVarighet(int limit) throws IOException {
        while(true){
            if (limit != 0) {
                System.out.print("Enter Varighet (max " + toVarighet(limit) + ")> ");
            } else {
                System.out.print("Enter Varighet (0h 0m 0s)> ");
            }

            String in = reader.readLine();
            Scanner scan = new Scanner(in);
            int hours = 0, minutes = 0, seconds = 0;
            if(scan.hasNext("\\d+[h]")){
                String num = scan.next("\\d+[h]");
                hours = Integer.parseInt(num.substring(0, num.length()-1));
                if (hours < 0) {
                    System.out.println("Please enter a valid Varighet.");
                    scan.close();
                    continue;
                }
            }
            if(scan.hasNext("\\d+[m]")){
                String num = scan.next("\\d+[m]");
                minutes = Integer.parseInt(num.substring(0, num.length()-1));
                if (minutes < 0) {
                    System.out.println("Please enter a valid Varighet.");
                    scan.close();
                    continue;
                }

            }
            if(scan.hasNext("\\d+[s]")){
                String num = scan.next("\\d+[s]");
                seconds = Integer.parseInt(num.substring(0, num.length()-1));
                if (seconds < 0) {
                    System.out.println("Please enter a valid Varighet.");
                    scan.close();
                    continue;
                }
            }
            scan.close();

            if (hours + minutes + seconds > 0) {
                int time = hours * 60 * 60 + minutes * 60 + seconds;
                if (limit == 0 || time <= limit) {
                    if (confirmPrompt("Parsed as " + this.toVarighet(time) + ". Is this correct?")) {
                        return time;
                    }
                } else {
                        System.out.println("Please enter a Varighet below " + this.toVarighet(limit) + ".");
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
            System.out.print("Enter the name of an Øvelse"); // oh shit, it's på norsk
            if (allownull) {
                System.out.print(" (enter q to cancel)");
            }
            System.out.print("> ");
            String in = reader.readLine().trim().split(" ")[0].toLowerCase();
            if (allownull && in.equals("q")) {
                return null;
            }
            ResultSet res = db.getØvelserByName(in);
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

    private void toGjennomføringer(ArrayList<Øvelse> øvelser) throws IOException {
        for(int i = 0; i < øvelser.size(); i++) {
            Øvelse øvelse = øvelser.get(i);
            if (øvelse.lengde == null) { // Kondisjonsøvelse
                Short in = inputShortRange("How many repetitions did you do of " + øvelse.navn +
                        "? (default, " + øvelse.repetisjoner + ")", 1, null, true);
                if (in != null) {
                    øvelse.repetisjoner = in;
                }

                in = inputShortRange("How many sets did you do of " + øvelse.navn +
                        "? (default " + øvelse.sett + ")", 1, null, true);

                if (in != null) {
                    øvelse.sett = in;
                }

                øvelser.set(i, øvelse);
            } else { // Utholdenhetsøvelse
                Integer in = inputRange("What distance did you do " + øvelse.navn +
                        " in metres? (default " + øvelse.lengde + ")", 1, null, true);
                if (in != null) {
                    øvelse.lengde = in;
                    øvelser.set(i, øvelse);
                }
            }
        }
    }

    private Geodata inputGeodata(int max_varighet) throws IOException{
        Geodata data = new Geodata();
        System.out.println("Creating new geodata.");
        System.out.println("First, enter how long into your session you took this reading.");
        data.tid = (long) inputVarighet(max_varighet);
        data.puls = inputShortRange("Enter pulse", 0, null, true);
        data.lengdegrad = inputFloatRange("Enter longitude", 0f, null, true);
        data.breddegrad = inputFloatRange("Enter latitude", 0f, null, true);
        data.moh = inputShortRange("Enter altitude", 0, null, true);
        return data;
    }

    class Geodata{
        public Integer geodataid;
        public Integer treningsøktid;
        public Long tid;
        public Short puls;
        public Float lengdegrad;
        public Float breddegrad;
        public Short moh;

        Geodata() {}

        /*Geodata(int max_varighet) throws IOException {
            System.out.println("Creating new geodata.");
            System.out.println("First, enter how long into your session you took this reading.");
            this.tid = (long) inputVarighet(max_varighet);
            this.puls = inputShortRange("Enter pulse", 0, null, true);
            this.lengdegrad = inputFloatRange("Enter longitude", 0f, null, true);
            this.breddegrad = inputFloatRange("Enter latitude", 0f, null, true);
            this.moh = inputShortRange("Enter altitude", 0, null, true);
        }*/

        // Goddamnit Java
        private long getTid() {
            return this.tid;
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
        Øvelse(int øvelseid) throws SQLException {//use the Øvelse as a template
            this(db.getØvelse(øvelseid), null);
        }
        Øvelse(Integer øvelseid, int øvelsegjennomføringid) throws SQLException {//use the Øvelse as a template
            this((øvelseid!=null)? db.getØvelse(øvelseid) : null, db.getØvingsgjennomføring(øvelsegjennomføringid));
        }
        Øvelse(ResultSet øvelsers, ResultSet øvelsegjennomføringrs) throws SQLException{//use the Øvelse as a template
            if (øvelsegjennomføringrs != null) {
                if (øvelsegjennomføringrs.next()){
                    lengde = øvelsegjennomføringrs.getInt("Lengde");
                    if (øvelsegjennomføringrs.wasNull()){lengde=null;}
                    repetisjoner = øvelsegjennomføringrs.getShort("Repetisjoner");
                    if (øvelsegjennomføringrs.wasNull()){repetisjoner=null;}
                    sett = øvelsegjennomføringrs.getShort("Sett");
                    if (øvelsegjennomføringrs.wasNull()){sett=null;}
                    
                    if (øvelsers == null){
                        øvelsers = db.getØvelse(øvelsegjennomføringrs.getInt("ØvelseID"));
                    }
                }
            }
            if (øvelsers != null){
                if (øvelsers.isFirst() || øvelsers.next()) {
                    øvelseid = øvelsers.getInt("ØvelseID");
                    navn = øvelsers.getString("Navn");
                    beskrivelse = øvelsers.getString("Beskrivelse");
                    belastning = øvelsers.getInt("Belastning");
                    if (øvelsegjennomføringrs == null){
                        lengde = øvelsers.getInt("Lengde");
                        if (øvelsers.wasNull()){lengde=null;}
                        repetisjoner = øvelsers.getShort("Repetisjoner");
                        if (øvelsers.wasNull()){repetisjoner=null;}
                        sett = øvelsers.getShort("Sett");
                        if (øvelsers.wasNull()){sett=null;}
                    }
                }
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
            this.geodatapunkter = new ArrayList<>();
            this.temperatur = null;
            this.værtype = null;
            this.måldenne = null;
            this.målneste = null;
            this.notat = null;
        }
        Treningsøkt(int treningsøktid) throws SQLException {
            this(db.getTreningsøkt(treningsøktid));
        }
        Treningsøkt(ResultSet rs) throws SQLException{
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
                    Øvelse ø = new Øvelse(null, ørs);
                    øvelser.add(ø);
                }

                this.notat = db.getNotat(this.treningsøktid);
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
        økt.print();
        while(true){
            if (!this.confirmPrompt("Do you want to add another øvelse?")){
                break;
            }
            ResultSet øvelse = this.searchØvelse(true);
            if (øvelse != null) {
                økt.øvelser.add(new Øvelse(øvelse, null));
                økt.print();
            }
        }
        System.out.println();
        this.toGjennomføringer(økt.øvelser);
        System.out.println();

        if (confirmPrompt("Do you want to add geodata to this session?")){
            økt.geodatapunkter.add(this.inputGeodata(økt.varighet));
        }
        while(true){
            System.out.println();
            if (!this.confirmPrompt("Do you want to add more geodata?")){
                break;
            }
            økt.geodatapunkter.add(this.inputGeodata(økt.varighet));
        }
        økt.geodatapunkter.sort(Comparator.comparing(Geodata::getTid));


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