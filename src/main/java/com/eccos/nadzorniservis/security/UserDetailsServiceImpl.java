package com.eccos.nadzorniservis.security;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eccos.nadzorniservis.models.UserModel;
import com.eccos.nadzorniservis.services.UserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    UserService userService;
   
    
    private static final Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);

    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userService.findByUsername(username);
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (user == null) {
            //return new org.springframework.security.core.userdetails.User("", "", grantedAuthorities);
            throw new UsernameNotFoundException("not found");
        } 
        else {
            postaviPravo(user, grantedAuthorities);
            System.out.println("Korisnik - " + user.getUsername() + " " + user.getPassword() + " " + grantedAuthorities);
            logger.info("Korisnik - " + user.getUsername() + " " + user.getPassword() + " " + grantedAuthorities);

            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
        }
    }
    
    
    private void postaviPravo(UserModel user, ArrayList<GrantedAuthority> list) {
        if (!user.getRole().isEmpty()) {
            if (user.getRole().equals(UserModel.adminRole)) {
                list.add(new SimpleGrantedAuthority(UserModel.adminRole));
                list.add(new SimpleGrantedAuthority(UserModel.userRole));
            }
            else if (user.getRole().equals(UserModel.userRole)) {
                list.add(new SimpleGrantedAuthority(UserModel.userRole));
            }
        }
    }

}
