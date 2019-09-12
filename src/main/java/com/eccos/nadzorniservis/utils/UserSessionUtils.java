package com.eccos.nadzorniservis.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class UserSessionUtils {
    
    /**
     * Metoda za dohvacanje trenutno prijavljenog korisnika
     * @return
     */
    public static String getUsernameUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
