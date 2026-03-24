package org.example;

import org.example.authentication.*;


public class Main {
    public static void main ( String[] args ) {
        UI ui = new UI();
        User user;
        while (true){
            if((user = ui.authenticateUser()) == null) break;
            if (ui.functions(user)) {
                break;
            }
        }
    }
}