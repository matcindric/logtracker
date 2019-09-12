package com.eccos.nadzorniservis;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.eccos.nadzorniservis.models.UserModel;
import com.eccos.nadzorniservis.services.UserService;

@Component
public class AdminStartup {
    
    @Autowired
    UserService userService;
    
    @PostConstruct
    public void init() {
       adminCreate();
    }
    
    private void adminCreate() {        
        if (userService.findAll().isEmpty()) { //kod prvog prvog pokretanja app kreiraj admina (kod restarta admin se nece vise kreirati, ostaje isti -> zbog perzistentnosti)
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            UserModel admin = new UserModel("admin", encoder.encode("admin"), UserModel.adminRole);
            userService.save(admin);
        }        
    }
}
