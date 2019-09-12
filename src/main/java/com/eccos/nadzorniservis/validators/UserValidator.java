package com.eccos.nadzorniservis.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserValidator {
    
    public UserValidator() {
    }
    
    /**
     * Metoda provjerava ispravan format lozinke
     * lozinka mora sadrzavati minimalno 8 znakova od koji barem jedan treba biti veliko slovo, jedan malo i jedan broj
     * @param password
     */
    public boolean checkPasswordFormat(String password) {
        String regPassword = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}";
        
        Pattern pattern = Pattern.compile(regPassword);
        Matcher m = pattern.matcher(password);
        boolean status = m.matches();

        if (status) {
            return true;
        } 
        else {
            return false;
        }
    }
    
    /**
     * Metoda provjerava format korisnickog imena
     * korisnicko ime mora sadrzavati najmanje 6 znakova bez razmaka
     * @param username
     * @return
     */
    public boolean checkUsernameFormat(String username) {
        String regUsername = "[a-zA-Z0-9.\\\\]{6,}";
                
        Pattern pattern = Pattern.compile(regUsername);
        Matcher m = pattern.matcher(username);
        boolean status = m.matches();

        if (status) {
            return true;
        } 
        else {
            return false;
        }
    }
}
