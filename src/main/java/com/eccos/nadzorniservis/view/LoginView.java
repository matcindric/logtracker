package com.eccos.nadzorniservis.view;


import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.eccos.nadzorniservis.services.UserService;

@Scope("session")
@Named
public class LoginView {
    
    private String username;
    private String password;
    
    @Autowired
    UserService userService;
    
    public LoginView() {

    }
    
    public String login() {
        /*System.out.println("Yes");
        Korisnik korisnik = korisnikService.findByKorisnickoIme(KorisnikSessionUtils.dohvatiKorisnickoImeKorisnika());
        if (!korisnik.getPravo().equals(Korisnik.adminPravo))
            return "iznimke.xhtml";
        return "home.xhtml";*/
        return "";
    }
    
    public String logout() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        new SecurityContextLogoutHandler().logout(request, null, null);
        return "/login.xhtml?faces-redirect=true";
    }
    
    //@GetMapping("/logout")
    /*public String odjavi(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            new SecurityContextLogoutHandler().logout(httpServletRequest,httpServletResponse,authentication);
        }
        return "login.xhtml";
    }*/
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }    

}
