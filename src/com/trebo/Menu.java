package com.trebo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Menu{
    private Database db;

    public Menu(Database db) {
        this.db = db;
    }

    private boolean confirmPrompt(String prompt){
        Scanner scan = new Scanner(System.in);
        while(true) {
            System.out.print(prompt.concat(" [Y/n]: "));
            String in = scan.next().toLowerCase();
            switch(in) {
                case "":
                case "y":
                case "yes":
                    scan.close();
                    return true;
                case "n":
                case "no":
                    scan.close();
                    return false;
                default:
                    System.out.println("Please enter yes or no.");
                    scan.nextLine();
                    break;
            }
        }
    }

    private Integer choicePrompt(Map<String, Integer> choiceMap){
        Scanner scan = new Scanner(System.in);
        Vector<String> choices = new Vector<>();
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
                int in = scan.nextInt();
                if (in == 0) {
                    return -2;
                } else if (in == 9 && choiceMap.size() == 8) {
                    return -1;
                } else if (in < 0 || in > choices.size()) {
                    System.out.println("Please enter a number corresponding to the above options.");
                } else {
                    return choiceMap.get(choices.get(in - 1));
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
            }
        }

    }

    private int searchØvelse() throws SQLException {
        Scanner scan = new Scanner(System.in);
        while(true) {
            if (scan.hasNextLine()){scan.nextLine();}
            String in = scan.next();
            ResultSet res = db.getØvelseByName(in);
            if(!res.isBeforeFirst()){ // is empty
                System.out.println("Could not find Øvelse with that name.");
            }
            else if(res.next() && res.isLast()){ // is length 1
                res.beforeFirst();
                if(this.confirmPrompt("Do you want to use " + res.getString("Navn") + "?")){
                    scan.close();
                    return res.getInt("ØvelseID");
                }
            } else {
                res.previous();
                Map<String, Integer> øvelser = new LinkedHashMap<>(8);
                    while (!res.isAfterLast()) {
                        øvelser.put(res.getString("Navn"), res.getInt("ØvelseID"));
                        if (øvelser.size() == 8) {
                             int øvelse = this.choicePrompt(øvelser);
                             if(øvelse == -1) {
                                 øvelser.clear();
                             } else if(øvelse == -2) {
                                 break;
                             }
                        }
                    }
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
    public void run(){
        confirmPrompt("asdasdas");
    }
}