package com.eccos.nadzorniservis.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="user")
public class UserModel {
    
    public static final String userRole = "ROLE_USER";
    public static final String adminRole = "ROLE_ADMIN";
    
    @Id
    @GeneratedValue
    @Column(name="id_user")
    private int idUser;
    
    @Column(name="username", nullable=false, unique=true)
    private String username;
    
    @Column(name="password")
    private String password;
    
    @Column(name="role")
    private String role;
    
    public UserModel() {
        
    }
    
    public UserModel(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public UserModel(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    
    public int getIdUser() {
        return idUser;
    }

    
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    

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

    
    public String getRole() {
        return role;
    }

    
    public void setRole(String role) {
        this.role = role;
    } 
}
