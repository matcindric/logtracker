package com.eccos.nadzorniservis.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eccos.nadzorniservis.models.ExceptionFile;
import com.eccos.nadzorniservis.models.UserModel;
import com.eccos.nadzorniservis.services.ExceptionFileService;
import com.eccos.nadzorniservis.services.UserService;
import com.eccos.nadzorniservis.utils.UserSessionUtils;
import com.eccos.nadzorniservis.validators.UserValidator;

@Scope("session")
@Named
public class UserView {
    
    private List<UserModel> users;
    private UserModel oznaceniKorisnik;
    
    private String username;
    private String password;
    private String newPassword;
    private String repeatedPassword;
    
    private boolean dialogOpened;
    
    @Autowired
    UserService userService;
    
    @Autowired
    ExceptionFileService exceptionFileService;
    
    UserValidator validator = new UserValidator();
    
    public UserView() {
        
    }
    
    @PostConstruct
    public void init() {
       this.users = userService.findAllByRole(UserModel.userRole);   
    }
    
    public void openDialog() {
        dialogOpened = true;
        PrimeFaces current = PrimeFaces.current();
        current.executeScript("PF('novaLozinkaDialog').show();");
    }
    
    public void closeDialog() {
        dialogOpened = false;
        this.newPassword = "";
        this.repeatedPassword = "";
        PrimeFaces current = PrimeFaces.current();
        current.executeScript("PF('novaLozinkaDialog').hide();");
    }
    
    public void saveUser() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserModel user = new UserModel();
        if (userService.findByUsername(username) != null) {
            System.out.println("Vec postoji korisnik s tim korisnickim imenom");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška!", "Korisničko ime " + username + " već postoji u bazi"));
        }
        else {
            if (validator.checkUsernameFormat(username)) {
                if (validator.checkPasswordFormat(password)) {
                    if (password.equals(repeatedPassword)) {
                        user.setUsername(username);
                        user.setPassword(encoder.encode(password));
                        user.setRole(UserModel.userRole);
                        Set<ExceptionFile> exceptions = new HashSet<ExceptionFile>(exceptionFileService.findAll());
                        user.setExceptions(exceptions);
                        userService.save(user);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Korisnik " + username + " je uspješno kreiran!"));
                        clear();  
                    }
                    else {
                        System.out.println("Nova i ponovljena lozinka nisu iste");
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška!", "Nova lozinka i ponovljena lozinka nisu jednake!"));
                    }                                     
                }
                else {
                    //FacesContext.getCurrentInstance().addMessage("lozinka", 
                      //      new FacesMessage("", "Lozinka nije u ispravnom formatu. Treba sadržavati minimalno 8 znakova i imati barem jedno malo, jedno veliko slovo i jedan broj!"));
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška!", "Lozinka nije u ispravnom formatu. Treba sadržavati minimalno 8 znakova i imati barem jedno malo, jedno veliko slovo i jedan broj!"));
                }
            }
            else {
                //FacesContext.getCurrentInstance().addMessage("korime", 
                  //      new FacesMessage("", "Korisničko ime nije u ispravnom formatu. Mora imati minimlano 6 znakova i ne smije sadržavati razmake!"));
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška!", "Korisničko ime nije u ispravnom formatu. Mora imati minimlano 6 znakova i ne smije sadržavati razmake!"));
            }           
        }        
    }
    
    public void updatePassword() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserModel user = new UserModel();
        //String lozinkaBaza = korisnikService.findByKorisnickoIme(KorisnikSessionUtils.dohvatiKorisnickoImeKorisnika()).getLozinka();
        //System.out.println(korisnikService.findByKorisnickoIme(KorisnikSessionUtils.dohvatiKorisnickoImeKorisnika()).getIdKorisnik());
        
        //if (encoder.matches(lozinka, lozinkaBaza)) {
            if (validator.checkPasswordFormat(newPassword)) {
                if (newPassword.equals(repeatedPassword)) {
                    user.setIdUser(userService.findByUsername(UserSessionUtils.getUsernameUser()).getIdUser());
                    user.setUsername(userService.findByUsername(UserSessionUtils.getUsernameUser()).getUsername());
                    user.setPassword(encoder.encode(newPassword));
                    user.setRole(userService.findByUsername(UserSessionUtils.getUsernameUser()).getRole());
                    userService.save(user);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Lozinka je uspješno promijenjena!"));
                }
                else {
                    System.out.println("Nova i ponovljena lozinka nisu iste");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška!", "Nova lozinka i ponovljena lozinka nisu jednake!"));
                }
            }
            else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "", "Nova lozinka nije u ispravnom formatu. Treba sadržavati minimalno 8 znakova i imati barem jedno malo, jedno veliko slovo i jedan broj!"));
                //FacesContext.getCurrentInstance().addMessage("validacijaLozinka", 
                  //      new FacesMessage("", "Lozinka nije u ispravnom formatu. Treba sadržavati minimalno 8 znakova i imati barem jedno malo, jedno veliko slovo i jedan broj!"));
            }       
        /*}
        else {
            System.out.println("Stara lozinka nije tocna");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška!", "Stara lozinka nije ispravna!"));
        }*/
    }
    
    public void updateUserPassword(int idUser, String username) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserModel user = new UserModel();
        if (validator.checkPasswordFormat(newPassword)) {
            if (newPassword.equals(repeatedPassword)) {
                user.setIdUser(idUser);
                user.setUsername(username);
                user.setPassword(encoder.encode(newPassword));
                user.setRole(UserModel.userRole);
                userService.save(user);
                System.out.println("Lozinka promijenjena za " + idUser + " - " + username);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Lozinka je uspješno promijenjena!"));

                //zatvoriDialog();
            } else {
                System.out.println("Nova i ponovljena lozinka nisu iste");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Pogreška!", "Nova lozinka i ponovljena lozinka nisu jednake!"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška!",
                    "Nova lozinka nije u ispravnom formatu. Treba sadržavati minimalno 8 znakova i imati barem jedno malo, jedno veliko slovo i jedan broj!"));
            // FacesContext.getCurrentInstance().addMessage("validacijaLozinka",
            // new FacesMessage("", "Lozinka nije u ispravnom formatu. Treba sadržavati minimalno 8 znakova i imati barem jedno malo, jedno veliko slovo i jedan broj!"));
        }
        
    }
    
    
    private void clear() {
        setUsername(null);
        setPassword(null);
    }
    
    
    public List<UserModel> getUsers() {
        users = userService.findAllByRole(UserModel.userRole);
        return users;
    }
    
    
    public void setUsers(List<UserModel> users) {
        this.users = users;
    }
    
    
    public UserModel getOznaceniKorisnik() {
        return oznaceniKorisnik;
    }

    
    public void setOznaceniKorisnik(UserModel oznaceniKorisnik) {
        this.oznaceniKorisnik = oznaceniKorisnik;
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

 
    public String getNewPassword() {
        return newPassword;
    }

    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    
    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    
    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }

    
    public boolean isDialogOpened() {
        return dialogOpened;
    }

    
    public void setDialogOpened(boolean dialogOpened) {
        this.dialogOpened = dialogOpened;
    }
    
}
